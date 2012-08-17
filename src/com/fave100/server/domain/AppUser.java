package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.Activity.Transaction;
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

	@IgnoreSave public static final String CONSUMER_KEY = "GXXfKwE5cXgoXCfghEAg";
	@IgnoreSave public static final String CONSUMER_SECRET = "cec1qLagfRSc0EDOo5r5iR8VUNKfw7DIo6GRuswgs";
	@IgnoreSave public static final int MAX_FAVES = 100;
	@IgnoreSave public static final String AUTH_USER = "loggedIn";
	
	@Id private String username;
	private String password;
	private String email;
	// TODO: Plan ahead for hashtags
	@Embed private List<FaveItem> fave100Songs = new ArrayList<FaveItem>();
	// TODO: user avatar/gravatar
	@IgnoreSave private String avatar;
	// TODO: location = for location based lists
	private Date faveFeedLastChecked;
	
	public AppUser() {}
	
	public AppUser(String username, String password, String email) {
		this.username = username;
		this.email = email;
		this.setFaveFeedLastChecked(new Date());
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
	
	public static AppUser findAppUserByTwitterId(long twitterID) {
		TwitterID tId = ofy().load().type(TwitterID.class).id(twitterID).get();
		if(tId != null) {			
			return ofy().load().type(AppUser.class).id(tId.getUsername()).get();
		} else {
			return null;
		}			
	}
	
	public static AppUser login(String username, String password) {
		AppUser loggedInUser;
		loggedInUser = findAppUser(username);		
		if(loggedInUser != null) {
			if(!BCrypt.checkpw(password, loggedInUser.getPassword())) throw new RuntimeException("Username or password incorrect");;
			RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
		} else {
			throw new RuntimeException("Username or password incorrect");
		}
		return loggedInUser;
	}
	
	public static AppUser loginWithGoogle() {
		AppUser loggedInUser;
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user == null) return null;		
		loggedInUser = findAppUserByGoogleId(user.getUserId());			
		if(loggedInUser != null) RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, loggedInUser.getUsername());		
		return loggedInUser;
	}
	
	public static String getTwitterAuthUrl() {
		Twitter twitter = new TwitterFactory().getInstance();
		//twitter.setOAuthConsumer(AppUser.CONSUMER_KEY, AppUser.CONSUMER_SECRET);
		twitter.setOAuthConsumer("GXXfKwE5cXgoXCfghEAg", "cec1qLagfRSc0EDOo5r5iR8VUNKfw7DIo6GRuswgs");
		
		try {
			RequestToken requestToken = twitter.getOAuthRequestToken();
			//String token = requestToken.getToken();
			//String tokenSecret = requestToken.getTokenSecret();
			String token = "762086864-iRzF4wN7giaYIjUL59kvPsX6PQNwwCyobPLaqLjL";
			String tokenSecret = "XcZ8UdUdNh5bBba1kuIniiqaGqal6cdDfAbVtQLLGE";
			
			RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("token", token);
			RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("tokenSecret", tokenSecret);
			return(requestToken.getAuthorizationURL());
		} catch (TwitterException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isTwitterUserLoggedIn() {
		if(getTwitterUser() != null) {
			return true;
		} else {
			return false;
		}
	}
	public static twitter4j.User getTwitterUser() {
		Twitter twitter = new TwitterFactory().getInstance();
		//twitter.setOAuthConsumer(AppUser.CONSUMER_KEY, AppUser.CONSUMER_SECRET);
		twitter.setOAuthConsumer("GXXfKwE5cXgoXCfghEAg", "cec1qLagfRSc0EDOo5r5iR8VUNKfw7DIo6GRuswgs");
		//String token = (String) RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute("token");
		//String tokenSecret = (String) RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute("tokenSecret");
		String token = "762086864-iRzF4wN7giaYIjUL59kvPsX6PQNwwCyobPLaqLjL";
		String tokenSecret = "XcZ8UdUdNh5bBba1kuIniiqaGqal6cdDfAbVtQLLGE";
		try {
			RequestToken requestToken = new RequestToken(token, tokenSecret);
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken);
			twitter.setOAuthAccessToken(accessToken);
			twitter4j.User twitterUser = twitter.verifyCredentials();
			return twitterUser;
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static AppUser loginWithTwitter() {
		twitter4j.User twitterUser = getTwitterUser();
		if(twitterUser != null) {
			AppUser loggedInUser = findAppUserByTwitterId(twitterUser.getId());			
			if(loggedInUser != null) RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, loggedInUser.getUsername());		
			return loggedInUser;
		}
		return null;
	}
	
	public static void logout() {
		RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, null);
	}
	
	/*
	 * Checks if the user is logged into Google (though not necessarily logged
	 * into the app)
	 */
	public static boolean isGoogleUserLoggedIn() {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null) {
			return true;
		}
		return false;
	}
	
	public static String getGoogleLoginURL(String destinationURL) {
		return UserServiceFactory.getUserService().createLoginURL(destinationURL);
	}
	
	public static String getGoogleLogoutURL(String destinationURL) {
		return UserServiceFactory.getUserService().createLogoutURL(destinationURL);
	}
	
	/*
	 * For when we want to get the correct URL depending on if the 
	 * user is logged in or not, with a single request
	 */	
	public static String getGoogleLoginLogoutURL(String destinationURL) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user == null) {
			return userService.createLoginURL(destinationURL);
		}
		return userService.createLogoutURL(destinationURL);
	}
	
	public static AppUser getLoggedInAppUser() {
		String username = (String) RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute(AUTH_USER);
		if(username != null) {
			return ofy().load().type(AppUser.class).id(username).get();
		} else {
			return null;
		}
	}
	
	public static AppUser createAppUser(final String username, final String password, final String email) {
		// TODO: Disallow username white-space, other special characters?, validate password not null, username not null		
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely 
		AppUser newAppUser = ofy().transact(new Work<AppUser>() {
			public AppUser run() {
				if(ofy().load().type(AppUser.class).id(username).get() != null) {
					throw new RuntimeException("A user with that name already exists");
				} else {
					// Create the user
					AppUser appUser = new AppUser(username, password, email);
					ofy().save().entity(appUser).now();
					RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
					return appUser;
				}
			}			
		});		
		return newAppUser;
	}
	
	public static AppUser createAppUserFromGoogleAccount(final String username) {
		// TODO: Disallow white-space, other special characters?		
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely 
		AppUser newAppUser = ofy().transact(new Work<AppUser>() {
			public AppUser run() {
				UserService userService = UserServiceFactory.getUserService();
				User user = userService.getCurrentUser();
				if(ofy().load().type(AppUser.class).id(username).get() != null) {
					throw new RuntimeException("A user with that name already exists");
				}
				if(ofy().load().type(GoogleID.class).id(user.getUserId()).get() != null) {
					throw new RuntimeException("There is already a Fave100 account associated with this Google ID");
				} 
				// Create the user
				AppUser appUser = new AppUser();
				appUser.setUsername(username);
				appUser.setEmail(user.getEmail());
				// Create the GoogleID lookup
				GoogleID googleID = new GoogleID(user.getUserId(), username);			
				ofy().save().entities(appUser, googleID).now();					
				RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
				return appUser;
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
			throw new RuntimeException("Please log in to complete this action");
			//return false;
		}
		if(currentUser.fave100Songs.size() >= AppUser.MAX_FAVES) throw new RuntimeException("You cannot have more than 100 songs in list");;		
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
		Ref<Song> songRef = Ref.create(Key.create(Song.class, songID));
		newFaveItem.setSong(songRef);
		currentUser.fave100Songs.add(newFaveItem);
		Activity activity = new Activity(currentUser.username, Transaction.FAVE_ADDED);
		activity.setSong(songRef);
		ofy().save().entities(currentUser, activity).now();
	}
	
	public static void removeFaveItemForCurrentUser(int index) {
		AppUser currentUser = AppUser.getLoggedInAppUser();
		if(currentUser == null) return;		
		Activity activity = new Activity(currentUser.username, Transaction.FAVE_REMOVED);
		activity.setSong(currentUser.fave100Songs.get(index).getSong());
		currentUser.fave100Songs.remove(index);
		ofy().save().entities(currentUser, activity).now();	
	}
	
	public static void rerankFaveItemForCurrentUser(final int currentIndex, final int newIndex) {		
		// TODO: Use a transaction to ensure that the indices are correct
		// For some reason this throws a illegal state exception about deregistering a transaction that is not registered
//		ofy().transact(new VoidWork() {
//			public void vrun() {
				AppUser currentUser = AppUser.getLoggedInAppUser();
				if(currentUser == null) return;	
				Activity activity = new Activity(currentUser.username, Transaction.FAVE_POSITION_CHANGED);
				activity.setSong(currentUser.fave100Songs.get(currentIndex).getSong());
				activity.setPreviousLocation(currentIndex+1);
				activity.setNewLocation(newIndex+1);
				FaveItem faveAtCurrIndex = currentUser.fave100Songs.remove(currentIndex);
				currentUser.fave100Songs.add(newIndex, faveAtCurrIndex);				
				ofy().save().entities(currentUser, activity).now();
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
	
	public static List<String> getFaveFeedForCurrentUser() {
		AppUser user = getLoggedInAppUser();
		if(user == null) throw new RuntimeException("Not logged in");
		// TODO: This is horrible, need to rethink strategy
		// Get all the users that the current user is following
		Ref.create(Key.create(AppUser.class, user.username));
		List<Follower> followingList = ofy().load().type(Follower.class)
				.filter("follower", Ref.create(Key.create(AppUser.class, user.username))).list();
		List<List<Activity>> rawActivityList = new ArrayList<List<Activity>>();
		Date twoDaysAgo = new Date();
		twoDaysAgo.setTime(twoDaysAgo.getTime()-(1000*60*60*24*2));
		// For each user that the current user is following, get their activity for past 2 days
		for(Follower following : followingList) {
			rawActivityList.add(
				ofy().load()
				.type(Activity.class)
				.filter("username", following.getFollowing().get().getUsername())
				.filter("timestamp >", twoDaysAgo)
				.order("-timestamp")
				.list()
			);
		}
		
		ArrayList<String> faveFeed = new ArrayList<String>();
		
		// We will bunch all activities less than a day apart
		int buncherTimeLimit = 1000*60*60*24;
	
		// TODO: This is probably horribly inefficient but is a start
		// TODO: Decide if this how we want to bunch activity or a different way
		for(List<Activity> activityList : rawActivityList) {
			
			List<Transaction> transactions = new ArrayList<Activity.Transaction>();
			transactions.add(Transaction.FAVE_ADDED);
			transactions.add(Transaction.FAVE_REMOVED);
			transactions.add(Transaction.FAVE_POSITION_CHANGED);
						
			// For each transaction type
			for(Transaction transaction : transactions) {
				// Go through all activities of that type and bunch them
				for(int i = 0; i < activityList.size(); i++) {
					int counter = 0;
					Activity activity = activityList.get(i);
					// Begin constructing the message
					String songName = activity.getSong().get().getTrackName();					
					String message = "";
					if(activity.getTransactionType().equals(transaction)) {
						if(transaction.equals(Transaction.FAVE_ADDED)) {
							message += activity.getUsername() + " added " + songName;
						} else if(transaction.equals(Transaction.FAVE_REMOVED)) {
							message += activity.getUsername() + " removed " + songName;
						} else if(transaction.equals(Transaction.FAVE_POSITION_CHANGED)) {
							message += activity.getUsername() + " changed the position of " + songName;
						}
					}					
					// Check for songs of the same activity type and within the time limit, so we can bunch them
					if(activity.getTransactionType().equals(transaction)) {
						boolean checkNextSong = true;
						while(checkNextSong && i+1 < activityList.size()) {
							Activity nextActivity = activityList.get(i+1);
							i++;							 
							if(activity.getTransactionType().equals(transaction) 
								&& nextActivity.getTransactionType().equals(transaction)) {
								// The two activities are of the same transaction type, check the time difference
								checkNextSong = false;
								if(activity.getTimestamp().getTime()-buncherTimeLimit < nextActivity.getTimestamp().getTime()) {
									// The two activities are close enough in time - bunch them
									counter++;
									checkNextSong = true;
								}
							}
						}
					}	
					if(counter == 1) {
						message += " and 1 other song";
					} else if(counter > 1) {					
						message += " and "+counter+" other songs";
					}
					if(counter == 0 && transaction.equals(Transaction.FAVE_POSITION_CHANGED)
						&& message != "") {
						// Edge case: Sing song changed position, not bunched with other songs
						message += " from "+activity.getPreviousLocation();
						message += " to "+activity.getNewLocation();
					}
					if(message != "") {
						faveFeed.add(message);
					}
				}
			}
		}
		// Construct messages based on their activity
		/*ArrayList<String> faveFeed = new ArrayList<String>();		
		for(Activity activity : activityList) {
			String songName = activity.getSong().get().getTrackName();
			String message = activity.getUsername();
			if(activity.getTransactionType().equals(Transaction.FAVE_ADDED)) {
				message += " added "+songName;
			} else if (activity.getTransactionType().equals(Transaction.FAVE_REMOVED)) {
				message += " removed "+songName;
			} else if (activity.getTransactionType().equals(Transaction.FAVE_POSITION_CHANGED)) {
				message += " changed the position of "+songName+" from "+activity.getPreviousLocation();
				message += " to "+activity.getNewLocation();
			}
			faveFeed.add(message);
		}*/
		return faveFeed;
	}
	
	public static List<String> getActivityForUser(String username) {
		// TODO: some limit over here on how many results to return
		List<Activity> activityList = ofy().load().type(Activity.class).filter("username", username).list();
		ArrayList<String> faveFeed = new ArrayList<String>();		
		for(Activity activity : activityList) {
			String songName = activity.getSong().get().getTrackName();
			String message = "";
			if(activity.getTransactionType().equals(Transaction.FAVE_ADDED)) {
				message += " Added "+songName;
			} else if (activity.getTransactionType().equals(Transaction.FAVE_REMOVED)) {
				message += " Removed "+songName;
			} else if (activity.getTransactionType().equals(Transaction.FAVE_POSITION_CHANGED)) {
				message += " Changed the position of "+songName+" from "+activity.getPreviousLocation();
				message += " To "+activity.getNewLocation();
			}
			if(message != "") {
				faveFeed.add(message);
			}			
		}
		return faveFeed;
	}
	
	public static void followUser(String username) {
		// TODO: Check for already following to prevent duplicates
		// TODO: Need a better method of message passing than RuntimeExceptions
		// TODO: Move this into follower class
		AppUser currentUser = getLoggedInAppUser();
		if(currentUser == null) throw new RuntimeException("Please log in");
		if(currentUser.username.equals(username)) throw new RuntimeException("You cannot follow yourself");
		if(ofy().load().type(Follower.class).id(currentUser.username+Follower.ID_SEPARATOR+username).get() != null) {
			throw new RuntimeException("You are already following this user");
		}
		Follower follower = new Follower(currentUser.username, username);
		ofy().save().entity(follower).now();
	}
	
	public static boolean checkFollowing(String username) {
		AppUser currentUser = getLoggedInAppUser();
		if(currentUser == null) return false;
		return ofy().load().type(Follower.class).id(currentUser.username+Follower.ID_SEPARATOR+username).get() != null;
	}
	
	public static boolean checkPassword(String password) {
		if(password.equals("100GreatFaves!")) {
			return true;
		} else {
			return false;
		}
	}
	
    // Getters and setters

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

	public String getAvatar() {
		// TODO: Need local avatar as well (if they don't have gravatar)
		// TODO: Do we even want to show gravatars at all? some privacy issues
		if(getEmail() == null) return "http://www.gravatar.com/avatar/?d=mm";
		try {
			byte[] bytes = getEmail().toLowerCase().getBytes("UTF-8");			
	        BigInteger i = new BigInteger(1, MessageDigest.getInstance("MD5").digest(bytes));
	        String hash = String.format("%1$032x", i);
	       return "http://www.gravatar.com/avatar/"+hash+"?d=mm";
		} catch (Exception e) {
			// TODO: Do we care what happens if an exception is thrown here?
		}	
		return null;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Date getFaveFeedLastChecked() {
		return faveFeedLastChecked;
	}

	public void setFaveFeedLastChecked(Date faveFeedLastChecked) {
		this.faveFeedLastChecked = faveFeedLastChecked;
	}
}
