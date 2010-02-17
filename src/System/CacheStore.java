/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */
package System;

import java.util.Vector;

/**
 * A cachestore can be seen as a database that stores caches.
 */
public class CacheStore {

    private Vector caches;

    // Save the cachestore to persistent storage.
    public void saveStore() {
        System.out.println("CacheStore.saveStore not impl.");
    }
    
    // Load the cachestore from persistent storage.
    public void loadStore() {
        System.out.println("CacheStore.loadStore not impl.");
    }
    
    // Add a cache to the store.
    public void addCache(Cache c) {
        caches.addElement(c);
    }

    // Remove all caches from the store.
    public void clearAll() {
        caches.removeAllElements();
    }

    /**
     * Calculates the cache closest to p.
     * @param p the relative position.
     * @return the nearest cache relative to p, or null if there are no caches.
     */
    public Cache getNearest(Position p) {
        if (caches.isEmpty()) return null;
        Cache c;
        int nearestDistance = 0;
        int nearestIndex = 0;
        for (int i=0;i<caches.size();i++) {
            c = (Cache)caches.elementAt(i);
            // fixme: distanceTo is compute-heavy, and we don't need the actual
            // distance.
            int d = c.position.distanceTo(p);
            if (nearestDistance > d) {
                nearestIndex = i;
                nearestDistance = d;
            }
        }
        return (Cache)caches.elementAt(nearestIndex);
    }
}
