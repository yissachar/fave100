package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.fave100.shared.requestfactory.WhylineProxy;
import com.googlecode.objectify.annotation.Embed;

@Embed
public class ExploreResult implements FaveItemProxy, WhylineProxy, Serializable {

	private static final long serialVersionUID = 4513875685034993004L;
	private static final String COUNT_KEY = "exploreCount";
	private static final String EXPLORE_LIST_KEY = "exploreList";
	private static Cache _cache;

	private String username;
	private String song;
	private String artist;
	private String songID;
	private String whyline;
	private String avatar;

	public ExploreResult() {
	}

	public ExploreResult(final FaveItem faveItem, final AppUser appUser) {
		setUsername(appUser.getUsername());
		setSong(faveItem.getSong());
		setArtist(faveItem.getArtist());
		setSongID(faveItem.getSongID());
		setWhyline(faveItem.getWhyline());
		setAvatar(appUser.getAvatarImage());
	}

	public static Cache getCacheInstance() {
		if (_cache == null) {
			CacheFactory cacheFactory;
			try {
				cacheFactory = CacheManager.getInstance().getCacheFactory();
				_cache = cacheFactory.createCache(new HashMap());
			}
			catch (final CacheException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return _cache;
	}

	public static void addToExploreCache(final FaveItem faveItem, final AppUser appUser) {
		final Cache cache = getCacheInstance();
		Integer count = (Integer)cache.get(COUNT_KEY);
		if (count == null) {
			count = 0;
			cache.put(COUNT_KEY, count);
		}
		else {
			count++;
			if (count == 100 || count % 1000 == 0) {
				// Once cache hits 100 for first time, or every 1000 song picks, populate a List with all the cache results and back with datastore
				final List<ExploreResult> exploreResults = new ArrayList<>();
				for (int i = 0; i < 100; i++) {
					exploreResults.add((ExploreResult)cache.get(i));
				}
				cache.put(EXPLORE_LIST_KEY, exploreResults);
				final ExploreResultList exploreResultList = new ExploreResultList(ExploreResultList.CURRENT_LIST);
				exploreResultList.setList(exploreResults);
				ofy().save().entity(exploreResultList).now();
			}
			cache.put(COUNT_KEY, count);
		}
		final ExploreResult exploreResult = new ExploreResult(faveItem, appUser);
		cache.put(count % 100, exploreResult);
	}

	public static List<ExploreResult> getExploreFeed() {
		final Cache cache = getCacheInstance();

		List<ExploreResult> list = (List<ExploreResult>)cache.get(EXPLORE_LIST_KEY);

		if (list == null) {
			list = new ArrayList<>();
			final Integer count = (Integer)cache.get(COUNT_KEY);
			if (count != null) {
				for (int i = 0; i <= count; i++) {
					final ExploreResult exploreResult = (ExploreResult)cache.get(i);
					if (exploreResult != null) {
						list.add(exploreResult);
					}
				}
			}
			// If there was nothing in the memcache, populate from backing datastore
			if (list.size() == 0) {
				list = ofy().load().type(ExploreResultList.class).id(ExploreResultList.CURRENT_LIST).get().getList();
			}
		}
		return list;
	}

	/* Getters and Setters */

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	@Override
	public String getSong() {
		return song;
	}

	public void setSong(final String song) {
		this.song = song;
	}

	@Override
	public String getArtist() {
		return artist;
	}

	public void setArtist(final String artist) {
		this.artist = artist;
	}

	@Override
	public String getSongID() {
		return songID;
	}

	public void setSongID(final String songID) {
		this.songID = songID;
	}

	@Override
	public String getWhyline() {
		return whyline;
	}

	public void setWhyline(final String whyline) {
		this.whyline = whyline;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(final String avatar) {
		this.avatar = avatar;
	}

}
