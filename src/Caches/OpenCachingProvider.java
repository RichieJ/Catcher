/*
 * Catcher
 *
 * License: GPL v2
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */
package Caches;

import MIDP.DownloaderTask;
import System.Cache;
import System.CacheFilter;
import System.ICacheProvider;
import System.Position;
import Utils.Task;
import Utils.TaskCompletedListener;
import Utils.TaskRunner;


public class OpenCachingProvider implements ICacheProvider, TaskCompletedListener {

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

    public void taskCompleted(Task task) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void submitQuery() {
        DownloaderTask task = new DownloaderTask("", "", "");
        task.setTaskCompletedListener(this);
        runner.addTask(task);
    }
}
