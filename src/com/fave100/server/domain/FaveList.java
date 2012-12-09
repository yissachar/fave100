package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fave100.client.pages.search.SearchPresenter;
import com.fave100.server.domain.Activity.Transaction;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.cmd.QueryKeys;

@Entity
public class FaveList extends DatastoreObject{

	@IgnoreSave public static final String SEPERATOR_TOKEN = ":";
	@IgnoreSave public static final String DEFAULT_HASHTAG = "fave100";
	@IgnoreSave public static final int MAX_FAVES = 100;

	@Id private String id;
	private Ref<AppUser> user;
	private String hashtag;
	private List<FaveItem> list = new ArrayList<FaveItem>();;

	@SuppressWarnings("unused")
	private FaveList() {}

	public FaveList(final String username, final String hashtag) {
		this.id = username+FaveList.SEPERATOR_TOKEN+hashtag;
		this.user = Ref.create(Key.create(AppUser.class, username));
		this.hashtag = hashtag;
	}

	public static FaveList findFaveList(final String id) {
		return ofy().load().type(FaveList.class).id(id).get();
	}

	// TODO: Do FaveList activities need to be transactional? If so, need to set AppUser as parent
	public static void addFaveItemForCurrentUser(final String hashtag, final String songID,
			final String songTitle, final String artist)
					throws NotLoggedInException, SongLimitReachedException, SongAlreadyInListException {

		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) {
			throw new NotLoggedInException();
		}
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();
		if(faveList.getList().size() >= FaveList.MAX_FAVES) throw new SongLimitReachedException();

		// See if the song exists in our datastore
		Song song = Song.findSongByTitleAndArtist(songTitle, artist);

		boolean unique = true;
		// If the song does not exist, create it
		if(song == null) {
			// Look up the song in the SQL database and add to AppEngine datastore
			try {
			    final URL url = new URL(SearchPresenter.BASE_SEARCH_URL+"lookup?song="+songTitle.replace(" ", "+")+"&artist="+artist.replace(" ", "+"));
			    final URLConnection conn = url.openConnection();
			    final BufferedReader in = new BufferedReader(new InputStreamReader(
		    		conn.getInputStream(), "UTF-8"));

				String inputLine;
				String content = "";

				while ((inputLine = in.readLine()) != null) {
				    content += inputLine;
				}
				in.close();

				final JsonParser parser = new JsonParser();
			    final JsonElement element = parser.parse(content);
			    final JsonObject songObject = element.getAsJsonObject();

			    final String title = songObject.get("song").getAsString();
			    final String songArtist = songObject.get("artist").getAsString();
			    final String mbid = songObject.get("mbid").getAsString();
			    String youTubeId = "";
			    final JsonElement youTubeElement = songObject.get("youtube_id");
			    if(youTubeElement != null) {
			    	youTubeId = youTubeElement.getAsString();
			    }
			    final Song newSong = new Song(title, songArtist, mbid);
			    if(!youTubeId.isEmpty()) {
			    	newSong.setYouTubeId(youTubeId);
			    }

			    // Before saving double-check that we do not have this record
			    // (Since we cannot really trust that the passed songTitle+artist is valid
			    if(Song.findSongByTitleAndArtist(newSong.getTrackName(), newSong.getArtistName()) == null) {
			    	ofy().save().entity(newSong).now();
			    	song = newSong;
			    }

			} catch (final Exception e) {
				e.printStackTrace();
			}

		} else {
			// Check if it is a unique song for this user
			for(final FaveItem faveItem : faveList.getList()) {
				if(faveItem.getSong().equals(Ref.create(Key.create(Song.class, song.getId())))){
					unique = false;
				}
			}
		}
		if(unique == false) throw new SongAlreadyInListException();;
		// Create the new FaveItem
		final String songArtistID = song.getTrackName()+Song.TOKEN_SEPARATOR+song.getArtistName();
		final Ref<Song> songRef = Ref.create(Key.create(Song.class, songArtistID));
		final FaveItem newFaveItem = new FaveItem(songArtistID);
		faveList.getList().add(newFaveItem);
		final Activity activity = new Activity(currentUser.getUsername(), Transaction.FAVE_ADDED);
		activity.setSong(songRef);
		ofy().save().entities(activity, faveList).now();
	}

	public static void removeFaveItemForCurrentUser(final String hashtag, final int index) {
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();
		if(faveList == null) return;
		final Activity activity = new Activity(currentUser.getUsername(), Transaction.FAVE_REMOVED);
		activity.setSong(faveList.getList().get(index).getSong());
		// We must also delete the whyline if it exists
		final Ref<Whyline> currentWhyline = faveList.getList().get(index).getWhyline();
		if(currentWhyline != null) {
			ofy().delete().entity(currentWhyline).now();
		}
		faveList.getList().remove(index);
		ofy().save().entities(activity, faveList).now();
	}

	public static void rerankFaveItemForCurrentUser(final String hashtag, final int currentIndex, final int newIndex) {
		// TODO: Use a transaction to ensure that the indices are correct
		// For some reason this throws a illegal state exception about deregistering a transaction that is not registered
//		ofy().transact(new VoidWork() {
//			public void vrun() {
				final AppUser currentUser = AppUser.getLoggedInAppUser();
				if(currentUser == null) return;
				final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();
				if(faveList == null) return;
				final Activity activity = new Activity(currentUser.getUsername(), Transaction.FAVE_POSITION_CHANGED);
				activity.setSong(faveList.getList().get(currentIndex).getSong());
				activity.setPreviousLocation(currentIndex+1);
				activity.setNewLocation(newIndex+1);
				final FaveItem faveAtCurrIndex = faveList.getList().remove(currentIndex);
				faveList.getList().add(newIndex, faveAtCurrIndex);
				ofy().save().entities(activity, faveList).now();
//			}
//		});
	}

	public static void editWhylineForCurrentUser(final String hashtag, final int index, final String whyline) {
		//TODO: Sanitize the string
		//TODO: Length restriction?
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();
		if(faveList == null) return;
		final FaveItem faveItem = faveList.getList().get(index);
		final Ref<Whyline> currentWhyline = faveItem.getWhyline();
		if(currentWhyline == null) {
			// Need to create an external whyline
			final Song song = faveItem.getSong().get();
			final Whyline whylineEntity = new Whyline(whyline, song.getId(), currentUser.getUsername());
			final String whylineID = currentUser.getUsername()+Whyline.SEPERATOR_TOKEN+song.getId();
			faveItem.setWhyline(Ref.create(Key.create(Whyline.class, whylineID)));
			ofy().save().entities(faveList, whylineEntity).now();
		} else {
			// Just modify the existing whyline
			currentWhyline.get().setWhyline(whyline);
			ofy().save().entity(currentWhyline).now();
		}
	}


	public static List<Song> getMasterFaveList() {
		setMasterFaveList();

		final List<Song> topSongs = new ArrayList<Song>();
		final List<Ref<Song>> songRefs = ofy().load().type(Fave100MasterList.class).id("current").get().getSongList();
		for(final Ref<Song> songRef : songRefs) {
			topSongs.add(songRef.get());
		}
		return topSongs;
	}

	public static void setMasterFaveList() {
		// TODO: Once fully on AppEngine, turn into background task
		// TODO: Performance critical - optimize! This code is horrible performance-wise!
		final List<Song> allSongs = ofy().load().type(Song.class).list();
		for(final Song song : allSongs) {
			song.setScore(0);
			ofy().save().entity(song).now();
		}
		final List<FaveList> allFaveLists = ofy().load().type(FaveList.class).list();
		for(final FaveList faveList : allFaveLists) {
			for(int i = 0; i < faveList.getList().size(); i++) {
				final Song song = faveList.getList().get(i).getSong().get();
				if(song != null) {
					song.addScore(FaveList.MAX_FAVES - i);
					ofy().save().entity(song).now();
				}
			}
		}

		final Fave100MasterList newMasterList = new Fave100MasterList("current");
		final QueryKeys<Song> songKeys = ofy().load().type(Song.class).order("-score").limit(100).keys();
		final List<Ref<Song>> songRefs = new ArrayList<Ref<Song>>();
		for(final Key<Song> songKey : songKeys) {
			songRefs.add(Ref.create(songKey));
		}
		newMasterList.setSongList(songRefs);
		ofy().save().entity(newMasterList).now();
	}

	public static List<Song> getFaveItemsForCurrentUser(final String hashtag) {
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return null;
		return getFaveList(currentUser.getUsername(), hashtag);

	}

	public static List<Song> getFaveList(final String username, final String hashtag) {
		final FaveList faveList = ofy().load().type(FaveList.class).id(username+FaveList.SEPERATOR_TOKEN+hashtag).get();
		if(faveList == null) return null;
		final ArrayList<Song> songArray = new ArrayList<Song>();
		for(final FaveItem faveItem : faveList.getList()) {
			final Song song = faveItem.getSong().get();
			if(song != null) {
				String whyline = "";
				final Ref<Whyline> whylineRef = faveItem.getWhyline();
				if(whylineRef != null) {
					whyline = whylineRef.get().getWhyline();
				}
				song.setWhyline(whyline);
				songArray.add(song);
			} else {
				Logger.getAnonymousLogger().log(Level.SEVERE, "Null song entry for favelist "+faveList.id);
			}
		}
		return songArray;
	}


	/* Getters and Setters */

	public String getId() {
		return id;
	}
	public void setId(final String id) {
		this.id = id;
	}
	public Ref<AppUser> getUser() {
		return user;
	}
	public void setUser(final Ref<AppUser> user) {
		this.user = user;
	}
	public String getHashtag() {
		return hashtag;
	}
	public void setHashtag(final String hashtag) {
		this.hashtag = hashtag;
	}
	public List<FaveItem> getList() {
		return list;
	}

}
