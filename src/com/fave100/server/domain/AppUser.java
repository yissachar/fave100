package com.fave100.server.domain;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.fave100.client.pages.users.UsersPresenter;
import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.Activity.Transaction;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.Work;
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

	@IgnoreSave public static final String TWITTER_CONSUMER_KEY = "GXXfKwE5cXgoXCfghEAg";
	@IgnoreSave public static final String TWITTER_CONSUMER_SECRET = "cec1qLagfRSc0EDOo5r5iR8VUNKfw7DIo6GRuswgs";
	@IgnoreSave public static final String FACEBOOK_APP_ID = "312128848885545";
	@IgnoreSave public static final String FACEBOOK_APP_SECRET = "9cd2202cdfc0ee179b465434b1294611";
	@IgnoreSave public static final String AUTH_USER = "loggedIn";
	
	@Id private String username;
	private String password;
	private String email;
	private String avatar;
	// TODO: location  = for location based lists
	
	public AppUser() {}
	
	public AppUser(final String username, final String password, final String email) {
		this.username = username;
		this.email = email;
		setPassword(password);
	}
	
	
	// Finder methods
	public static AppUser findAppUser(final String username) {
		return ofy().load().type(AppUser.class).id(username).get();
	}
	
	public static AppUser findAppUserByGoogleId(final String googleID) {
		final GoogleID gId = ofy().load().type(GoogleID.class).id(googleID).get();
		if(gId != null) {			
			return ofy().load().type(AppUser.class).id(gId.getUsername()).get();
		} else {
			return null;
		}			
	}
	
	public static AppUser findAppUserByTwitterId(final long twitterID) {
		final TwitterID tId = ofy().load().type(TwitterID.class).id(twitterID).get();
		if(tId != null) {			
			return ofy().load().type(AppUser.class).id(tId.getUsername()).get();
		} else {
			return null;
		}			
	}
	
	/*public static AppUser findAppUserByFacebookId(final long twitterID) {
		final TwitterID tId = ofy().load().type(TwitterID.class).id(twitterID).get();
		if(tId != null) {			
			return ofy().load().type(AppUser.class).id(tId.getUsername()).get();
		} else {
			return null;
		}			
	}*/
	
	// Login methods
	public static AppUser login(final String username, final String password) {
		final AppUser loggingInUser = findAppUser(username);		
		if(loggingInUser != null) {
			if(!BCrypt.checkpw(password, loggingInUser.getPassword()) 
				|| password == null || password.isEmpty()) {
					throw new RuntimeException("Username or password incorrect");
			}
			RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
		} else {
			throw new RuntimeException("Username or password incorrect");
		}
		return loggingInUser;
	}
	
	public static AppUser loginWithGoogle() {
		AppUser loggedInUser;
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if(user == null) return null;		
		loggedInUser = findAppUserByGoogleId(user.getUserId());			
		if(loggedInUser != null) RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, loggedInUser.getUsername());		
		return loggedInUser;
	}
	
	public static AppUser loginWithTwitter(final String oauth_verifier) {
		final twitter4j.User twitterUser = getTwitterUser(oauth_verifier);
		if(twitterUser != null) {
			final AppUser loggedInUser = findAppUserByTwitterId(twitterUser.getId());			
			if(loggedInUser != null) {
				RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, loggedInUser.getUsername());
				final String twitterAvatar = twitterUser.getProfileImageURL().toString();
				if(!loggedInUser.getAvatar().equals(twitterAvatar)) {
					// Update the user's avatar from Twitter
					loggedInUser.setAvatar(twitterAvatar);
					ofy().save().entity(loggedInUser).now();
				}
			}			
			return loggedInUser;
		}
		return null;
	}
	
	/*public static AppUser loginWithFacebook(final String state) {
		AppUser loggedInUser;
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if(user == null) return null;		
		loggedInUser = findAppUserByFacebookId(user.getUserId());			
		if(loggedInUser != null) RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, loggedInUser.getUsername());		
		return loggedInUser;
	}*/
	
	// Logout
	public static void logout() {
		RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, null);
		RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("requestToken", null);
		RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("twitterUser", null);
	}
	
	public static String getTwitterAuthUrl() {
		final Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(AppUser.TWITTER_CONSUMER_KEY, AppUser.TWITTER_CONSUMER_SECRET);
		
		try {
			final RequestToken requestToken = twitter.getOAuthRequestToken();			
			RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("requestToken", requestToken);
			return requestToken.getAuthenticationURL();
		} catch (final TwitterException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isTwitterUserLoggedIn(final String oauth_verifier) {
		if(getTwitterUser(oauth_verifier) != null) {
			return true;
		} else {
			return false;
		}		
	}
	
	public static twitter4j.User getTwitterUser(final String oauth_verifier) {
		final twitter4j.User user = (twitter4j.User) RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute("twitterUser");
		if(user == null) {
			final Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(AppUser.TWITTER_CONSUMER_KEY, AppUser.TWITTER_CONSUMER_SECRET);
			
			final RequestToken requestToken = (RequestToken) RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute("requestToken");
			//Logger.getAnonymousLogger().log(Level.SEVERE, oauth_verifier+"hi "+(requestToken.getToken()));
			try {
				final AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
				twitter.setOAuthAccessToken(accessToken);
				final twitter4j.User twitterUser = twitter.verifyCredentials();
				RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("twitterUser", twitterUser);
				return twitterUser;
			} catch (final TwitterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}
		return user;
		
	}	
	
	// Facebook login methods
	public static String getFacebookAuthUrl(final String redirectUrl) {
		final String fbState = "foo";
		// TODO: Use MD5 generated string
		//final byte[] bytes ="asdf".getBytes("UTF-8");			
        //final BigInteger i = new BigInteger(1, MessageDigest.getInstance("MD5").digest(bytes));
        //final String hash = String.format("%1$032x", i);
		RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("fbState", fbState);
		String url = "http://facebook.com/dialog/oauth/?client_id="+ AppUser.FACEBOOK_APP_ID;
		try {
			url += "&state="+fbState+"&redirect_uri="+URLEncoder.encode(redirectUrl, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.getAnonymousLogger().log(Level.SEVERE, "First redirect url:"+redirectUrl);
		return url;
	}	
	
	
	/*
	 * Checks if the user is logged into Google (though not necessarily logged
	 * into the app)
	 */
	public static boolean isGoogleUserLoggedIn() {
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if(user != null) {
			return true;
		}
		return false;
	}
	
	public static String getGoogleLoginURL(final String destinationURL) {
		return UserServiceFactory.getUserService().createLoginURL(destinationURL);
	}
	
	public static String getGoogleLogoutURL(final String destinationURL) {
		return UserServiceFactory.getUserService().createLogoutURL(destinationURL);
	}
	
	/*
	 * For when we want to get the correct URL depending on if the 
	 * user is logged in or not, with a single request
	 */	
	public static String getGoogleLoginLogoutURL(final String destinationURL) {
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if(user == null) {
			return userService.createLoginURL(destinationURL);
		}
		return userService.createLogoutURL(destinationURL);
	}
	
	public static AppUser getLoggedInAppUser() {
		final String username = (String) RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute(AUTH_USER);
		if(username != null) {
			return ofy().load().type(AppUser.class).id(username).get();
		} else {
			return null;
		}
	}
	
	// TODO: Merge account creations to avoid duplication
	public static AppUser createAppUser(final String username, final String password, final String email) {
		// TODO: Disallow username white-space, other special characters?, validate password not null, username not null		
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely 
		final AppUser newAppUser = ofy().transact(new Work<AppUser>() {
			@Override
			public AppUser run() {
				if(ofy().load().type(AppUser.class).id(username).get() != null) {
					throw new RuntimeException("A user with that name already exists");
				} else {
					// Create the user
					final AppUser appUser = new AppUser(username, password, email);
					// Create the user's list
					final FaveList faveList = new FaveList(username, FaveList.DEFAULT_HASHTAG);
					ofy().save().entities(appUser, faveList).now();
					//RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
					//return appUser;
					return login(username, password);
				}
			}			
		});		
		return newAppUser;
	}
	
	public static AppUser createAppUserFromGoogleAccount(final String username) {
		// TODO: Disallow white-space, other special characters?		
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely 
		final AppUser newAppUser = ofy().transact(new Work<AppUser>() {
			@Override
			public AppUser run() {
				final UserService userService = UserServiceFactory.getUserService();
				final User user = userService.getCurrentUser();
				if(ofy().load().type(AppUser.class).id(username).get() != null) {
					throw new RuntimeException("A user with that name already exists");
				}
				if(ofy().load().type(GoogleID.class).id(user.getUserId()).get() != null) {
					throw new RuntimeException("There is already a Fave100 account associated with this Google ID");
				} 
				// Create the user
				final AppUser appUser = new AppUser();
				appUser.setUsername(username);
				appUser.setEmail(user.getEmail());
				// Create the user's list
				final FaveList faveList = new FaveList(username, FaveList.DEFAULT_HASHTAG);				
				// Create the GoogleID lookup
				final GoogleID googleID = new GoogleID(user.getUserId(), username);			
				ofy().save().entities(appUser, googleID, faveList).now();					
				//RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
				//return appUser;
				return loginWithGoogle();
			}			
		});
		return newAppUser;
	}
	
	public static AppUser createAppUserFromTwitterAccount(final String username, final String oauth_verifier) {
		// TODO: Disallow white-space, other special characters?		
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely 
		final AppUser newAppUser = ofy().transact(new Work<AppUser>() {
			@Override
			public AppUser run() {
				final twitter4j.User user = getTwitterUser(oauth_verifier);
				if(ofy().load().type(AppUser.class).id(username).get() != null) {
					throw new RuntimeException("A user with that name already exists");
				}
				if(ofy().load().type(TwitterID.class).id(user.getId()).get() != null) {
					throw new RuntimeException("There is already a Fave100 account associated with this Twitter ID");
				} 
				// Create the user
				final AppUser appUser = new AppUser();
				appUser.setUsername(username);
				// TODO: Do we need an email for twitter users?
				// Create the user's list
				final FaveList faveList = new FaveList(username, FaveList.DEFAULT_HASHTAG);				
				// Create the TwitterID lookup
				final TwitterID twitterID = new TwitterID(user.getId(), username);
				// TODO: Store tokens in database?
				ofy().save().entities(appUser, twitterID, faveList).now();					
				RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
				return appUser;
				//return loginWithTwitter(oauth_verifier);
			}			
		});
		return newAppUser;
	}
	
	// TODO: Broken: Error validating verification code
	public static AppUser createAppUserFromFacebookAccount(final String username, final String state,
			final String code, final String redirectUrl) {
		// TODO: Disallow white-space, other special characters?		
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		Logger.getAnonymousLogger().log(Level.SEVERE, "URL:"+redirectUrl);
		
		// Stop CSRF attempts
		if(!state.equals(RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute("fbState"))) {			
			return null;
		}		
		
		final AppUser newAppUser = ofy().transact(new Work<AppUser>() {
			@Override
			public AppUser run() {
				try {
					// TODO: Redirect URI for production?
					String urlString = "https://graph.facebook.com/oauth/access_token?client_id=";
					urlString += AppUser.FACEBOOK_APP_ID+"&redirect_uri="+URLEncoder.encode(redirectUrl, "UTF-8");
					urlString += "&client_secret="+AppUser.FACEBOOK_APP_SECRET+"&code="+code;
					final URL url = new URL(urlString);
					final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		            String line;
		            while ((line = reader.readLine()) != null) {
		            	 Logger.getAnonymousLogger().log(Level.SEVERE, "Line:"+ line);
		            }
		            reader.close();
		           
		            //final String graphUrl = "https://facebook.com/me?access_token="+getUrlParameters(line).get("access_token");
		            //Logger.getAnonymousLogger().log(Level.SEVERE, "Graph URL:" + graphUrl);
		            
				} catch (final MalformedURLException  e) {
					// TODO: handle exception
					e.printStackTrace();
				} catch (final IOException e) {
					// TODO: handle exception
					e.printStackTrace();
		        }

				// TODO: return actual user
				return null;
				/*final twitter4j.User user = getTwitterUser(oauth_verifier);
				if(ofy().load().type(AppUser.class).id(username).get() != null) {
					throw new RuntimeException("A user with that name already exists");
				}
				if(ofy().load().type(TwitterID.class).id(user.getId()).get() != null) {
					throw new RuntimeException("There is already a Fave100 account associated with this Facebook ID");
				} 
				// Create the user
				final AppUser appUser = new AppUser();
				appUser.setUsername(username);
				// TODO: Do we need an email for twitter users?
				// Create the user's list
				final FaveList faveList = new FaveList(username, FaveList.DEFAULT_HASHTAG);				
				// Create the TwitterID lookup
				final TwitterID twitterID = new TwitterID(user.getId(), username);
				// TODO: Store tokens in database?
				ofy().save().entities(appUser, twitterID, faveList).now();					
				RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
				return appUser;
				//return loginWithTwitter(oauth_verifier);*/
			}			
		});
		return newAppUser;
	}
	
	private static Map<String, List<String>> getUrlParameters(final String url)
	        throws UnsupportedEncodingException {
	    final Map<String, List<String>> params = new HashMap<String, List<String>>();
	    final String[] urlParts = url.split("\\?");
	    if (urlParts.length > 1) {
	        final String query = urlParts[1];
	        for (final String param : query.split("&")) {
	            final String pair[] = param.split("=");
	            final String key = URLDecoder.decode(pair[0], "UTF-8");
	            String value = "";
	            if (pair.length > 1) {
	                value = URLDecoder.decode(pair[1], "UTF-8");
	            }
	            List<String> values = params.get(key);
	            if (values == null) {
	                values = new ArrayList<String>();
	                params.put(key, values);
	            }
	            values.add(value);
	        }
	    }
	    return params;
	}

	
	public static List<AppUser> getAppUsers() {
		// TODO: Add parameters to restrict amount of users returned
		// or otherwise decide how best to show list of users in UI
		return ofy().load().type(AppUser.class).list();
	}
	
	public static List<String> getFaveFeedForCurrentUser() {
		final AppUser user = AppUser.getLoggedInAppUser();
		if(user == null) throw new RuntimeException("Not logged in");
		
		// Get all the users that the current user is following
		Ref.create(Key.create(AppUser.class, user.getUsername()));
		final List<Follower> followingList = ofy().load().type(Follower.class)
				.filter("follower", Ref.create(Key.create(AppUser.class, user.getUsername()))).list();
		final List<List<Activity>> rawActivityList = new ArrayList<List<Activity>>();
		final Date twoDaysAgo = new Date();
		twoDaysAgo.setTime(twoDaysAgo.getTime()-(1000*60*60*24*2));
		// For each user that the current user is following, get their activity for past 2 days
		for(final Follower following : followingList) {
			rawActivityList.add(
				ofy().load()
				.type(Activity.class)
				.filter("username", following.getFollowing().get().getUsername())
				.filter("timestamp >", twoDaysAgo)
				.order("-timestamp")
				.list()
			);
		}
		
		final ArrayList<String> faveFeed = new ArrayList<String>();
		
		// We will bunch all activities less than a day apart
		final int buncherTimeLimit = 1000*60*60*24;
	
		// TODO: This is probably horribly inefficient but is a start
		// TODO: Decide if this how we want to bunch activity or a different way
		for(final List<Activity> activityList : rawActivityList) {
			
			final List<Transaction> transactions = new ArrayList<Activity.Transaction>();
			transactions.add(Transaction.FAVE_ADDED);
			transactions.add(Transaction.FAVE_REMOVED);
			transactions.add(Transaction.FAVE_POSITION_CHANGED);
						
			// For each transaction type
			for(final Transaction transaction : transactions) {
				// Go through all activities of that type and bunch them
				for(int i = 0; i < activityList.size(); i++) {
					int counter = 0;
					final Activity activity = activityList.get(i);
					// Begin constructing the message
					final String songName = activity.getSong().get().getTrackName();					
					String message = "";
					if(activity.getTransactionType().equals(transaction)) {
						message += "<a href='#users;u="+activity.getUsername()+";tab="+UsersPresenter.ACTIVITY_TAB+"'>";
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
							final Activity nextActivity = activityList.get(i+1);
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
						message += "</a>";
						faveFeed.add(message);
					}
				}
			}
		}
		return faveFeed;
	}
	
	public static List<String> getActivityForUser(final String username) {
		final List<Activity> activityList = ofy()
											.load()
											.type(Activity.class)
											.filter("username", username)
											.order("-timestamp")
											.limit(50)
											.list();
		final ArrayList<String> faveFeed = new ArrayList<String>();		
		for(final Activity activity : activityList) {
			final String songName = activity.getSong().get().getTrackName();
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
	
	public static void followUser(final String username) {
		// TODO: Need a better method of message passing than RuntimeExceptions
		// TODO: Move this into follower class
		final AppUser currentUser = getLoggedInAppUser();
		if(currentUser == null) throw new RuntimeException("Please log in");
		if(currentUser.username.equals(username)) throw new RuntimeException("You cannot follow yourself");
		if(ofy().load().type(Follower.class).id(currentUser.username+Follower.ID_SEPARATOR+username).get() != null) {
			throw new RuntimeException("You are already following this user");
		}
		final Follower follower = new Follower(currentUser.username, username);
		ofy().save().entity(follower).now();
	}
	
	public static boolean checkFollowing(final String username) {
		final AppUser currentUser = getLoggedInAppUser();
		if(currentUser == null) return false;
		return ofy().load().type(Follower.class).id(currentUser.username+Follower.ID_SEPARATOR+username).get() != null;
	}
	
	public static boolean checkPassword(final String password) {
		if(password.equals("100GreatFaves!")) {			
			return true;
		} else {
			return false;
		}
	}
	
	public static String createBlobstoreUrl(final String successPath) {
		return BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(successPath);
	}
	
	public static void setAvatarForCurrentUser(final String avatar) {
		final AppUser currentUser = getLoggedInAppUser();
		if(currentUser == null) return;
		// TODO: Max upload size?
		if(currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
			BlobstoreServiceFactory.getBlobstoreService().delete(new BlobKey(currentUser.getAvatar()));
		}		
		currentUser.setAvatar(avatar);		
		ofy().save().entity(currentUser).now();
	}
		
	public String getAvatarImage() {
		// TODO: Need local avatar as well (if they don't have gravatar or twitter)		
		if(avatar == null) {			
			// If there is no avatar, serve a Gravatar
			// TODO: Do we even want to show gravatars at all? some privacy issues
			if(getEmail() == null) return "http://www.gravatar.com/avatar/?d=mm";
			try {
				final byte[] bytes = getEmail().toLowerCase().getBytes("UTF-8");			
		        final BigInteger i = new BigInteger(1, MessageDigest.getInstance("MD5").digest(bytes));
		        final String hash = String.format("%1$032x", i);
		        return "http://www.gravatar.com/avatar/"+hash+"?d=mm";
			} catch (final Exception e) {
				// TODO: Do we care what happens if an exception is thrown here?
			}	
		}	
		// Serve the image blob from Google if it exists
		final ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(new BlobKey(avatar));
		final String servingUrl = ImagesServiceFactory.getImagesService().getServingUrl(options);
		if(servingUrl != null) {
			//TODO: This is bad hack, unfortunately needed for dev mode
			return servingUrl.replace("http://0.0.0.0", "http://127.0.0.1");
		}
		// Otherwise serve the Twitter or FaceBook avatar
		return avatar;
	} 
	
    // Getters and setters	

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}
	
	public String getId() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = BCrypt.hashpw(password, BCrypt.gensalt());
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(final String avatar) {
		this.avatar = avatar;
	}
}
