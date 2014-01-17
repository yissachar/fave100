package com.fave100.server.domain.favelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.api.server.spi.config.Api;

@Api(name = "fave100", version = "v1")
public class Fave100Api {

	public List<FaveItem> listFaveItems() {
		List<FaveItem> messages = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			FaveItem message1 = new FaveItem(getRandomString(), getRandomString(), getRandomString());
			messages.add(message1);
		}
		return messages;
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
