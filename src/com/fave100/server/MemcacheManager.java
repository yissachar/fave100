package com.fave100.server;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.fave100.server.domain.Song;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveList;

public class MemcacheManager {

	public static final String SEPARATOR_TOKEN = ":";
	public static final String FAVEITEM_RANK_NAMESPACE = "faveItemRank";
	public static final String MASTER_FAVELIST_NAMESPACE = "masterFaveList";

	private static MemcacheManager _instance;
	private Cache _cache;

	private MemcacheManager(final Cache cache) {
		setCache(cache);
	}

	public static MemcacheManager getInstance() {
		if (_instance == null) {
			try {
				final CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
				_instance = new MemcacheManager(cacheFactory.createCache(Collections.emptyMap()));
			}
			catch (final Exception e) {
				Logger.getAnonymousLogger().log(Level.WARNING, e.getMessage());
			}
		}
		return _instance;
	}

	public int getFaveItemScore(final String id, final String hashtag) {
		// Need to store in intermediate object, since casting a null results in NPE
		final Object cacheItem = _cache.get(FAVEITEM_RANK_NAMESPACE + SEPARATOR_TOKEN + hashtag + SEPARATOR_TOKEN + id);
		return (cacheItem == null) ? 0 : (int)cacheItem;
	}

	public void putFaveItemScore(final String id, final String hashtag, final int score) {
		// e.g. {faveItemRank:rock2013:645116, 245}
		_cache.put(FAVEITEM_RANK_NAMESPACE + SEPARATOR_TOKEN + hashtag + SEPARATOR_TOKEN + id, score);

		// If it now belongs to master list for hashtag, update master
		final List<FaveItem> master = getMasterFaveList(hashtag);
		if (master == null)
			return;

		int targetScore = 0;
		int rank = master.size() + 1;
		FaveItem existingFave = null;

		// Check for dupe, and rerank
		for (int i = master.size() - 1; i > 0; i--) {
			final String targetId = master.get(i).getId();
			targetScore = getFaveItemScore(targetId, hashtag);
			if (score > targetScore) {
				rank = i;
			}

			if (id.equals(targetId)) {
				existingFave = master.get(i);
			}
		}

		// TODO: Bad that we have to lookup the song each time a rerank occurs -> pass the Song in since we have it already from add or remove
		if (score > 0 && rank < FaveList.MAX_FAVES) {
			FaveItem faveItem = existingFave;
			// Fave not in master yet, insert new fave
			if (faveItem == null) {
				Song song;
				try {
					song = Song.findSong(id);
					faveItem = new FaveItem(song.getSong(), song.getArtist(), song.getId());
				}
				catch (final Exception e) {
					Logger.getAnonymousLogger().log(Level.WARNING, e.getMessage());
				}
			}
			// Fave already in master, adjust position
			else {
				master.remove(existingFave);
			}

			if (master.size() == FaveList.MAX_FAVES || rank < master.size()) {
				master.add(rank, faveItem);
			}
			else {
				master.add(faveItem);
			}

			putMasterFaveList(hashtag, master);
		}

	}

	public void modifyFaveItemScore(final String id, final String hashtag, final int delta) {
		// TODO: Aug 19 2013: Is it possible to increment counter atomically? Not super important, but nice
		final int currentRank = getFaveItemScore(id, hashtag);
		putFaveItemScore(id, hashtag, currentRank + delta);
	}

	public List<FaveItem> getMasterFaveList(final String hashtag) {
		// Need to store in intermediate object, since casting a null results in NPE
		final Object cacheItem = _cache.get(MASTER_FAVELIST_NAMESPACE + SEPARATOR_TOKEN + hashtag);
		return cacheItem == null ? null : (List<FaveItem>)cacheItem;
	}

	public void putMasterFaveList(final String hashtag, final List<FaveItem> list) {
		_cache.put(MASTER_FAVELIST_NAMESPACE + SEPARATOR_TOKEN + hashtag, list);
	}

	/* Getters and Setters */

	public Cache getCache() {
		return _cache;
	}

	public void setCache(final Cache cache) {
		_cache = cache;
	}

}
