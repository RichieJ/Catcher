/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */

package MIDP;

import Utils.Task;
import java.io.InputStream;
import javax.microedition.io.Connector;

/**
 * 
 */
public class StringDownloadTask extends Task {

    private final String url;

    private String string;

    public StringDownloadTask(String url) {
        this.url = url;
    }

    public void task() {
        try {
            InputStream data = Connector.openDataInputStream(url);
            string = String.valueOf(data);
            data.close();
        } catch (Exception ex) {
            string = null;
        }
    }

    public String getString() {
        return string;
    }

    public String getUrl() {
        return url;
    }

}
