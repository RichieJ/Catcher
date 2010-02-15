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
 *
 * This class is an ugly mess, makes assumptions and will break on malformed
 * input!!!
 */
public abstract class GPX {
    
    /**
     * Extract value of attribute "name" from "tag"
     * @param tag is the tag containing the attribute.
     * @param name is the attribute whose value to return.
     * @return value of attribute, or null if not found.
     */
    private static String tagAttribute(String tag, String name) {
        int a = tag.indexOf('"', tag.indexOf(name))+1;
        int b = tag.indexOf('"', a+1);
        if (a >= b) return "";
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
        if (a >= b) return "";
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
        if (a >= b) return "";
        System.out.println("tagContents ("+tag+"): "+ xml.substring(a, b));
        return xml.substring(a, b);
    }

    /**
     * Get a tag pair and everything inside them.
     * @param xml is a string with xml markup.
     * @param tag is the name of the tag to return.
     * @return the tag pair and its contents.
     */
    private static String tag(String xml, String tag) {
        int a = xml.indexOf('<'+tag);
        int b = xml.indexOf('>', xml.indexOf("</"+tag));
        System.out.println(String.valueOf(a)+'-'+String.valueOf(b));
        if (a >= b) return "";
        System.out.println("tag ("+tag+"): "+ xml.substring(a, b+1));
        return xml.substring(a, b+1);
    }

    /**
     * Remove html-tags from a string.
     * @param html is the string to strip.
     * @return string with its tags removed.
     */
    private static String stripHTML(String html) {
        html = StringUtils.replaceAll(html, "</p>", "\n");
        String os = "";
        for (int i=0;i<html.length();i++) {
            boolean inTag = false;
            switch (html.charAt(i)) {
                case '<': inTag=true;break;
                case '>': inTag=false;break;
                default:
                    if (!inTag) {
                        os+=html.charAt(i);
                    }
            }
        }
        return os;
    }

    /**
     * Parses log section of a cache.
     * @param cl: The log data
     * @return an array of CacheLog objects.
     */
    private static CacheLog[] parseCacheLogs(String cl) {
        int logCount = StringUtils.subStrCount(cl, "<groundspeak:log ");
        System.out.println(logCount);
        CacheLog[] logs = new CacheLog[logCount];
        int start = 0;
        int end = 0;
        for (int i=0; i<logCount; i++) {
            start = cl.indexOf("<groundspeak:log ", end);
            end = cl.indexOf("</groundspeak:log", start);
            String xml = cl.substring(start, end);
            String type = tagContents(xml, "groundspeak:type");
            String date = tagContents(xml, "groundspeak:date");
            String name = tagContents(xml, "groundspeak:finder");
            String log = tagContents(xml, "groundspeak:text");
            logs[i] = new CacheLog(type, date, name, log);
        }
        return logs;
    }

    private static Cache parseCache(String gc) {
        String gs = "groundspeak:";
        Cache c = new Cache();
        String tag = startTag(gc, gs+"cache");
        c.available = tagAttributeBool(tag, "available");
        c.setType(tagContents(gc, gs+"type"));
        c.placedBy = tagContents(gc, gs+"placed_by");
        c.setDifficulty(tagContents(gc, gs+"difficulty"));
        c.setTerrain(tagContents(gc, gs+"terrain"));
        c.setContainer(tagContents(gc, gs+"container"));
        c.shortDesc = tagContents(gc, gs+"short_description");
        if (tagAttributeBool(
                startTag(gc, gs+"short_description"), "html")) {
            c.shortDesc = stripHTML(c.shortDesc);
        }
        c.longDesc = tagContents(gc, gs+"long_description");
        if (tagAttributeBool(
                startTag(gc, gs+"long_description"), "html")) {
            c.longDesc = stripHTML(c.longDesc);
        }
        c.hint = tagContents(gc, gs+"encoded_hints");

        // wayPoints
        /* Waypoints to the cache are stored as regular waypoints independent
         * of the cache's waypoint, except for the waymarking id.
         * They are also at the end of longDesc, in a table if html=true,
         * else in plain text with line breaks.
         * Search for "Additional Hidden Waypoints" and parse everything after.
         */

        // Logs
        c.logs = parseCacheLogs(tag(gc, "groundspeak:logs"));

        // Attributes
        return c;
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

        boolean last = false;
        while (!last) { //fixme: this is ugly!
            try {
                // Get next wpt tag, break if there are no more waypoints.
                entryStart = gpx.indexOf("<wpt", entryEnd);
                entryEnd = gpx.indexOf("</wpt", entryStart);
                if (entryStart < 0 || entryStart >= entryEnd) {
                    last = true;
                    break;
                }

                String wpt = gpx.substring(entryStart, entryEnd);

                String tag = startTag(wpt, "wpt");
                System.out.println("tag = "+tag);

                lat = Double.parseDouble(tagAttribute(tag, "lat"));
                lon = Double.parseDouble(tagAttribute(tag, "lon"));
                Position position = new Position(lat, lon);

                String name = tagContents(wpt, "name");
                String type = tagContents(wpt, "type");

                if (type.compareTo("Geocache") == 0) {
                    Cache c = parseCache(tag(wpt, "groundspeak:cache"));
                    c.position = position;
                    c.code = name;
                    // cacheStore.add(c);
                } else {
                    // waypoint
                    //wp.name = name;
                    //wp.desc = tagContents(wpt, "desc");
                    //wp.position = position;
                    //wayPointStore.add(wp);
                }
            } catch (Exception e) {
                System.out.println("GPX Exception: "
                        + e.getMessage());
            }
        }
        return null;
    }

}
