package com.fave100.server.domain.favelist;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.Random;

import com.fave100.shared.Constants;
import com.google.api.server.spi.config.Api;

@Api(name = "fave100", version = "v1")
public class Fave100Api {

	public List<FaveItem> listFaveItems() {
		return ofy().load().type(Hashtag.class).id(Constants.DEFAULT_HASHTAG).get().getList();
	}

	private String getRandomString() {
		return getRandomString(20);
	}

	private String getRandomString(int size) {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}
}
