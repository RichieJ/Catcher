/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */

package MIDP;

import GUI.IClientEvents;
import System.IImageLoader;
import Utils.Task;
import Utils.TaskCompletedListener;
import Utils.TaskRunner;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;



public class MIDPImageLoader implements IImageLoader, TaskCompletedListener {

    private final Hashtable imageCache;
    private final Vector unloadQueue;
    private final Vector currentDownloads;
    private final TaskRunner runner;
    private final IClientEvents events;
    private int cacheSizeLow;
    private int cacheSizeHigh;


    public MIDPImageLoader(IClientEvents events) {
        this.imageCache = new Hashtable();
        this.currentDownloads = new Vector();
        this.unloadQueue = new Vector();
        this.events = events;
        cacheSizeLow = 16;
        cacheSizeHigh = 24;
        runner = new TaskRunner("Downloader", 4);
        runner.start();
    }

    public Object httpLoad(String url, String localCachePath, int minimumMemoryCache) {
        //Test image: http://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png
        // Check if Image has been cached already
        Image ret = null;
        
        synchronized (imageCache) {
            ret = (Image)imageCache.get(url);
        }
        
        // Check if Image was cached
        if (ret == null) {
            synchronized (currentDownloads) {
                if (currentDownloads.contains(url)) return null;
                currentDownloads.addElement(url);
            }

            ImageDownloadTask task = new ImageDownloadTask(url, localCachePath, localCachePath);
            task.setTaskCompletedListener(this);
            runner.addTask(task);
        } else {
            synchronized (unloadQueue) {
                unloadQueue.removeElement(url);
                unloadQueue.addElement(url);
            }
        }

        return ret;
    }

    public Object localLoad(String path, int minimumMemoryCache) {
        return null;
    }

    public Object createImage(int width, int height) {
        Image image = Image.createImage(width, height);
        return image;
    }

    public Object drawImage(Object canvas, Object image, int xPos, int yPos) {
        Image dest = (Image)canvas;
        Image src = (Image)image;

        if ((dest == null) || (src == null)) return dest;

        dest.getGraphics().drawImage(src, xPos, yPos, Graphics.TOP|Graphics.LEFT);
        return dest;
    }

    public void taskCompleted(Task task) {
        ImageDownloadTask dl = (ImageDownloadTask)task;
        synchronized (imageCache) {
            imageCache.put(dl.getUrl(), dl.getImage());
        }
        synchronized (currentDownloads) {
            currentDownloads.removeElement(dl.getUrl());
        }
        synchronized (unloadQueue) {
            unloadQueue.addElement(dl.getUrl());
            if (unloadQueue.size() >= cacheSizeHigh) {
                while (unloadQueue.size() > cacheSizeLow) {
                    String url = (String)unloadQueue.firstElement();
                    unloadQueue.removeElementAt(0);
                    imageCache.remove(url);
                }
            }
        }
        if (events != null) {
            events.maptileDownloaded();
        }
    }

}
