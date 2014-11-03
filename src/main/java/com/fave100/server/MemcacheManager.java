package com.fave100.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.shared.Constants;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class MemcacheManager {

	private static final String NAMESPACE_NEWEST = "Newest";
	private static final String NAMESPACE_TRENDING = "Trending";
	private static final String NEWEST_COUNT_ID = "count";
	private static final String TRENDING_MIN_ID = "min";
	private static final String TRENDING_SCORE_ID = "s";
	private static final String TRENDING_REMAINING_ID = "remaining";
	private static final String ID_SEPARATOR = ":";

	public static void addNewSong(String list, FaveItem faveItem) {
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService(NAMESPACE_NEWEST);
		// This is basically a circular buffer. There will never be more than 100 entries
		// per list, since count will wrap around and rewrite old values.
		long count = cache.increment(list + ID_SEPARATOR + NEWEST_COUNT_ID, 1L, -1L);
		cache.put(list + ID_SEPARATOR + (count % Constants.MAX_ITEMS_PER_LIST), faveItem);
	}

	public static List<FaveItem> getNewestSongs(String list) {
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService(NAMESPACE_NEWEST);

		long count = ((Number)cache.get(list + ID_SEPARATOR + NEWEST_COUNT_ID)).longValue() % Constants.MAX_ITEMS_PER_LIST;
		List<FaveItem> newestSongs = new ArrayList<FaveItem>();

		// Need to add in two groups to ensure proper ordering
		for (int i = (int)count; i >= 0; i--) {
			FaveItem faveItem = (FaveItem)cache.get(list + ID_SEPARATOR + i);
			if (faveItem != null) {
				newestSongs.add(faveItem);
			}
		}

		for (int i = (int)count + 1; i < Constants.MAX_ITEMS_PER_LIST; i++) {
			FaveItem faveItem = (FaveItem)cache.get(list + ID_SEPARATOR + i);
			if (faveItem != null) {
				newestSongs.add(faveItem);
			}
		}

		return newestSongs;
	}

	public static void setNewestSongs(String list, List<FaveItem> faveItems) {
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService(NAMESPACE_NEWEST);

		Map<String, FaveItem> map = new HashMap<String, FaveItem>();
		int i = 0;
		for (FaveItem faveItem : faveItems) {
			map.put(list + ID_SEPARATOR + i, faveItem);
			i++;
		}

		cache.putAll(map);
		cache.put(list + ID_SEPARATOR + NEWEST_COUNT_ID, i - 1);
	}

	public static long incrementTrendingMin(long increment, long defaultMin) {
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService(NAMESPACE_TRENDING);
		return cache.increment(TRENDING_MIN_ID, increment, defaultMin);
	}

	public static void resetTrendingMin() {
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService(NAMESPACE_TRENDING);
		cache.put(TRENDING_MIN_ID, 0);
	}

	public static long getTrendingScore(FaveItem faveItem) {
		return getTrendingScore(faveItem, true);
	}

	public static long getTrendingScore(FaveItem faveItem, boolean alter) {
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService(NAMESPACE_TRENDING);
		long score = Math.max(800_000_000 - (new Date().getTime() - faveItem.getDatePicked().getTime()), 0);
		String id = TRENDING_SCORE_ID + ID_SEPARATOR + faveItem.getId();
		if (alter)
			return cache.increment(id, score, score);

		Long storedScore = (Long)cache.get(id);
		return storedScore != null ? storedScore : score;
	}

	public static long incrementRemainingHashtagCount(long increment) {
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService(NAMESPACE_TRENDING);
		return cache.increment(TRENDING_REMAINING_ID, increment, 0L);
	}
}
