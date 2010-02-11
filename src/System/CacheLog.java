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

    public CacheLog(int type, long dateStamp, String name, String log) {
        this.type=type;
        this.dateStamp = dateStamp;
        this.name = name;
        this.log = log;
    }
}
