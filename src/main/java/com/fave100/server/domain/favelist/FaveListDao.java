package com.fave100.server.domain.favelist;

import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.inject.Named;

public class FaveListDao {

	public static final String SEPERATOR_TOKEN = ":";
	public static final int MAX_FAVES = 100;

	public static FaveList findFaveList(final String id) {
		return ofy().load().type(FaveList.class).id(id).now();
	}

	public static FaveList findFaveList(final String username, final String hashtag) {
		return findFaveList(username.toLowerCase() + FaveListDao.SEPERATOR_TOKEN + hashtag.toLowerCase());
	}

	public static double calculateItemScore(@Named final int position) {
		return ((double)(-1 * position) / 11 + ((double)111 / 11));
	}

}
