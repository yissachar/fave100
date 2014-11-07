package com.fave100.server.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fave100.server.MemcacheManager;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.domain.favelist.FaveRankerWrapper;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.server.domain.favelist.TrendingList;
import com.fave100.shared.Constants;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

/**
 * This servlet will process hashtags as they are added to the task queue
 * by getting all FaveLists for the hashtag and calculating the top 100 items
 * 
 * @author yissachar.radcliffe
 * 
 */
@SuppressWarnings("serial")
public class HashtagBuilderServlet extends HttpServlet
{
	public static String HASHTAG_BUILDER_URL = "/tasks/hashtags";
	public static String HASHTAG_PARAM = "hashtag";

	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse res)
			throws ServletException, IOException {

		final String hashtag = req.getParameter(HASHTAG_PARAM);

		final HashMap<FaveRankerWrapper, Double> users = new HashMap<>();
		final HashMap<FaveRankerWrapper, Double> critics = new HashMap<>();
		final List<FaveRankerWrapper> all = new ArrayList<FaveRankerWrapper>();

		// Build the lists
		final int listCount = addAllLists(null, hashtag, users, critics, all);

		if (!users.isEmpty()) {
			saveTopItems(hashtag, users, listCount, false);
		}

		if (!critics.isEmpty()) {
			saveTopItems(hashtag, critics, listCount, true);
		}

		saveNewestAndTrendingItems(hashtag, all);
	}

	private void saveTopItems(String hashtag, HashMap<FaveRankerWrapper, Double> items, int listCount, boolean critic) {
		final List<Map.Entry<FaveRankerWrapper, Double>> sorted = sort(items);

		int i = 0;
		final List<FaveItem> master = new ArrayList<FaveItem>();
		for (final Map.Entry<FaveRankerWrapper, Double> entry : sorted) {
			// Add the top 100 songs to master list
			if (i < 100) {
				final FaveItem faveItem = entry.getKey().getFaveItem();
				faveItem.setWhyline("");
				faveItem.setWhylineRef(null);
				master.add(faveItem);
				i++;
			}
		}

		Hashtag hashtagEntity = ofy().load().type(Hashtag.class).id(hashtag).now();
		if (critic) {
			hashtagEntity.setCriticsList(master);
		}
		else {
			// Calculate the zcore to determine top trending lists
			hashtagEntity.setZscore(calculateZscore(hashtagEntity.getSlidingListCount(), listCount));
			hashtagEntity.addListCount(listCount);
			hashtagEntity.setList(master);
		}

		// Save the master list to the datastore
		ofy().save().entity(hashtagEntity).now();
	}

	private void saveNewestAndTrendingItems(String hashtag, List<FaveRankerWrapper> all) {
		Collections.sort(all, Collections.reverseOrder(new Comparator<FaveRankerWrapper>() {

			@Override
			public int compare(FaveRankerWrapper o1, FaveRankerWrapper o2) {
				return o1.getFaveItem().getDatePicked().compareTo(o2.getFaveItem().getDatePicked());
			}

		}));

		// Add the 100 newest songs to the list
		final List<FaveItem> newest = new ArrayList<FaveItem>();
		for (int i = Math.min(99, all.size() - 1); i >= 0; i--) {
			FaveItem faveItem = all.get(i).getFaveItem();
			faveItem.setWhyline("");
			faveItem.setWhylineRef(null);
			newest.add(faveItem);
		}

		// Save the newest list
		Hashtag hashtagEntity = ofy().load().type(Hashtag.class).id(hashtag).now();
		hashtagEntity.setNewestList(newest);
		ofy().save().entity(hashtagEntity).now();

		// Initialize memcache with the list
		MemcacheManager.setNewestSongs(hashtag, newest);

		// Calculate Trending
		if (!all.isEmpty()) {
			long defaultMin = MemcacheManager.getTrendingScore(newest.get(0), false);

			List<FaveItem> pseudoTrending = new ArrayList<FaveItem>();
			List<Long> trendingScores = new ArrayList<Long>();
			long minTrending = MemcacheManager.incrementTrendingMin(0L, defaultMin);
			for (FaveRankerWrapper wrapper : all) {
				long trendingScore = MemcacheManager.getTrendingScore(wrapper.getFaveItem());
				if (trendingScore >= minTrending) {
					trendingScores.add(trendingScore);
					pseudoTrending.add(wrapper.getFaveItem());
				}
			}

			long oldMin = MemcacheManager.incrementTrendingMin(0L, defaultMin);
			long newMin = oldMin;
			Collections.sort(trendingScores);
			if (trendingScores.size() >= 100) {
				newMin = trendingScores.get(99);
			}

			MemcacheManager.incrementTrendingMin(newMin - oldMin, newMin);

			TrendingList trendingList = new TrendingList(hashtag, pseudoTrending);
			ofy().save().entity(trendingList).now();
		}

		// If all hashtags built, aggregrate the trending as a last step
		if (MemcacheManager.incrementRemainingHashtagCount(-1) == 0) {
			Map<FaveRankerWrapper, Long> trending = new HashMap<FaveRankerWrapper, Long>();
			// TODO: Nov 3, 2014 Handle more than 1000 lists
			List<TrendingList> lists = ofy().load().type(TrendingList.class).list();
			for (TrendingList list : lists) {
				for (FaveItem faveItem : list.getItems()) {
					trending.put(new FaveRankerWrapper(faveItem), MemcacheManager.getTrendingScore(faveItem, false));
				}
			}

			List<Map.Entry<FaveRankerWrapper, Long>> sortedTrending = new LinkedList<>(trending.entrySet());
			Collections.sort(sortedTrending, Collections.reverseOrder(new Comparator<Map.Entry<FaveRankerWrapper, Long>>() {
				@Override
				public int compare(final Map.Entry<FaveRankerWrapper, Long> o1, final Map.Entry<FaveRankerWrapper, Long> o2) {
					return (o1.getValue()).compareTo(o2.getValue());
				}
			}));

			List<FaveItem> finalTrending = new ArrayList<FaveItem>();
			for (int i = 0; i < 100 && i < sortedTrending.size(); i++) {
				finalTrending.add(sortedTrending.get(i).getKey().getFaveItem());
			}

			Hashtag trendingHashtag = new Hashtag(Constants.TRENDING_LIST_NAME, "Fave100");
			trendingHashtag.setList(finalTrending);
			ofy().save().entity(trendingHashtag).now();
		}
	}

	private List<Map.Entry<FaveRankerWrapper, Double>> sort(Map<FaveRankerWrapper, Double> items) {
		List<Map.Entry<FaveRankerWrapper, Double>> sorted = new LinkedList<>(items.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<FaveRankerWrapper, Double>>() {
			@Override
			public int compare(final Map.Entry<FaveRankerWrapper, Double> o1, final Map.Entry<FaveRankerWrapper, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		return sorted;
	}

	private double calculateZscore(List<Integer> slidingListCount, int newListCount) {
		int n = slidingListCount.size();

		if (n == 0)
			return 0;

		int total = 0;
		for (Integer count : slidingListCount) {
			total += count;
		}
		double avg = total / n;
		double sumsq = 0;
		for (Integer count : slidingListCount) {
			sumsq += Math.pow(count - avg, 2);
		}
		double std = Math.sqrt(sumsq / n);
		if (std == 0)
			std = 1;

		return (newListCount - avg) / std;
	}

	// Get favelists 1000 at a time, and store their rank, returns number of lists
	private int addAllLists(String cursor, String hashtag, HashMap<FaveRankerWrapper, Double> users, HashMap<FaveRankerWrapper, Double> critics, List<FaveRankerWrapper> all) {
		Query<FaveList> query = ofy().load().type(FaveList.class).filter("hashtagId", hashtag).limit(1000);

		if (cursor != null) {
			query = query.startAt(Cursor.fromWebSafeString(cursor));
		}

		boolean shouldContinue = false;

		int count = 0;
		final QueryResultIterator<FaveList> iterator = query.iterator();
		while (iterator.hasNext()) {
			count++;
			final FaveList faveList = iterator.next();
			// Add up the total rank for each song in the list
			int i = 1;
			for (final FaveItem faveItem : faveList.getList()) {
				final FaveRankerWrapper faveHolder = new FaveRankerWrapper(faveItem);
				final double score = FaveListDao.calculateItemScore(i);

				// Build a critics only master list if needed
				if (faveList.getCriticUrl() != null && !faveList.getCriticUrl().isEmpty()) {
					final double newVal = (critics.get(faveHolder) != null) ? critics.get(faveHolder) + score : score;
					critics.put(faveHolder, newVal);
				}
				// Otherwise just build the user master list
				else {
					final double newVal = (users.get(faveHolder) != null) ? users.get(faveHolder) + score : score;
					users.put(faveHolder, newVal);
				}
				all.add(faveHolder);
				i++;
			}

			// If we processed the full 1000 limit, grab the next batch of hashtags to process
			if (count == 1000)
				shouldContinue = true;
		}

		// While we still have favelists to process, keep adding their ranks
		if (shouldContinue) {
			return addAllLists(iterator.getCursor().toWebSafeString(), hashtag, users, critics, all) + count;
		}
		return count;
	}
}
