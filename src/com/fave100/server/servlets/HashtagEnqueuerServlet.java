package com.fave100.server.servlets;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fave100.server.domain.favelist.Hashtag;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.googlecode.objectify.cmd.Query;

/**
 * This servlet will periodically be called as a cron job, to add
 * each hashtag to a queue where a master list will be built
 * 
 * @author yissachar.radcliffe
 * 
 */
@SuppressWarnings("serial")
public class HashtagEnqueuerServlet extends HttpServlet
{
	public static String HASHTAG_ENQUEUER_URL = "/cron/hashtags";
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
			final Queue queue = QueueFactory.getQueue("hashtag-queue");
			queue.add(withUrl(HashtagBuilderServlet.HASHTAG_BUILDER_URL).param(HashtagBuilderServlet.HASHTAG_PARAM, iterator.next().getId()));

			// If we processed the full 1000 limit, grab the next batch of hashtags to process
			if (count == 1000)
				shouldContinue = true;
		}

		// While we still have hashtags to process, keep hitting the cron URL with new cursor position
		if (shouldContinue) {
			res.sendRedirect(HASHTAG_ENQUEUER_URL + "?" + CURSOR_PARAM + "=" + iterator.getCursor().toWebSafeString());
		}
	}
}
