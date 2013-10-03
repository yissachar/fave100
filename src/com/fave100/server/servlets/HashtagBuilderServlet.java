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
import com.fave100.server.domain.favelist.FaveRankerWrapper;
import com.fave100.server.domain.favelist.Hashtag;
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

		final HashMap<FaveRankerWrapper, Double> all = new HashMap<FaveRankerWrapper, Double>();
		final int listCount = addAllLists(null, hashtag, all);
		// Sort the list
		final List<Map.Entry<FaveRankerWrapper, Double>> sorted = new LinkedList<>(all.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<FaveRankerWrapper, Double>>()
		{
			@Override
			public int compare(final Map.Entry<FaveRankerWrapper, Double> o1, final Map.Entry<FaveRankerWrapper, Double> o2)
			{
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// Add everything to memcache
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
			MemcacheManager.getInstance().putFaveItemScoreNoRerank(entry.getKey().getFaveItem().getId(), hashtag, entry.getValue());
		}

		// Save the master list back to the datastore
		final Hashtag hashtagEntity = ofy().load().type(Hashtag.class).id(hashtag).get();
		// Calculate the zcore to determine top trending lists
		int n = hashtagEntity.getSlidingListCount().size();
		if (n > 0) {
			int total = 0;
			for (Integer count : hashtagEntity.getSlidingListCount()) {
				total += count;
			}
			double avg = total / n;
			double sumsq = 0;
			for (Integer count : hashtagEntity.getSlidingListCount()) {
				sumsq += Math.pow(count - avg, 2);
			}
			double std = Math.sqrt(sumsq / n);
			if (std == 0)
				std = 1;
			hashtagEntity.setZscore((listCount - avg) / std);
		}
		hashtagEntity.addListCount(listCount);
		hashtagEntity.setList(master);
		ofy().save().entity(hashtagEntity).now();

		// And memcache the master
		MemcacheManager.getInstance().putMasterFaveList(hashtag, master);
	}

	// Get favelists 1000 at a time, and store their rank, returns number of lists
	private int addAllLists(final String cursor, final String hashtag, final HashMap<FaveRankerWrapper, Double> all) {
		final Query<FaveList> query = ofy().load().type(FaveList.class).filter("hashtag", hashtag).limit(1000);

		if (cursor != null)
			query.startAt(Cursor.fromWebSafeString(cursor));

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
				final double score = FaveList.calculateItemScore(i);
				final double newVal = (all.get(faveHolder) != null) ? all.get(faveHolder) + score : score;
				all.put(faveHolder, newVal);
				i++;
			}

			// If we processed the full 1000 limit, grab the next batch of hashtags to process
			if (count == 1000)
				shouldContinue = true;
		}

		// While we still have favelists to process, keep adding their ranks
		if (shouldContinue) {
			return addAllLists(iterator.getCursor().toWebSafeString(), hashtag, all) + count;
		}
		return count;
	}
}
