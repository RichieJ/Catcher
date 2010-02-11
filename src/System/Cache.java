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
    public static final int CT_UNDFINED =-1;
    
    // Ordinary types
    public static final int CT_TRADITIONAL  =0x00;
    public static final int CT_MYSTERY      =0x01; // aka unknown
    public static final int CT_MULTI        =0x02;
    public static final int CT_LETTERBOX    =0x03;
    public static final int CT_EARTH        =0x04;
    public static final int CT_VIRTUAL      =0x05;
    public static final int CT_WEBCAM       =0x06;
    public static final int CT_WHEREIGO     =0x07;

    // Extended, opencaching etc. (Not detected atm)
    public static final int CT_MOVABLE      =0x20;
    public static final int CT_QUIZ         =0x21;
    public static final int CT_POD          =0x22;
    public static final int CT_EDU          =0x23;

    // Events
    public static final int CT_EVENT        =0x40;
    public static final int CT_MEGAEVENT    =0x41;
    public static final int CT_CITO         =0x42; // aka "Cache In Trash Out"

    public Position position;

    // Waypoints associated with this cache, will only show up on the map if
    // this is the selected cache.
    public Position[] wayPoints=null;

    public int difficulty;
    public int terrain;
    public int type;

    public String hint;         // decoded
    public String name;
    public String code;         // Cache id-code, for ex. OS3FD4.
    public String altCode;      // Cache code on some other site.
    public String shortDesc;    // Usually a summary
    public String longDesc;     // Full description
    public CacheLog[] logs=null;     // last logs, descending order

    public String fullDesc() {
        return shortDesc+'\n'+longDesc;
    }

    public static int stringToType(String type) {
        if (type.startsWith("Trad")) {
            return CT_TRADITIONAL;
        } else if (type.startsWith("Unkn")) {
            return CT_MYSTERY;
        } else if (type.startsWith("Mult")) {
            return CT_MULTI;
        } else if (type.startsWith("Webc")) {
            return CT_WEBCAM;
        } else if (type.startsWith("Virt")) {
            return CT_VIRTUAL;
        } else if (type.startsWith("Eart")) {
            return CT_EARTH;
        } else if (type.startsWith("Lett")) {
            return CT_LETTERBOX;
        } else if (type.startsWith("Even")) {
            return CT_EVENT;
        } else if (type.startsWith("Wher")) {
            return CT_WHEREIGO;
        } else if (type.startsWith("Mega")) {
            return CT_MEGAEVENT;
        } else if (type.startsWith("Cach")) {
            return CT_CITO;
        }
        return CT_UNDFINED;
    }
}