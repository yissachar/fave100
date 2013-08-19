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

import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveRankerWrapper;
import com.fave100.server.domain.favelist.Hashtag;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

/**
 * This servlet will periodically be called as a cron job, to add
 * each hashtag to a queue where a master list will be built
 * 
 * @author yissachar.radcliffe
 * 
 */
@SuppressWarnings("serial")
public class HashtagBuilderServlet extends HttpServlet
{
	public static String HASHTAG_BUILDER_URL = "/cron/hashtags";
	public static String CURSOR_PARAM = "cursor";

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse res)
			throws ServletException, IOException {

		// Get hashtags 1000 at a time, and add them to hashtag builder queue
		final Query<Hashtag> query = ofy().load().type(Hashtag.class).limit(1000);
		final String cursor = req.getParameter(CURSOR_PARAM);
		if (cursor != null)
			query.startAt(Cursor.fromWebSafeString(cursor));

		boolean shouldContinue = false;

		int count = 0;
		final QueryResultIterator<Hashtag> iterator = query.iterator();
		while (iterator.hasNext()) {
			count++;
			// TODO: Add hashtag to queue to process, instead of calculating here
			final Hashtag hashtag = iterator.next();
			final HashMap<FaveRankerWrapper, Integer> all = new HashMap<FaveRankerWrapper, Integer>();

			final List<FaveList> faveLists = ofy().load().type(FaveList.class).filter("hashtag", hashtag.getId()).list();
			for (final FaveList faveList : faveLists) {
				for (final FaveItem faveItem : faveList.getList()) {
					final FaveRankerWrapper faveHolder = new FaveRankerWrapper(faveItem);
					final int newVal = (all.get(faveHolder) != null) ? all.get(faveHolder) + 1 : 1;
					all.put(faveHolder, newVal);
				}
			}

			final List<Map.Entry<FaveRankerWrapper, Integer>> sorted = new LinkedList<Map.Entry<FaveRankerWrapper, Integer>>(all.entrySet());
			Collections.sort(sorted, new Comparator<Map.Entry<FaveRankerWrapper, Integer>>()
			{
				@Override
				public int compare(final Map.Entry<FaveRankerWrapper, Integer> o1, final Map.Entry<FaveRankerWrapper, Integer> o2)
				{
					return (o2.getValue()).compareTo(o1.getValue());
				}
			});

			int i = 0;
			final List<FaveItem> master = new ArrayList<FaveItem>();
			for (final Map.Entry<FaveRankerWrapper, Integer> entry : sorted) {
				if (i >= 100)
					break;
				master.add(entry.getKey().getFaveItem());
				i++;
			}

			hashtag.setList(master);
			ofy().save().entity(hashtag).now();

			if (count == 1000)
				shouldContinue = true;
		}

		// While we still have hashtags to process, keep hitting the cron URL with new cursor position
		if (shouldContinue) {
			res.sendRedirect(HASHTAG_BUILDER_URL + "?" + CURSOR_PARAM + "=" + iterator.getCursor().toWebSafeString());
		}
	}
}
