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

		final HashMap<FaveRankerWrapper, Integer> all = new HashMap<FaveRankerWrapper, Integer>();
		addAllLists(null, hashtag, all);

		// Sort the list
		final List<Map.Entry<FaveRankerWrapper, Integer>> sorted = new LinkedList<Map.Entry<FaveRankerWrapper, Integer>>(all.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<FaveRankerWrapper, Integer>>()
		{
			@Override
			public int compare(final Map.Entry<FaveRankerWrapper, Integer> o1, final Map.Entry<FaveRankerWrapper, Integer> o2)
			{
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// Add everything to memcache
		int i = 0;
		final List<FaveItem> master = new ArrayList<FaveItem>();
		for (final Map.Entry<FaveRankerWrapper, Integer> entry : sorted) {
			// Add the top 100 songs to master list
			if (i < 100) {
				master.add(entry.getKey().getFaveItem());
				i++;
			}
			MemcacheManager.getInstance().putFaveItemScoreNoRerank(entry.getKey().getFaveItem().getId(), hashtag, entry.getValue());
		}

		// Save the master list back to the datastore
		final Hashtag hashtagEntity = ofy().load().type(Hashtag.class).id(hashtag).get();
		hashtagEntity.setList(master);
		ofy().save().entity(hashtagEntity).now();

		// And memcache the master
		MemcacheManager.getInstance().putMasterFaveList(hashtag, master);
	}

	// Get favelists 1000 at a time, and store their rank
	private void addAllLists(final String cursor, final String hashtag, final HashMap<FaveRankerWrapper, Integer> all) {
		final Query<FaveList> query = ofy().load().type(FaveList.class).filter("hashtag", hashtag).limit(1000);

		if (cursor != null)
			query.startAt(Cursor.fromWebSafeString(cursor));

		boolean shouldContinue = false;

		int count = 0;
		final QueryResultIterator<FaveList> iterator = query.iterator();
		while (iterator.hasNext()) {
			count++;
			// Add up the total rank for each song in the list
			for (final FaveItem faveItem : iterator.next().getList()) {
				final FaveRankerWrapper faveHolder = new FaveRankerWrapper(faveItem);
				final int newVal = (all.get(faveHolder) != null) ? all.get(faveHolder) + 1 : 1;
				all.put(faveHolder, newVal);
			}

			// If we processed the full 1000 limit, grab the next batch of hashtags to process
			if (count == 1000)
				shouldContinue = true;
		}

		// While we still have favelists to process, keep adding their ranks
		if (shouldContinue) {
			addAllLists(iterator.getCursor().toWebSafeString(), hashtag, all);
		}
	}
}
