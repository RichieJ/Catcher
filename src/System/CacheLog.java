/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package System;

/**
 *
 * @author richie
 */
public class CacheLog {
    public int type;
    public long dateStamp;
    public String name;
    public String log;

    private static int strToType(String sType) {
        return 0; //fixme: not impl
    }

    private static int strToDate(String sDate) {
        return 0; //fixme: not impl
    }

    public CacheLog(String type, String dateStamp, String name, String log) {
        this.type = strToType(type);
        this.dateStamp = strToDate(dateStamp);
        this.name = name;
        this.log = log;
    }
}
