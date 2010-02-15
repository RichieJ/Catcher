/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */
package MIDP;

import GUI.ICacheView;
import GUI.IViewNavigator;
import System.Cache;
import System.CacheLog;
import System.GPX;
import System.Position;
import javax.microedition.lcdui.Graphics;

public class CacheView extends CatcherCanvas implements ICacheView {

    private Cache cache;


    // Careful when changing globalItems! menuAction() assumes a certain order!
    private String[] viewItems = {"Cache list", "Set nearest", "Show hint"};
    private int page=0;
    private static final int PAGE_DESC=0;
    private static final int PAGE_LOGS=1;
    private TextBox textBox = new TextBox();

    public CacheView(IViewNavigator viewNavigator, ViewResources viewResources) {
        super(viewNavigator);

        setFullScreenMode(true);
        this.viewResources = viewResources;
        initFakeCache(); // Placeholder
        textBox.setText(cache.fullDesc());
    }

    // This is just a placeholder
    private void initFakeCache() {

        // temp debug code start

        // fake gpx waypoint
String s = 
"<wpt lat=\"58.23347\" lon=\"14.63824\">\n"+
"<time>2003-12-13T00:00:00</time>\n"+
"<name>OS0000</name>\n"+
"<desc>Cachename by placedby, Traditional Cache (2/1.5)</desc>\n"+
"<url>http://www.opencaching.se/viewcache.php?cacheid=0</url>\n"+
"<urlname>Cachename by placedby, Traditional Cache</urlname>\n"+
"<sym>Geocache</sym>\n"+
"<type>Geocache</type>\n"+
"<groundspeak:cache id=\"0\" available=\"True\" archived=\"False\" xmlns:groundspeak=\"http://www.groundspeak.com/cache/1/0\">\n"+
"  <groundspeak:name>Cachename</groundspeak:name>\n"+
"  <groundspeak:placed_by>placedby</groundspeak:placed_by>\n"+
"  <groundspeak:owner id=\"0\">owner</groundspeak:owner>\n"+
"  <groundspeak:type>Traditional Cache</groundspeak:type>\n"+
"  <groundspeak:container>Regular</groundspeak:container>\n"+
"  <groundspeak:attributes>\n"+
"    <groundspeak:attribute id=\"25\" inc=\"1\">Parkering i närheten</groundspeak:attribute>\n"+
"    <groundspeak:attribute id=\"26\" inc=\"1\">Nåbar med allmänna kommunikationer</groundspeak:attribute>\n"+
"  </groundspeak:attributes>\n"+
"  <groundspeak:difficulty>2</groundspeak:difficulty>\n"+
"  <groundspeak:terrain>1.5</groundspeak:terrain>\n"+
"  <groundspeak:country>Sweden</groundspeak:country>\n"+
"  <groundspeak:state></groundspeak:state>\n"+
"  <groundspeak:short_description html=\"False\">short desc.</groundspeak:short_description>\n"+
"  <groundspeak:long_description html=\"True\">Long desc\n"+
"&lt;br&gt;</groundspeak:long_description>\n"+
"  <groundspeak:encoded_hints>Under sten</groundspeak:encoded_hints>\n"+
"  <groundspeak:logs>\n"+
"    <groundspeak:log id=\"0\">\n"+
"      <groundspeak:date>2010-03-15T00:00:00</groundspeak:date>\n"+
"      <groundspeak:type>Found it</groundspeak:type>\n"+
"      <groundspeak:finder id=\"0\">finding_user</groundspeak:finder>\n"+
"      <groundspeak:text encoded=\"False\">Tack för cachen.</groundspeak:text>\n"+
"    </groundspeak:log>\n"+
"  </groundspeak:logs>\n"+
"</groundspeak:cache>\n"+
"</wpt>";
        GPX.parse(s);
        // temp debug code end

        if (cache == null) {
            cache = new Cache();
        }
        cache.code="OC3G4F5";
        cache.difficulty=0;
        cache.terrain=0;
        cache.setType("Trad");
        cache.shortDesc="This is the summary of the cache.";
        cache.longDesc = "This is the long description.";
        cache.hint="Under sten";
        CacheLog[] logs = new CacheLog[2];
        logs[0] = new CacheLog("", "", "joser1", "TFTC!\n");
        logs[1] = new CacheLog("", "", "joser2", "10x for this one. It had me searching for ages until I finally spotted it.\ndropped tb");
        cache.logs = logs;
        cache.name="Catcher fake cache placeholder";
        cache.position= new Position(57.1, 14.8);
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
        page = 0;
        textBox.setText(cache.fullDesc());
    }

    private int getHeading(Position p) {
        return 0;
    }

    private int paintCacheAttributes(Graphics g) {
        int ht = sysFont.getHeight();
        int x = 0;
        int y = 20;
        int width = getWidth();
        int tl = Graphics.TOP|Graphics.LEFT;
        int height = (16>sysFont.getHeight()? 16 : sysFont.getHeight());

        paintCache(g, x, y, width, height, getHeading(cache.position), cache);
        y += sysFont.getHeight();
        return y;
    }

    public void paintView(Graphics g) {
        g.setColor(COLOR_BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight()-HEIGHT_STATUSBAR);
        int y = paintCacheAttributes(g);
        textBox.paint(g, 0, y, getWidth(), getHeight()-y);
    }

    private void nextPage() {
        ++page;
        switch(page) {
            case 1:
                String s = "";
                for (int i = 0; i<cache.logs.length; i++) {
                    s += cache.logs[i].name+": "+cache.logs[i].log+'\n';
                }
                textBox.setText(s);
                break;
            default:
                page=0;
                textBox.setText(cache.fullDesc());
        }
    }
    
    /**
     * Called when a menu item has been selected
     * @param menuItem: index in view menu array
     */
    void menuActionView(int menuItem) {
        switch(menuItem) {
            case 0:
                // Cache list
                System.out.println("menu Cache list (not impl)");
                break;
            case 1:
                // Set nearest
                System.out.println("menu Set nearest (not impl)");
                break;
            case 2:
                // Show hint
                System.out.println("menu Show hint (not impl)");
                break;
        }
    }

    /**
     * Called when a key is pressed.
     */
    protected  void keyPressedView(int keyCode) {
        switch(getGameAction(keyCode)) {
            case DOWN:
                textBox.scrollDown();
                repaint();
                break;
            case UP:
                textBox.scrollUp();
                repaint();
                break;
            case FIRE:
                nextPage();
                repaint();
                break;
        }
    }

    public void activate() {
        menu.viewItems(viewItems);
    }

    public void deactivate() {
        menu.clearViewItems();
    }
}