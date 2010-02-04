/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */
package Maps;

import System.IImageLoader;
import System.Position;
import System.IMapProvider;
import System.MathUtil;
import System.StringUtils;

/**
 * Spherical mercator (Google style) tiled map with 256x256px tiles.
 *
 * References:
 * http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
 *
 */
public class MercatorMap implements IMapProvider {
    private IImageLoader imageLoader = null;

    private static final int NOF_CACHED_TILES = 16;

    // We could set this to 0 and see the entire world, but what use is it?
    private static final int ZOOM_MIN = 6;

    // Depending on map source, this value varies. 14 would be safe for most
    // maps. 18 is the closest zoom seen in the wild.
    private static final int ZOOM_MAX = 18;

    // fixme: OSM's mapnik maps are hardcoded, and these don't belong here!
    // fixme: review http://wiki.openstreetmap.org/wiki/Tile_usage_policy
    private static final String mapSource=
            "http://tile.openstreetmap.org/[Z]/[X]/[Y].png";
    private static final String mapID="osm_mapnik";

    // fixme: add format detection
    private static final String mapTileFormat=".png";

    /**
     * Perform some checks to see if a map server source string is properly
     * formatted.
     *
     * @param source is an URL typically in the form
     * http://maps.url/?x=[X]&y=[Y]&z=[Z] or
     * http://maps.url/[Z]/[X]/[Y].png
     *
     * [X], [Y] and [Z] are case sensitive, and must all be in the string
     *
     * An other client uses [INVZ] which in fact is NOT inverted. URLs formatted
     * for that client needs to drop "INV" for those URLs to work with Catcher.
     *
     * @param id is a map source identifier, used as foldername on disk and
     * screen name.
     *
     * @return true if map source is valid.
     * Note that this function does not check if the maps are available or even
     * if the domain exists.
     *
     */
    public boolean isValidMapSource(String source, String id) {
        // We have this input validation here in case the settings file is
        // altered with an external tool. It is assumed the strings are != null.
        if ((source.indexOf("[X]")>0)
                && (source.indexOf("[Y]")>0)
                && (source.indexOf("[Z]")>0)
                && (source.indexOf("http")==0) // Note that https is allowed too
                && (id.length() > 0)) {
            return true;
        }
        return false;
    }

    /**
     * fixme: Find a better way to get the image loader. We don't want to store
     * a reference to it in this class.
     *
     * @param imageLoader is a reference to the image loader responsible for
     * getting map tiles.
     */
    public MercatorMap(IImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    /**
     * Increases the zoom factor if possible.
     * @param zoom is the current zoom factor.
     * @return new zoom factor.
     */
    public int zoomIn(int zoom) {
        return (zoom < ZOOM_MAX? ++zoom:ZOOM_MAX);
    }

    /**
     * Decreases the zoom factor if possible.
     * @param zoom is the current zoom factor.
     * @return new zoom factor.
     */
    public int zoomOut(int zoom) {
        return (zoom > ZOOM_MIN? --zoom:ZOOM_MIN);
    }

    /**
     * Converts a position on a map in pixels to WGS84 coordinates.
     *
     * @param x The horizontal position in pixels on the map.
     * @param y The vertical position in pixels on the map.
     * @param center    Current map center in WGS84 datum.
     * @param mapWidth  Current map width.
     * @param mapHeight Current map height.
     * @param zoom      Current map zoom factor.
     * @return WGS84 coordinates of x and y.
     */
    public Position XYtoPosition(int x, int y, Position center, int mapWidth,
            int mapHeight, int zoom) {
        int[] mapTileX = tileX(center.getLon(), zoom);
        int[] mapTileY = tileY(center.getLat(), zoom);

        int mask = (1 << (zoom+8))-1;
        int xPos = ((mapTileX[0]<<8)+mapTileX[1]-(mapWidth>>1)+x) & mask;
        int yPos = ((mapTileY[0]<<8)+mapTileY[1]-(mapHeight>>1)+y) & mask;
        return new Position(yToLat(yPos, zoom), xToLon(xPos, zoom));
    }

    /**
     * Converts a WGS84 datum to a position on the map in pixels.
     *
     * @param position  The coords that will be translated.
     * @param center    Current map center in WGS84 datum.
     * @param mapWidth  Current map width.
     * @param mapHeight Current map height.
     * @param zoom      Current map zoom.
     * @return Position translated to pixels for the given map.
     */
    public int[] positionToXY(Position position, Position center, int mapWidth,
            int mapHeight, int zoom) {
        int[] mapTileX = tileX(center.getLon(), zoom);
        int[] mapTileY = tileY(center.getLat(), zoom);

        int[] tileX = tileX(position.getLon(), zoom);
        int[] tileY = tileY(position.getLat(), zoom);

        int pX = (tileX[0]<<8)+tileX[1];
        int pY = (tileY[0]<<8)+tileY[1];

        int mapX = (mapTileX[0]<<8)+mapTileX[1];
        int mapY = (mapTileY[0]<<8)+mapTileY[1];

        int retX = pX-mapX+(mapWidth>>1);
        int retY = pY-mapY+(mapHeight>>1);

        int[] ret = {retX, retY};
        return ret;
    }

    /**
     * fixme: Prioritize tile loading order, load from center
     * 
     * This could be done by calculating each tile's center position and
     * ordering by distance to map center. Optimize by offsetting map center
     * position by a half tile instead.
     *
     * @param center    Current map center in WGS84 datum.
     * @param width     Current map width.
     * @param height    Current map height.
     * @param zoom      Current map zoom factor.
     * @return Image of size width x height containing a geographical map.
     * The returned image may be only partially rendered or enirely blank,
     * depending on availability of map tiles.
     */
    public Object getMap(Position center, int width, int height, int zoom) {

        // Get center tile position and calculate the rest from there.
        int[] mapTileX = tileX(center.getLon(), zoom);
        int[] mapTileY = tileY(center.getLat(), zoom);

        // offset in map to center tile's top-left corner.
        int ctx = (width >> 1)-mapTileX[1];
        int cty = (height >> 1)-mapTileY[1];

        int tilesLeft = (ctx+255) >> 8;
        int tilesAbove = (cty+255) >> 8;

        int tilesRight = (width-1-ctx) >> 8;
        int tilesBelow = (height-1-cty) >> 8;

        int nofTilesX = tilesLeft+tilesRight+1;
        int nofTilesY = tilesAbove+tilesBelow+1;

        // offset of topleftmost tile.
        int firstX = ctx-(tilesLeft<<8);
        int firstY = cty-(tilesAbove<<8);
        int firstTileX = mapTileX[0]-tilesLeft;
        int firstTileY = mapTileY[0]-tilesAbove;

        int maxXY = (1 << zoom)-1;
        Object map = imageLoader.createImage(width, height);
        Object imTile = null;
        for (int y=0; y<nofTilesY; y++) {
            // maxXY is n^2-1 and can mask tileX, tileY into range
            int tileY = (firstTileY+y) & maxXY;
            for (int x=0; x<nofTilesX; x++) {
                int tileX = (firstTileX+x) & maxXY;
                imTile = getTile(tileX, tileY, zoom);
                map = imageLoader.drawImage(map, imTile, firstX+(x<<8),
                        firstY+(y<<8));
            }
        }
        return map;
    }

    /**
     * Make an URL for a given tile.
     * @param tileX Tile X number.
     * @param tileY Tile Y number.
     * @param tileZ Zoom level.
     * @return an URL for the given tile.
     */
    private String getTileURL(int tileX, int tileY, int tileZ) {
        String s = new String(mapSource);
        s = StringUtils.replace(s, "[X]", String.valueOf(tileX));
        s = StringUtils.replace(s, "[Y]", String.valueOf(tileY));
        s = StringUtils.replace(s, "[Z]", String.valueOf(tileZ));
        return s;
    }

    /**
     * Returns the path to the local tile.
     * It's the callers responsibility to check if the tile exists.
     *
     * @param tileX Tile X number.
     * @param tileY Tile Y number.
     * @param tileZ Zoom level.
     * @return a file name for the given tile.
     */
    private String getTilePath(int tileX, int tileY, int tileZ) {
        return mapID+"/"+String.valueOf(tileZ)
                +"/"+String.valueOf(tileX)
                +"/"+String.valueOf(tileY)+mapTileFormat;
    }

    /**
     * Get tile from image loader.
     * 
     * @param tileX
     * @param tileY
     * @param tileZ
     * @return image containing the requested tile, or an empty image if the
     * tile is queued for download or not retrievable.
     */
    private Object getTile(int tileX, int tileY, int tileZ) {
        // Local storage path
        String path = getTilePath(tileX, tileY, tileZ);
        // http url
        String url = getTileURL(tileX, tileY, tileZ);
        Object imTile = imageLoader.httpLoad(url, path, NOF_CACHED_TILES);

        return imTile;
    }

    /*
     * Calculate tile x from longitude.
     *
     * @param lon   The WGS84 longitude to convert.
     * @param zoom  The current map zoom factor.
     * @return integer array { tileX, offset in tileX }
     */
    private int[] tileX(double lon, int zoom) {
        int tileX = (int)((lon + 180) / 360 * (1 << (zoom+8)));
        int[] ret = {tileX >> 8, tileX & 0xff};
        return ret;
    }

    /*
     * Calculate tile y from latitude.
     *
     * @param lat
     * @param zoom  The current map zoom factor.
     * @return integer array { tileY, offset in tileY }
     */
    private int[] tileY(double lat, int zoom) {
        lat = lat*Math.PI/180;
        int tileY = (int)((1 - MathUtil.log(Math.tan(lat)+1/Math.cos(lat)) /
                Math.PI) / 2 * (1 << (zoom+8)));

        int[] ret = {tileY >> 8, tileY & 0xff};
        return ret;
    }

    /*
     * Converts an absolute pixel to WGS84 longitude.
     *
     * @param x is the absolute pixel value to convert.
     * @param zoom The current map zoom factor.
     * @return WGS84 longitude for x.
     */
    private double xToLon(int x, int zoom) {
        double dx = (double)x/256;
        return dx / (1 << zoom) * 360 - 180;
    }

    /*
     * Converts an absolute pixel to WGS84 latitude.
     *
     * @param y is the absolute pixel value to convert.
     * @param zoom The current map zoom factor.
     * @return WGS84 latitude for y.
     */
    private double yToLat(int y, int zoom) {
        double dy = (double)y/256;
        double n = Math.PI - 2 * Math.PI * dy / (1 << zoom);
        return 180 / Math.PI * MathUtil.atan(0.5 * (MathUtil.exp(n) -
                MathUtil.exp(-n)));
    }
}
