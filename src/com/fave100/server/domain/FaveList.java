package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fave100.server.domain.Activity.Transaction;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;

@Entity @Index
public class FaveList extends DatastoreObject{
	
	@IgnoreSave public static final String SEPERATOR_TOKEN = ":";
	@IgnoreSave public static final String DEFAULT_HASHTAG = "fave100";
	@IgnoreSave public static final int MAX_FAVES = 100;
	
	@Id private String id;
	private Ref<AppUser> user;
	private String hashtag;
	private List<FaveItem> list = new ArrayList<FaveItem>();;
	
	@SuppressWarnings("unused")
	// Request Factory calls this in some way, so need to make sure
	// that we initialize the list
	private FaveList() {		
		//list = new ArrayList<FaveItem>();
	}
	
	public FaveList(final String username, final String hashtag) {
		this.id = username+FaveList.SEPERATOR_TOKEN+hashtag;
		this.user = Ref.create(Key.create(AppUser.class, username));
		this.hashtag = hashtag;		
	}
	
	public static FaveList findFaveList(final String id) {
		return ofy().load().type(FaveList.class).id(id).get();
	}
	
	// TODO: Do FaveList activities need to be transactional? If so, need to set AppUser as parent
	public static void addFaveItemForCurrentUser(final String hashtag, final String songID) {
		
		// TODO: Verify integrity of songProxy on server-side? 
		final AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) {
			throw new RuntimeException("Please log in to complete this action");
			//return false;
		}
		final FaveList faveList = ofy().load().type(FaveList.class).id(currentUser.getUsername()+FaveList.SEPERATOR_TOKEN+hashtag).get();		
		if(faveList.getList().size() >= FaveList.MAX_FAVES) throw new RuntimeException("You cannot have more than 100 songs in list");		
		final Song song = ofy().load().type(Song.class).id(songID).get();		
		
		boolean unique = true;
		// If the song does not exist, create it
		if(song == null) {			
			// Lookup the MBID in Musicbrainz and add to song database
			try {
			    final URL url = new URL("http://musicbrainz.org/ws/2/recording/"+songID+"?inc=artists+releases&fmt=json");
			    final URLConnection conn = url.openConnection();
			    final BufferedReader in = new BufferedReader(new InputStreamReader(
		    		conn.getInputStream()));
		    
				String inputLine;
				String content = "";
				
				while ((inputLine = in.readLine()) != null) {				    
				    content += inputLine;
				}
				in.close();
				Logger.getAnonymousLogger().log(Level.SEVERE, content);
				final JsonParser parser = new JsonParser();
			    final JsonElement element = parser.parse(content);
			    final JsonObject songObject = element.getAsJsonObject();	
			    
			    final Song newSong = new Song();
			    newSong.setId(songObject.get("id").getAsString());
			    newSong.setTrackName(songObject.get("title").getAsString());
			    // TODO: What if more than 1 artist credited?	
			    newSong.setArtistName(songObject.get("artist-credit").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString());
			    Logger.getAnonymousLogger().log(Level.SEVERE, "sadder");
			    
			    // Get the earliest release date
			    // TODO: Getting as JSON seems to strip release list
			    String earliestReleaseDate = "";
			    if(songObject.get("release") != null) {
			    	final JsonArray releaseArray = songObject.get("release").getAsJsonArray();
				    Logger.getAnonymousLogger().log(Level.SEVERE, "length: "+releaseArray.size());
				    for(int i = 0; i < releaseArray.size(); i++) {
				    	Logger.getAnonymousLogger().log(Level.SEVERE,"i:  "+i);
				    	final JsonElement releaseDate = releaseArray.get(i).getAsJsonObject().get("date");
				    	Logger.getAnonymousLogger().log(Level.SEVERE,"The curr relase date is: "+releaseDate.getAsString());
				    	if(releaseDate != null && !releaseDate.getAsString().isEmpty()) {
				    		if(earliestReleaseDate.isEmpty() || releaseDate.getAsInt() > Integer.parseInt(earliestReleaseDate)) {
					    		earliestReleaseDate = releaseDate.getAsString();
					    	}
				    	}			    	
				    }
			    }
			    Logger.getAnonymousLogger().log(Level.SEVERE,"The relase date is: "+earliestReleaseDate);
			    if(earliestReleaseDate != null && !earliestReleaseDate.isEmpty()) {
			    	newSong.setReleaseDate(earliestReleaseDate.substring(0, 4));
			    }			    
			    
			    // Look up the cover art and add it if it exists
				try {
				    final URL coverArtUrl = new URL("http://coverartarchive.org/release/"+songID);
				    final HttpURLConnection coverArtConn = (HttpURLConnection) coverArtUrl.openConnection();
				    final BufferedReader coverArtIn = new BufferedReader(new InputStreamReader(
				    		coverArtConn.getInputStream()));
				    
				    if(coverArtConn.getResponseCode() != 404) {
						String coverArtInputLine;
						String coverArtContent = "";
						
						while ((coverArtInputLine = coverArtIn.readLine()) != null) {				    
							coverArtContent += coverArtInputLine;					   
						}
						coverArtIn.close();
						
						final JsonParser coverArtParser = new JsonParser();
					    final JsonElement coverArtElement = coverArtParser.parse(coverArtContent);
					    final JsonObject coverArtObject = coverArtElement.getAsJsonObject();
					    final JsonArray imagesArray = coverArtObject.get("images").getAsJsonArray();
					    final JsonObject imagesObject = imagesArray.get(0).getAsJsonObject();
					    String imageUrl = "";
					    
					    final JsonObject thumbnailsObject = imagesObject.get("thumbnails").getAsJsonObject();
					    final String smallThumbnail = thumbnailsObject.get("small").getAsString();
					    final String largeThumbnail = thumbnailsObject.get("large").getAsString();
					    
					    // Try to get the smallest image possible
					    if(smallThumbnail != null && !smallThumbnail.isEmpty()) {
					    	imageUrl = smallThumbnail;
					    } else if(largeThumbnail != null && !largeThumbnail.isEmpty()) {
							imageUrl = largeThumbnail;
						} else {
							imageUrl = imagesObject.get("image").getAsString();
						}
					    
					    newSong.setCoverArtUrl(imageUrl);
					    
				    }
				    
				} catch(final Exception e){		
					
				}
				
			    ofy().save().entity(newSong).now();
					
			} catch (final Exception e) {
			}
			
			
			//songProxy.setId(songID);
			//ofy().save().entity(songProxy);
		} else {
			// Check if it is a unique song for this user
			for(final FaveItem faveItem : faveList.getList()) {
				if(faveItem.getSong().get().getId().equals(song.getId())) unique = false;
			}
		}
		if(unique == false) throw new RuntimeException("The song is already in your list");;
		// Create the new FaveItem 		
		final Ref<Song> songRef = Ref.create(Key.create(Song.class, songID));
		final FaveItem newFaveItem = new FaveItem(songID);
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
		// TODO: For now, run on ever page refresh but should really be a background task
		// TODO: Performance critical - optimize! This code is horrible performance-wise!
		final List<Song> allSongs = ofy().load().type(Song.class).list();
		for(final Song song : allSongs) {
			song.setScore(0);
			ofy().save().entity(song).now();
		}		
		final List<AppUser> allAppUsers = ofy().load().type(AppUser.class).list();
		for(final AppUser appUser : allAppUsers) {
			final FaveList faveList = ofy().load().type(FaveList.class).id(appUser.getUsername()+FaveList.SEPERATOR_TOKEN+FaveList.DEFAULT_HASHTAG).get();
			if(faveList != null) {
				for(int i = 0; i < faveList.getList().size(); i++) {
					final Song song = faveList.getList().get(i).getSong().get();
					song.addScore(FaveList.MAX_FAVES - i);
					ofy().save().entity(song).now();
				}
			}				
		}		
		final List<Song> topSongs = ofy().load().type(Song.class).order("-score").limit(100).list();		
		return topSongs;
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
			String whyline = "";
			final Ref<Whyline> whylineRef = faveItem.getWhyline(); 
			if(whylineRef != null) {
				whyline = whylineRef.get().getWhyline();
			}
			song.setWhyline(whyline);
			songArray.add(song);
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
