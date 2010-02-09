/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */
package System;

/**
 * Helper class to parse gpx files into something usable.
 * GPX-files are cache data defined in an xml-schema.
 */
public abstract class GPX {
    
    /**
     * Extract value of attribute "name" from "tag"
     * @param tag
     * @param name
     * @return value of attribute, or null if not found
     */
    private static String tagAttribute(String tag, String name) {
        int start = tag.indexOf(name);
        int a = tag.indexOf('"', start)+1;
        int b = tag.indexOf('"', a+1);
        if (start < 0 || a < 1 || b < 0) return null;
        System.out.println("tagAttribute: "+ tag.substring(a, b));
        return tag.substring(a, b);
    }

    /**
     * get first start tag in string xml, useful in conjunction with
     * tagAttribute.
     * @param xml is a string with xml markup.
     * @param tag is the name of the tag to return
     * @return the first tag named tag.
     */
    private static String startTag(String xml, String tag) {
        int a = xml.indexOf('<'+tag);
        int b = xml.indexOf('>', a);
        System.out.println("startTag: "+ xml.substring(a, b+1));
        return xml.substring(a, b+1);
    }

    /**
     * Parses an xml-document formatted as defined in
     * http://www.topografix.com/GPX/1/0/gpx.xsd
     * http://www.topografix.com/GPX/1/1/gpx.xsd
     * Full accuracy is not guaranteed as the parser takes some shortcuts.
     *
     * @param gpx is a gpx-document in a string.
     * @return an array of all caches in the file.
     */
    public static Cache[] parse(String gpx) {
        // Find first entry, surrounded by <wpt></wpt>
        // Sample: <wpt lat="57.32088" lon="13.54707">
        int entryStart;
        int entryEnd = 0;
        double lat, lon;
        /* while entryStart... (for now we decode the first cache only)
         * contain try-except clause in while loop, that way only single caches
         * are dropped instead of remaining gpx data.
         */
        try {
            Cache cache = new Cache();

            entryStart = gpx.indexOf("<wpt", entryEnd);
            entryEnd = gpx.indexOf("</wpt>", entryStart);
            String wayPoint = gpx.substring(entryStart, entryEnd);

            String tag = startTag(wayPoint, "wpt");
            System.out.println("tag = "+tag);

            lat = Double.parseDouble(tagAttribute(tag, "lat"));
            lon = Double.parseDouble(tagAttribute(tag, "lon"));
            cache.position = new Position(lat, lon);

            // add cache to cache array here if no exceptions were triggered
        } catch (NullPointerException e) {
        } catch (StringIndexOutOfBoundsException ee) {
            // whatever, the cache got dropped, fail silently
            System.out.println("ToManyExceptionsInJavaException");
        }
        return null;
    }

}
