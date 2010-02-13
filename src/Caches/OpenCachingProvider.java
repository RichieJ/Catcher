/*
 * Catcher
 *
 * License: GPL v2
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */
package Caches;

import MIDP.StringDownloadTask;
import System.GPX;
import System.CacheFilter;
import System.CacheStore;
import System.ICacheProvider;
import System.Position;
import Utils.Task;
import Utils.TaskCompletedListener;
import Utils.TaskRunner;

/**
 * OpenCaching is a cache service with individual sites for different countries.
 *
 * There are many ways to get caches from Open Caching. The ones that makes most
 * sense to us are GPX and LOC.
 * GPX - An xml format, very detailed data for each cache, including logs,
 *       attributes and waypoints.
 * LOC - An xml format with very sparse info. This would suit us if it included
 *       cache type.
 *
 * This leaves us with GPX which we need to support anyway.
 *
 * In order to save memory it would probably make sense to just store the
 * crucial information from searches, and request a new GPX for each cache that
 * gets selected.
 */
public class OpenCachingProvider implements ICacheProvider,
        TaskCompletedListener {

    private final TaskRunner runner;

    public OpenCachingProvider() {
        runner = new TaskRunner("GPXLoader", 2);
        runner.start();
    }

    public void findNear(CacheStore cs, Position position, int radiusKiloMeters,
            CacheFilter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void findText(CacheStore cs, String text, boolean searchCacheNames,
            boolean searchDescriptions, boolean searchLogs, CacheFilter filter){
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void findByOwner(CacheStore cs, String ownerID, CacheFilter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * taskCompleted gets called when a http request completes.
     * @param task
     */
    public void taskCompleted(Task task) {
        StringDownloadTask dl = (StringDownloadTask)task;
        GPX.parse(dl.getString());
    }

    private void httpLoad(String url) {
        StringDownloadTask task = new StringDownloadTask(url);
        task.setTaskCompletedListener(this);
        runner.addTask(task);
    }

}
