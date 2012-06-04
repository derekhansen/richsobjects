package com.github.ryanbrainard.richsobjects.api.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author Ryan Brainard
 */
public class SimpleInMemoryMultiUserCacheLoader implements SfdcApiCacheLoaderProvider {

    private static final ServiceLoader<SfdcApiUserCacheProvider> userCacheLoader = ServiceLoader.load(SfdcApiUserCacheProvider.class);

    private static Map<String, SfdcApiUserCache> allCaches = new HashMap<String, SfdcApiUserCache>();
    
    @Override
    public SfdcApiUserCache get(String key, SfdcApiClient apiClient) {
        if (allCaches.containsKey(key)) {
            return allCaches.get(key);
        } else {
            final Iterator<SfdcApiUserCacheProvider> providerIterator = userCacheLoader.iterator();

            if (!providerIterator.hasNext()) {
                throw new IllegalStateException(
                        "Could not load service from " + userCacheLoader +
                                "\nEnsure an entry in META-INF/services has been loaded on the classpath.");
            }

            final SfdcApiUserCache cache = providerIterator.next().get(apiClient);
            allCaches.put(key, cache);
            return cache;
        }
    }

    @Override
    public void invalidate() {
        allCaches.clear();
    }

}