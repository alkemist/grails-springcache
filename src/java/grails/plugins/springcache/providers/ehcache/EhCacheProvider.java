package grails.plugins.springcache.providers.ehcache;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import grails.plugins.springcache.cache.AbstractCacheProvider;
import grails.plugins.springcache.cache.CacheFacade;
import grails.plugins.springcache.cache.CacheNotFoundException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.lang.StringUtils;

public class EhCacheProvider extends AbstractCacheProvider<EhCacheCachingModel, EhCacheFlushingModel> {

	private CacheManager cacheManager;

	protected CacheFacade getCache(EhCacheCachingModel cachingModel) {
		return getCacheByName(cachingModel.getCacheName());
	}

	protected Collection<CacheFacade> getCaches(EhCacheFlushingModel flushingModel) {
		Collection<CacheFacade> caches = new HashSet<CacheFacade>();
		for (String cacheName : flushingModel.getCacheNames()) {
			caches.add(getCacheByName(cacheName));
		}
		return caches;
	}

	private CacheFacade getCacheByName(String name) {
		if (cacheManager.cacheExists(name)) {
			Cache cache = cacheManager.getCache(name);
			return new EhCacheFacade(cache);
		} else {
			throw new CacheNotFoundException(name);
		}
	}

	public void addCachingModel(String id, Properties properties) {
		String cacheName = getRequiredProperty(properties, "cacheName");
		EhCacheCachingModel cachingModel = new EhCacheCachingModel(id, cacheName);
		addCachingModel(cachingModel);
	}

	public void addFlushingModel(String id, Properties properties) {
		String cacheNames = getRequiredProperty(properties, "cacheNames");
		EhCacheFlushingModel flushingModel = new EhCacheFlushingModel(id, Arrays.asList(StringUtils.split(cacheNames, ",")));
		addFlushingModel(flushingModel);
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

}
