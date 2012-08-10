package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.fave100.server.bcrypt.BCrypt;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;

/**
 * A Fave100 user.
 * @author yissachar.radcliffe
 *
 */
@Entity
@Index
public class AppUser extends DatastoreObject{//TODO: remove indexes before launch

	@IgnoreSave public static final int MAX_FAVES = 100;
	@IgnoreSave public static final String AUTH_USER = "loggedIn";
	
	@Id private String username;//TODO: username case sensitive??
	private String password;
	private String googleId;
	private String email;
	@Embed private List<FaveItem> fave100Songs = new ArrayList<FaveItem>();// TODO: Plan ahead for hashtags
	// TODO: user avatar/gravatar
	
	public AppUser() {}
	
	public AppUser(String username, String password) {
		this.username = username;
		setPassword(password);
	}
	
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
	
	public static AppUser login(String username, String password) {
		// TODO: DO NOT LOGIN WITH GOOGLE OVER HERE! They should be separate functions
		AppUser loggedInUser;
		// Check if the user is a google login
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {		
			loggedInUser = findAppUserByGoogleId(user.getUserId());			
			RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, loggedInUser.getUsername());
		} else {
			// If the user is not a logged in Google user, check if they are a native user
			loggedInUser = findAppUser(username);		
			if(loggedInUser != null) {
				if(!BCrypt.checkpw(password, loggedInUser.getPassword())) return null;
				RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
			}			
		}	
		return loggedInUser;
	}
	
	public static void logout() {
		RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, null);
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
		/*UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {		e
			AppUser appUser = findAppUserByGoogleId(user.getUserId());
			if(appUser != null) {
				return appUser;
			}
		} 
		return null;*/
		String username = (String) RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute(AUTH_USER);
		if(username != null) {
			return ofy().load().type(AppUser.class).id(username).get();
		} else {
			return null;
		}
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
					RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
					return appUser;
				}
			}			
		});
		return newAppUser;		
	}
	
	public static AppUser createAppUser(final String username, final String password) {
		// TODO: Disallow white-space, other special characters?		
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely 
		AppUser newAppUser = ofy().transact(new Work<AppUser>() {
			public AppUser run() {
				if(ofy().load().type(AppUser.class).id(username).get() != null) {
					return null;
				} else {
					// Create the user
					AppUser appUser = new AppUser(username, password);
					//appUser.setEmail(user.getEmail());
					//appUser.setGoogleId(user.getUserId());
					// Create the GoogleID lookup
					//GoogleID googleID = new GoogleID(user.getUserId(), username);			
					//ofy().save().entities(appUser, googleID).now();
					ofy().save().entity(appUser);
					RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
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
		if(currentUser == null) {
			throw new RuntimeException("Please log in.");
			//return false;
		}
		if(currentUser.fave100Songs.size() >= AppUser.MAX_FAVES) throw new RuntimeException("You cannot have more than 100 songs in list.");;		
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
		if(unique == false) throw new RuntimeException("The song is already in your list");;
		// Create the new FaveItem 
		FaveItem newFaveItem = new FaveItem();		
		newFaveItem.setSong(Ref.create(Key.create(Song.class, songID)));
		currentUser.fave100Songs.add(newFaveItem);
		ofy().save().entity(currentUser).now();
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
	
	public static List<FaveItem> getFaveItemsForCurrentUser() {
		AppUser currentUser = getLoggedInAppUser();
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
	
	public static List<Song> getMasterFaveList() {
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
		List<Song> topSongs = ofy().load().type(Song.class).order("-score").limit(100).list();		
		return topSongs;
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

	public List<Song> getFave100Songs() {
		List<Song> songs = new ArrayList<Song>();
		for(FaveItem faveItem : fave100Songs) {
			songs.add(faveItem.getSong().get());
			/*Song song = faveItem.getSong().get();
			faveItem.setTrackName(song.getTrackName());
			faveItem.setArtistName(song.getArtistName());
			faveItem.setTrackViewUrl(song.getTrackViewUrl());
			faveItem.setReleaseYear(song.getReleaseYear());*/
		}
		return songs;
		//return fave100Songs;
	}

	public void setFave100Songs(List<FaveItem> fave100Songs) {
		this.fave100Songs = fave100Songs;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = BCrypt.hashpw(password, BCrypt.gensalt());
	}
}
