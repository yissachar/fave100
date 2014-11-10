package com.fave100;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.fave100.server.domain.Song;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.Following;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.shared.Constants;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;

public class RemoteApi {

	public static void main(String[] args) throws IOException {

		String pw = "";
		RemoteApiOptions options = new RemoteApiOptions()
				.server("", 443)
				.credentials("", pw);

		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(Whyline.class);
		ObjectifyService.register(Hashtag.class);
		ObjectifyService.register(Following.class);
		ObjectifyService.register(Song.class);

		RemoteApiInstaller installer = new RemoteApiInstaller();

		installer.install(options);
		try {
			// Add your code here	    	
		}
		finally {
			System.out.println("Done");
			installer.uninstall();
		}
	}

	public static void suggestReplacement(String oldId) {
		printSongLists(oldId);

		FaveItem oldSong = findSong(oldId);
		if (oldSong == null) {
			System.out.println("No song with that id exists");
			return;
		}

		System.out.println();
		String jsonp = search(oldSong.getSong() + " " + oldSong.getArtist());
		String json = jsonp.substring(jsonp.indexOf("(") + 1, jsonp.lastIndexOf(")"));
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(json).getAsJsonObject();
		JsonArray jsonArray = jsonObject.get("results").getAsJsonArray();
		if (jsonArray.size() == 0) {
			System.out.println("No suitable replacement found");
			return;
		}

		System.out.println(jsonArray.size() + " potential replacements found: ");
		List<JsonObject> replacements = new ArrayList<JsonObject>();
		int i = 1;
		for (final JsonElement result : jsonArray) {
			JsonObject songObj = result.getAsJsonObject();
			replacements.add(songObj);

			String newId = songObj.get("id").getAsString();
			String song = songObj.get("song").getAsString();
			String artist = songObj.get("artist").getAsString();

			System.out.println(i + ": " + newId + ": " + song + " - " + artist);
			i++;
		}

		System.out.println();
		System.out.println("Replace song? 1-5 to select replacement, n or any other character to cancel");

		Scanner scanner = new Scanner(System.in);
		String in = scanner.nextLine();
		System.out.println();

		try {
			int index = Integer.parseInt(in);
			if (index > replacements.size()) {
				System.out.println("Invalid index - cancelling");
				return;
			}

			JsonObject songObj = replacements.get(index - 1);

			String newId = songObj.get("id").getAsString();
			String song = songObj.get("song").getAsString();
			String artist = songObj.get("artist").getAsString();

			replaceSong(oldId, newId, song, artist);
			printSongLists(newId);
		}
		catch (Exception e) {
			return;
		}
	}

	public static String search(final String searchTerm) {
		URL url;
		HttpURLConnection connection = null;
		String urlParameters = "";
		try {
			urlParameters = URLEncoder.encode(searchTerm, "UTF-8");
		}
		catch (final UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			//Create connection
			url = new URL(Constants.SEARCH_URL + "callback=callback&searchTerm=" + urlParameters);
			connection = (HttpURLConnection)url.openConnection();

			//Get Response	
			final InputStream is = connection.getInputStream();
			final BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line;
			final StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		}
		catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static String lookup(final String id) {
		URL url;
		HttpURLConnection connection = null;
		String urlParameters = "";
		try {
			urlParameters = URLEncoder.encode(id, "UTF-8");
		}
		catch (final UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			//Create connection
			url = new URL(Constants.LOOKUP_URL + "id=" + urlParameters);
			connection = (HttpURLConnection)url.openConnection();

			//Get Response	
			final InputStream is = connection.getInputStream();
			final BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line;
			final StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		}
		catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static void reindexLists() {
		List<Hashtag> lists = ofy().load().type(Hashtag.class).list();
		ofy().save().entities(lists).now();
	}

	public static void reindexWhylines() {
		List<FaveList> faveLists = ofy().load().type(FaveList.class).list();
		for (FaveList faveList : faveLists) {
			for (FaveItem faveItem : faveList.getList()) {
				Ref<Whyline> whylineRef = faveItem.getWhylineRef();
				if (whylineRef != null) {
					Whyline whylineEntity = (Whyline)ofy().load().value(whylineRef).now();
					if (whylineEntity != null) {
						whylineEntity.setList(faveList.getHashtag());
						ofy().save().entity(whylineEntity).now();
					}
				}
			}
		}
	}

	public static void makeAdmin(String username) {
		AppUser admin = ofy().load().type(AppUser.class).id(username).now();
		admin.setAdmin(true);
		ofy().save().entity(admin).now();
	}

	public static FaveItem findSong(String id) {
		List<FaveList> faveLists = ofy().load().type(FaveList.class).filter("list.songID =", id).list();

		for (FaveList faveList : faveLists) {
			for (FaveItem faveItem : faveList.getList()) {
				if (faveItem.getSongID().equals(id)) {
					return faveItem;
				}
			}
		}

		return null;
	}

	public static void printSongLists(String id) {
		List<FaveList> faveLists = ofy().load().type(FaveList.class).filter("list.songID =", id).list();

		for (FaveList faveList : faveLists) {
			for (FaveItem faveItem : faveList.getList()) {
				if (faveItem.getSongID().equals(id)) {
					System.out.println("Found " + faveItem.getSongID() + ": \"" + faveItem.getSong() + " - " + faveItem.getArtist() + "\" in " + faveList.getId());
				}
			}
		}
	}

	public static void replaceSong(String id, String newId) {
		String json = lookup(newId);
		JsonParser parser = new JsonParser();
		JsonObject songObj = parser.parse(json).getAsJsonObject();
		replaceSong(id, newId, songObj.get("song").getAsString(), songObj.get("artist").getAsString());
	}

	public static void replaceSong(String id, String newId, String song, String artist) {
		List<FaveList> faveLists = ofy().load().type(FaveList.class).filter("list.songID =", id).list();

		for (FaveList faveList : faveLists) {
			for (FaveItem faveItem : faveList.getList()) {
				if (faveItem.getSongID().equals(id)) {
					faveItem.setSong(song);
					faveItem.setArtist(artist);
					faveItem.setSongID(newId);
					Ref<Whyline> whylineRef = faveItem.getWhylineRef();
					if (whylineRef != null) {
						Whyline whyline = whylineRef.get();
						whyline.setSong(Ref.create(Key.create(Song.class, newId)));
						ofy().save().entity(whyline).now();
					}
				}
			}
		}
		ofy().save().entities(faveLists).now();
		System.out.println("Replaced:");
		System.out.println(id + " ----> " + newId);
		System.out.println(id.replaceAll(".", " ") + " ----> " + song);
		System.out.println(id.replaceAll(".", " ") + " ----> " + artist);
	}

	public static void printAllSongs() {
		Set<String> songIds = new HashSet<String>();

		List<FaveList> faveLists = ofy().load().type(FaveList.class).list();
		for (FaveList faveList : faveLists) {
			for (FaveItem faveItem : faveList.getList()) {
				songIds.add(faveItem.getSongID());
			}
			System.out.println("Processed list - " + faveList.getId());
		}

		for (String songId : songIds) {
			System.out.println(songId);
		}
	}
}
