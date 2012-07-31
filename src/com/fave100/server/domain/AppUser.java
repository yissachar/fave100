package com.fave100.server.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import com.fave100.server.DAO;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.annotation.Entity;

/**
 * A Fave100 user.
 * @author yissachar.radcliffe
 *
 */
@Entity
public class AppUser{
	
	@Id private String username;
	private Integer version = 0;
	private String googleId;
	private String email;
	@Embedded private List<FaveItem> fave100Songs = new ArrayList<FaveItem>();
	//TODO: Add user types (normal, reviewer, celebrity)?
	
	public static final Objectify ofy() {
		//TODO: find better way of getting Objectify instance
		DAO dao = new DAO();
		return dao.ofy();
	}
	
	public static AppUser findAppUser(String username) {
		return ofy().get(new Key<AppUser>(AppUser.class, username));
	}
	
	public static AppUser findAppUserByGoogleId(String googleId) {
		return ofy().query(AppUser.class).filter("googleId", googleId).get();		
	}
	
	public static boolean isGoogleUserLoggedIn() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {
			return true;
		}
		return false;
	}
	
	public static AppUser getLoggedInAppUser() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {
			//TODO: This will only work if Google Id is enforced as unique
			AppUser appUser = findAppUserByGoogleId(user.getUserId());
			if(appUser != null) {
				return appUser;
			}
		} 
		return null;
	}	
	
	public static AppUser createAppUserFromCurrentGoogleUser(String username) {		
		//TODO: Disallow white-space, other special characters?
		if(ofy().find(AppUser.class, username) != null) {
			return null;
		} else {
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			AppUser appUser = new AppUser();
			appUser.setUsername(username);
			appUser.setEmail(user.getEmail());
			appUser.setGoogleId(user.getUserId());
			return(appUser.persist());
		}
		// TODO: Use transactions to prevent duplicate user entries
		/*Transaction txn = ofy().getTxn();
		try {
			// Verify that username is unique
			//if(ofy().find(AppUser.class, user.getNickname()) != null) {
			//	return null; 
			//} 
			AppUser appUser = new AppUser();
			appUser.setUsername(user.getNickname());
			appUser.setEmail(user.getEmail());
			appUser.setGoogleId(user.getUserId());
			AppUser foo = appUser.persist();
			txn.commit();
			return(foo);
		} finally {
			if(txn.isActive()) {
				txn.rollback();
				return null;
			}
		}*/
	}
	
	public static void addFaveItemForCurrentUser(Long songID, Song songProxy) {
		// TODO: Verify integrity of songProxy on server-side? 
		// TODO: Only allow user to store 100 faveItems
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;
		Song song = ofy().find(Song.class, songID);
		// If the song does not exist, create it
		if(song == null) {
			songProxy.setId(songID);
			songProxy.persist();
		}		
		// Create the new FaveItem 
		FaveItem newFaveItem = new FaveItem();		
		newFaveItem.setSong(new Key<Song>(Song.class, songID));
		currentUser.fave100Songs.add(newFaveItem);
		currentUser.persist();
	}
	
	public static void removeFaveItemForCurrentUser(int index) {
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;	
		currentUser.fave100Songs.remove(index);
		currentUser.persist();		
	}
	
	public static void rerankFaveItemForCurrentUser(int currentIndex, int newIndex) {
		//TODO: Verify that this is working successfully, eventual consistency makes it hard to know
		//TODO: Need transaction locking here, to prevent override in middle
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;	
//		FaveItem faveAtCurrIndex = currentUser.fave100Songs.get(currentIndex);
//		FaveItem faveAtNewIndex = currentUser.fave100Songs.get(newIndex);
//		currentUser.fave100Songs.set(currentIndex, faveAtNewIndex);
//		currentUser.fave100Songs.set(newIndex, faveAtCurrIndex);
		FaveItem faveAtCurrIndex = currentUser.fave100Songs.remove(currentIndex);
		currentUser.fave100Songs.add(newIndex, faveAtCurrIndex);
		currentUser.persist();		
	}
	
	public static List<FaveItem> getAllSongsForCurrentUser() {
		// TODO: This absolutely needs to be highly consistent
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return null;
		// Get the song keys from the FaveItem list
		List<Key<Song>> songKeys = new ArrayList<Key<Song>>();
		for(FaveItem faveItem : currentUser.fave100Songs) {
			songKeys.add(faveItem.getSong());
		}
		// Now that we have song keys, get actual songs in batch get
		Map<Key<Song>, Song> songsForFaveItems = ofy().get(songKeys);
		// Add the song data to the FaveItems that we will send back to the client
		for(FaveItem faveItem : currentUser.fave100Songs) {
			Song song = songsForFaveItems.get(faveItem.getSong());
			faveItem.setTrackName(song.getTrackName());
			faveItem.setArtistName(song.getArtistName());
			faveItem.setTrackViewUrl(song.getTrackViewUrl());
			faveItem.setReleaseYear(song.getReleaseYear());
		}
		return currentUser.fave100Songs;
	}
	
	public AppUser persist() {
		ofy().put(this);
		return this;
	}
	
	/**
     * Auto-increment version # whenever persisted
     */
    @PrePersist
    void onPersist()
    {
        this.version++;
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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<FaveItem> getFave100Songs() {
		return fave100Songs;
	}

	public void setFave100Songs(List<FaveItem> fave100Songs) {
		this.fave100Songs = fave100Songs;
	}
}
