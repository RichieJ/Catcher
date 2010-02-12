/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */

package System;


public class Cache {
    public static final int LOG_FOUND = 0;
    public static final int LOG_DNF = 1; // Did Not find
    public static final int LOG_NOTE = 2;
    public static final int LOG_PUBLISHED = 3;
    public static final int LOG_MAINTENANCE = 4;
    public static final int LOG_ARCHIVED = 5;
    public static final int LOG_DISABLED = 6;

    // Cache types. Do not change numbering as they are stored in db!!

    // Unidentified, default type
    public static final int CT_UNDEFINED =0x00;
    
    // Ordinary types
    public static final int CT_TRADITIONAL  =0x01;
    public static final int CT_MYSTERY      =0x02; // aka unknown
    public static final int CT_MULTI        =0x03;
    public static final int CT_LETTERBOX    =0x04;
    public static final int CT_EARTH        =0x05;
    public static final int CT_VIRTUAL      =0x06;
    public static final int CT_WEBCAM       =0x07;
    public static final int CT_WHEREIGO     =0x08;

    // Extended, opencaching etc. (Not detected atm)
    public static final int CT_MOVABLE      =0x20;
    public static final int CT_QUIZ         =0x21;
    public static final int CT_POD          =0x22;
    public static final int CT_EDU          =0x23;

    // Events
    public static final int CT_EVENT        =0x40;
    public static final int CT_MEGAEVENT    =0x41;
    public static final int CT_CITO         =0x42; // aka "Cache In Trash Out"

    // Cache Containers
    private static int CC_UNDEFINED = 0;
    private static int CC_MICRO = 1;
    private static int CC_SMALL = 2;
    private static int CC_REGULAR = 3;
    private static int CC_LARGE = 4;
    private static int CC_OTHER = 5;

    public Position position;

    // Waypoints associated with this cache, will only show up on the map if
    // this is the selected cache.
    public Position[] wayPoints=null;

    // These really only need one 32bit int!
    public boolean available;
    public int difficulty;
    public int terrain;
    public int type;
    public int container;

    public String hint;         // decoded
    public String placedBy;
    public String name;
    public String code;         // Cache id-code, for ex. OS3FD4.
    public String altCode;      // Cache code on some other site.
    public String shortDesc;    // Usually a summary
    public String longDesc;     // Full description
    public CacheLog[] logs=null;     // last logs, descending order

    public String fullDesc() {
        return shortDesc+'\n'+longDesc;
    }

    private static int stringToRating(String sVal) {
        int val = (sVal.charAt(0)-'0') << 1;
        if (sVal.length() >= 3) ++val;
        return val;
    }

    public void setDifficulty(String sDiff) {
        difficulty = stringToRating(sDiff);
    }

    public void setTerrain(String sTerr) {
        terrain = stringToRating(sTerr);
    }

    public void setType(String sType) {
        if (sType.startsWith("Trad")) type = CT_TRADITIONAL;
        else if (sType.startsWith("Unkn")) type = CT_MYSTERY;
        else if (sType.startsWith("Mult")) type = CT_MULTI;
        else if (sType.startsWith("Webc")) type = CT_WEBCAM;
        else if (sType.startsWith("Virt")) type = CT_VIRTUAL;
        else if (sType.startsWith("Eart")) type = CT_EARTH;
        else if (sType.startsWith("Lett")) type =  CT_LETTERBOX;
        else if (sType.startsWith("Even")) type = CT_EVENT;
        else if (sType.startsWith("Wher")) type = CT_WHEREIGO;
        else if (sType.startsWith("Mega")) type = CT_MEGAEVENT;
        else if (sType.startsWith("Cach")) type = CT_CITO;
        else type = CT_UNDEFINED;
    }

    public void setContainer(String sCont) {
        if (sCont.startsWith("Mi")) container = CC_MICRO;
        if (sCont.startsWith("Sm")) container = CC_SMALL;
        if (sCont.startsWith("Re")) container = CC_REGULAR;
        if (sCont.startsWith("La")) container = CC_LARGE;
        if (sCont.startsWith("Ot")) container = CC_OTHER;
        else container = CC_UNDEFINED;
    }
}