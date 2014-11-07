package com.fave100.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.shared.Constants;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class MemcacheManager {

	private static final String NAMESPACE_NEWEST = "Newest";
	private static final String NAMESPACE_TRENDING = "Trending";
	private static final String TRENDING_MIN_ID = "min";
	private static final String TRENDING_SCORE_ID = "s";
	private static final String TRENDING_REMAINING_ID = "remaining";
	private static final String ID_SEPARATOR = ":";

	public static void addNewSong(String list, FaveItem faveItem) {
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService(NAMESPACE_NEWEST);

		@SuppressWarnings("unchecked")
		LinkedHashSet<FaveItem> newestItems = (LinkedHashSet<FaveItem>)cache.get(list);
		if (newestItems == null) {
			newestItems = new LinkedHashSet<FaveItem>();
			cache.put(list, newestItems);
		}

		if (newestItems.size() >= Constants.MAX_ITEMS_PER_LIST && !newestItems.contains(faveItem)) {
			newestItems.remove(newestItems.iterator().next());
		}
		newestItems.add(faveItem);
		cache.put(list, newestItems);
	}

	public static List<FaveItem> getNewestSongs(String list) {
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService(NAMESPACE_NEWEST);

		@SuppressWarnings("unchecked")
		List<FaveItem> newestSongs = new ArrayList<FaveItem>((LinkedHashSet<FaveItem>)cache.get(list));
		Collections.reverse(newestSongs);
		return newestSongs;
	}

	public static void setNewestSongs(String list, List<FaveItem> faveItems) {
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService(NAMESPACE_NEWEST);
		cache.put(list, new LinkedHashSet<FaveItem>(faveItems));
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
