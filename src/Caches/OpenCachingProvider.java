/*
 * Catcher
 *
 * License: GPL v2
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */
package Caches;

import MIDP.StringDownloadTask;
import System.GPX;
import System.Cache;
import System.CacheFilter;
import System.ICacheProvider;
import System.Position;
import Utils.Task;
import Utils.TaskCompletedListener;
import Utils.TaskRunner;


public class OpenCachingProvider implements ICacheProvider,
        TaskCompletedListener {

    private final TaskRunner runner;

    public OpenCachingProvider() {
        runner = new TaskRunner("GPXLoader", 2);
        runner.start();
    }

    public Cache[] findNear(Position position, int radiusKiloMeters,
            CacheFilter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Cache[] findText(String text, boolean searchCacheNames,
            boolean searchDescriptions, boolean searchLogs, CacheFilter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Cache[] findByOwner(String ownerID, CacheFilter filter) {
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
