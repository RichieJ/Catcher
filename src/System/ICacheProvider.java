/*
 * Catcher
 *
 * License: GPL v3
 * Authors: richard_jonsson@hotmail.com, tommyc@lavabit.com
 */

package System;


public interface ICacheProvider {

    void findNear(CacheStore cs, Position position, int radiusKiloMeters, CacheFilter filter);
    void findText(CacheStore cs, String text, boolean searchCacheNames, boolean searchDescriptions, boolean searchLogs, CacheFilter filter);
    void findByOwner(CacheStore cs, String ownerID, CacheFilter filter);

}
