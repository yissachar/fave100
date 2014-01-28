package com.fave100.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.servlet.http.HttpServletRequest;

import com.fave100.server.domain.Session;
import com.fave100.shared.Constants;

/*
 * Helper methods for dealing with sessions. All interactions with sessions should
 * go through this class. Google Cloud Endpoints does not manage sessions properly
 * so we must manually manage them. 
 */
public class SessionHelper {

	public static Session getSession(HttpServletRequest request) {

		String sessionId = request.getHeader(Constants.SESSION_HEADER);

		Session session = null;

		// Retrieve the session from the datastore
		if (sessionId != null)
			session = ofy().load().type(Session.class).id(sessionId).get();

		// If the retrieved session is already expired, delete it
		if (session.isExpired()) {
			ofy().delete().entity(session).now();
			session = null;
		}

		// If there is no existing session (or we deleted the expired session), create a new one
		if (session == null)
			session = new Session();

		return session;

	}
}
