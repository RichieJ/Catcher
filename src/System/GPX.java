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
     * @param tag is the tag containing the attribute.
     * @param name is the attribute whose value to return.
     * @return value of attribute, or null if not found.
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
     * Evaluate an attribute value as true or false.
     * @param tag: the tag whose attribute to evaluate.
     * @param name: the attribute to evaluate.
     * @return true if the contents of the attribute is "True".
     */
    private static boolean tagAttributeBool(String tag, String name) {
        if (tagAttribute(tag, name).compareTo("True") == 0) {
            return true;
        }
        return false;
    }

    /**
     * get first start tag in string xml, useful in conjunction with
     * tagAttribute.
     * @param xml is a string with xml markup.
     * @param tag is the name of the tag to return.
     * @return the first tag named tag.
     */
    private static String startTag(String xml, String tag) {
        int a = xml.indexOf('<'+tag);
        int b = xml.indexOf('>', a);
        System.out.println("startTag: "+ xml.substring(a, b+1));
        return xml.substring(a, b+1);
    }

    /**
     * Get the contents between a start and end tag.
     * @param xml is a string with xml markup.
     * @param tag is the name of the tag whose contents to return.
     * @return the contents surrounded by the tag.
     */
    private static String tagContents(String xml, String tag) {
        int a = xml.indexOf('>', xml.indexOf('<'+tag))+1;
        int b = xml.indexOf('<', a);
        System.out.println("tagContents ("+tag+"): "+ xml.substring(a, b));
        return xml.substring(a, b);
    }

    private static String stripHTML(String html) {
        System.out.println("stripHTML not impl");
        return html;
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

            // Get next wpt tag
            entryStart = gpx.indexOf("<wpt", entryEnd);
            entryEnd = gpx.indexOf("</wpt>", entryStart);
            String wpt = gpx.substring(entryStart, entryEnd);

            String tag = startTag(wpt, "wpt");
            System.out.println("tag = "+tag);

            lat = Double.parseDouble(tagAttribute(tag, "lat"));
            lon = Double.parseDouble(tagAttribute(tag, "lon"));
            Position position = new Position(lat, lon);

            String name = tagContents(wpt, "name");
            String type = tagContents(wpt, "type");

            if (type.compareTo("Geocache") == 0) {
                System.out.println("GPX.parse: contains a cache");
                String gs = "groundspeak:";
                tag = startTag(wpt, gs+"cache");
                boolean available = tagAttributeBool(tag, "available");
                int cacheType = Cache.stringToType(tagContents(wpt, gs+"type"));
                String placedBy = tagContents(wpt, gs+"placed_by");
                String difficulty = tagContents(wpt, gs+"difficulty");
                String terrain = tagContents(wpt, gs+"terrain");
                String container = tagContents(wpt, gs+"container");
                String cachetype = tagContents(wpt, gs+"type");
                String shortDesc = tagContents(wpt, gs+"short_description");
                if (tagAttributeBool(
                        startTag(wpt, gs+"short_description"), "html")) {
                    shortDesc = stripHTML(shortDesc);
                }
                String longDesc = tagContents(wpt, gs+"long_description");
                if (tagAttributeBool(
                        startTag(wpt, gs+"long_description"), "html")) {
                    longDesc = stripHTML(longDesc);
                }
                String hint = tagContents(wpt, gs+"encoded_hints");
            }


            
            // add cache to cache array here if no exceptions were triggered
        } catch (Exception e) {
            System.out.println("GPX Exception: "
                    + e.getMessage());
        }
        return null;
    }

}
