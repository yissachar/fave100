package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;

/**
 * A Fave100 user.
 * @author yissachar.radcliffe
 *
 */
@Entity
public class AppUser extends DatastoreObject{

	@IgnoreSave public static int MAX_FAVES = 100;
	
	@Id private String username;//TODO: username case sensitive??
	private String googleId;
	private String email;
	@Embed private List<FaveItem> fave100Songs = new ArrayList<FaveItem>();
	// TODO: user avatar/gravatar
	
	public static AppUser findAppUser(String username) {
		return ofy().load().type(AppUser.class).id(username).get();
	}
	
	public static AppUser findAppUserByGoogleId(String googleID) {
		GoogleID gId = ofy().load().type(GoogleID.class).id(googleID).get();
		if(gId != null) {			
			return ofy().load().type(AppUser.class).id(gId.getUsername()).get();
		} else {
			return null;
		}			
	}
	
	public static boolean isGoogleUserLoggedIn() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {
			return true;
		}
		return false;
	}
	
	public static String getLoginLogoutURL(String destinationURL) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user == null) {
			return userService.createLoginURL(destinationURL);
		}
		return userService.createLogoutURL(destinationURL);
	}
	
	public static AppUser getLoggedInAppUser() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {		
			AppUser appUser = findAppUserByGoogleId(user.getUserId());
			if(appUser != null) {
				return appUser;
			}
		} 
		return null;
	}	
	
	public static AppUser createAppUserFromCurrentGoogleUser(final String username) {		
		// TODO: Disallow white-space, other special characters?		
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely 
		AppUser newAppUser = ofy().transact(new Work<AppUser>() {
			public AppUser run() {
				UserService userService = UserServiceFactory.getUserService();
				User user = userService.getCurrentUser();
				if(ofy().load().type(AppUser.class).id(username).get() != null
					|| ofy().load().type(GoogleID.class).id(user.getUserId()).get() != null) {
					return null;
				} else {
					// Create the user
					AppUser appUser = new AppUser();
					appUser.setUsername(username);
					appUser.setEmail(user.getEmail());
					appUser.setGoogleId(user.getUserId());
					// Create the GoogleID lookup
					GoogleID googleID = new GoogleID(user.getUserId(), username);			
					ofy().save().entities(appUser, googleID).now();
					return appUser;
				}
			}			
		});		
		return newAppUser;		
	}
	
	public static List<AppUser> getAppUsers() {
		// TODO: Add parameters to restrict amount of users returned
		// or otherwise decide how best to show list of users in UI
		return ofy().load().type(AppUser.class).list();
	}
	
	public static void addFaveItemForCurrentUser(Long songID, Song songProxy) {
		// TODO: Verify integrity of songProxy on server-side? 
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;
		// TODO: Show some user friendly message instead of silent fail
		if(currentUser.fave100Songs.size() >= AppUser.MAX_FAVES) return;		
		Song song = ofy().load().type(Song.class).id(songID).get();		
		boolean unique = true;
		// If the song does not exist, create it
		if(song == null) {
			songProxy.setId(songID);
			ofy().save().entity(songProxy);
		} else {
			// Check if it is a unique song for this user
			for(FaveItem faveItem : currentUser.fave100Songs) {
				if(faveItem.getSong().get().getId().equals(song.getId())) unique = false;
			}
		}
		// TODO: Show some user friendly message instead of silent fail
		if(unique == false) return;
		// Create the new FaveItem 
		FaveItem newFaveItem = new FaveItem();		
		newFaveItem.setSong(Ref.create(Key.create(Song.class, songID)));
		currentUser.fave100Songs.add(newFaveItem);
		ofy().save().entity(currentUser);
	}
	
	public static void removeFaveItemForCurrentUser(int index) {
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;
		currentUser.fave100Songs.remove(index);
		ofy().save().entity(currentUser);
	}
	
	public static void rerankFaveItemForCurrentUser(final int currentIndex, final int newIndex) {		
		// TODO: Use a transaction to ensure that the indices are correct
		// For some reason this throws a illegal state exception about deregistering a transaction that is not registered
//		ofy().transact(new VoidWork() {
//			public void vrun() {
				AppUser currentUser = AppUser.getLoggedInAppUser();
				if(currentUser == null) return;	
				FaveItem faveAtCurrIndex = currentUser.fave100Songs.remove(currentIndex);
				currentUser.fave100Songs.add(newIndex, faveAtCurrIndex);
				ofy().save().entity(currentUser).now();
//			}
//		});		
	}
	
	public static List<FaveItem> getAllSongsForCurrentUser() {
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return null;
		for(FaveItem faveItem : currentUser.fave100Songs) {
			Song song = faveItem.getSong().get();
			faveItem.setTrackName(song.getTrackName());
			faveItem.setArtistName(song.getArtistName());
			faveItem.setTrackViewUrl(song.getTrackViewUrl());
			faveItem.setReleaseYear(song.getReleaseYear());
		}
		return currentUser.fave100Songs;
	}
	
	public static List<FaveItem> getMasterFaveList() {
		// TODO: For now, run on ever page refresh but should really be a background task
		// TODO: Performance critical - optimize! This code is horrible performance-wise!
		List<Song> allSongs = ofy().load().type(Song.class).list();
		for(Song song : allSongs) {
			song.setScore(0);
			ofy().save().entity(song).now();
		}		
		List<AppUser> allAppUsers = ofy().load().type(AppUser.class).list();
		for(AppUser appUser : allAppUsers) {
			for(int i = 0; i < appUser.fave100Songs.size(); i++) {
				Song song = appUser.fave100Songs.get(i).getSong().get();
				song.addScore(AppUser.MAX_FAVES - i);
				ofy().save().entity(song).now();
			}
		}		
		// TODO: Order not working. Random order for some reason.
		List<Song> topSongs = ofy().load().type(Song.class).order("-score").limit(100).list();
		List<FaveItem> masterFaveList = new ArrayList<FaveItem>();
		for(Song song : topSongs) {		
			FaveItem faveItem = new FaveItem();
			faveItem.setTrackViewUrl(song.getTrackViewUrl());
			faveItem.setTrackName(song.getTrackName());
			faveItem.setArtistName(song.getArtistName());
			faveItem.setReleaseYear(song.getReleaseYear());			
			masterFaveList.add(faveItem);
		}
		return masterFaveList;
	}
	
    // Getters and setters

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getId() {
		return username;
	}

	public List<FaveItem> getFave100Songs() {
		return fave100Songs;
	}

	public void setFave100Songs(List<FaveItem> fave100Songs) {
		this.fave100Songs = fave100Songs;
	}
}
