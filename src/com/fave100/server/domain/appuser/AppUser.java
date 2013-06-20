package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.scribe.oauth.OAuthService;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.DatastoreObject;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.Constants;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.Validator;
import com.fave100.shared.exceptions.following.AlreadyFollowingException;
import com.fave100.shared.exceptions.following.CannotFollowYourselfException;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.fave100.shared.exceptions.user.FacebookIdAlreadyExistsException;
import com.fave100.shared.exceptions.user.GoogleIdAlreadyExistsException;
import com.fave100.shared.exceptions.user.IncorrectLoginException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.exceptions.user.TwitterIdAlreadyExistsException;
import com.fave100.shared.exceptions.user.UserNotFoundException;
import com.fave100.shared.exceptions.user.UsernameAlreadyExistsException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreSave;
import com.googlecode.objectify.annotation.Index;

/**
 * A Fave100 user.
 * 
 * @author yissachar.radcliffe
 * 
 */

@Entity
public class AppUser extends DatastoreObject {

	@IgnoreSave private static String TWITTER_CONSUMER_KEY = "";
	@IgnoreSave private static String TWITTER_CONSUMER_SECRET = "";
	@IgnoreSave private static String FACEBOOK_APP_ID = "";
	@IgnoreSave private static String FACEBOOK_APP_SECRET = "";
	@IgnoreSave public static final String AUTH_USER = "loggedIn";
	@IgnoreSave private static OAuthService facebookOAuthservice;
	@IgnoreSave private static TwitterFactory twitterFactory;

	// Store a case-sensitive username field, as well as lowercase username lookup
	@Id private String usernameID;
	private String username;
	private String password;
	// A case sensitive email address stored directly with user for easy access
	// Must be manually kept in sync with EmailID
	private String email;
	private String avatar;
	private Date joinDate;
	@Index private Set<Ref<AppUser>> following = new HashSet<Ref<AppUser>>();

	@SuppressWarnings("unused")
	private AppUser() {
	}

	public AppUser(final String username) {
		this.username = username;
		this.usernameID = username.toLowerCase();
		this.joinDate = new Date();
	}

	// Finder methods
	public static AppUser findAppUser(final String username) {
		return ofy().load().type(AppUser.class).id(username.toLowerCase()).get();
	}

	public static AppUser findAppUserByGoogleId(final String googleID) {
		final GoogleID gId = ofy().load().type(GoogleID.class).id(googleID).get();
		if (gId != null) {
			return ofy().load().ref(gId.getUser()).get();
		}
		else {
			return null;
		}
	}

	public static AppUser findAppUserByTwitterId(final long twitterID) {
		final TwitterID tId = ofy().load().type(TwitterID.class).id(twitterID).get();
		if (tId != null) {
			return ofy().load().ref(tId.getUser()).get();
		}
		else {
			return null;
		}
	}

	public static AppUser findAppUserByFacebookId(final long facebookID) {
		final FacebookID fId = ofy().load().type(FacebookID.class).id(facebookID).get();
		if (fId != null) {
			return ofy().load().ref(fId.getUser()).get();
		}
		else {
			return null;
		}
	}

	// Login methods
	public static AppUser login(final String username, final String password) throws IncorrectLoginException {
		AppUser loggingInUser = null;
		if (username.contains("@")) {
			// User trying to login with email address
			final EmailID emailID = EmailID.findEmailID(username);
			// No email found
			if (emailID == null)
				throw new IncorrectLoginException();
			// Email found, get corresponding user
			loggingInUser = ofy().load().ref(emailID.getUser()).get();
		}
		else {
			// User trying to login with username
			loggingInUser = findAppUser(username);
		}

		if (loggingInUser != null) {
			if (password == null || password.isEmpty()
					|| !BCrypt.checkpw(password, loggingInUser.getPassword())) {
				// Bad password
				throw new IncorrectLoginException();
			}
			// Successful login - store session
			RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
		}
		else {
			// Bad username
			throw new IncorrectLoginException();
		}
		return loggingInUser;
	}

	public static AppUser loginWithGoogle() {
		AppUser loggedInUser;
		// Get the Google user
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if (user == null)
			return null;
		// Find the corresponding Fave100 user
		loggedInUser = findAppUserByGoogleId(user.getUserId());
		if (loggedInUser != null) {
			// Successful login - store session
			RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, loggedInUser.getUsername());
		}
		return loggedInUser;
	}

	public static AppUser loginWithTwitter(final String oauth_verifier) {
		// Get the Twitter user
		final twitter4j.User twitterUser = getTwitterUser(oauth_verifier);
		if (twitterUser != null) {
			// Find the corresponding Fave100 user
			final AppUser loggedInUser = findAppUserByTwitterId(twitterUser.getId());
			if (loggedInUser != null) {
				// Successful login - store session
				RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, loggedInUser.getUsername());
				final String twitterAvatar = twitterUser.getProfileImageURL();
				if (loggedInUser.getAvatar() == null) {
					// Update the user's avatar from Twitter
					// TODO: Verify that twitter avatars work properly
					loggedInUser.setAvatar(twitterAvatar);
					ofy().save().entity(loggedInUser).now();
				}
			}
			return loggedInUser;
		}
		return null;
	}

	public static AppUser loginWithFacebook(final String code) {
		// Get the Facebook user
		final Long facebookUserId = getCurrentFacebookUserId(code);
		if (facebookUserId != null) {
			// Find the corresponding Fave100 user
			final AppUser loggedInUser = findAppUserByFacebookId(facebookUserId);
			if (loggedInUser != null) {
				// Successful login - store session
				RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, loggedInUser.getUsername());
				// TODO: Handle Facebook avatars
				/*	final URL twitterAvatar =  twitterUser.getProfileImageURL();
					if(loggedInUser.getAvatar() == null || !loggedInUser.getAvatar().equals(twitterAvatar.toString())) {
						// Update the user's avatar from Twitter
						loggedInUser.setAvatar(twitterAvatar.toString());
						ofy().save().entity(loggedInUser).now();
					}*/
			}
			return loggedInUser;
		}
		return null;
	}

	// Logout
	public static void logout() {
		RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, null);
		RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("requestToken", null);
		RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("twitterUser", null);
	}

	public static Twitter getTwitterInstance() {
		if (twitterFactory == null) {
			twitterFactory = new TwitterFactory();
		}
		return twitterFactory.getInstance();
	}

	// Builds a Twitter login URL that the client can use
	public static String getTwitterAuthUrl(final String redirectUrl) {
		final Twitter twitter = getTwitterInstance();
		twitter.setOAuthConsumer(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);

		try {
			final RequestToken requestToken = twitter.getOAuthRequestToken(redirectUrl);
			RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("requestToken", requestToken);
			return requestToken.getAuthenticationURL();
		}
		catch (final TwitterException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Gets a Twitter user - not a Fave100 user
	public static twitter4j.User getTwitterUser(final String oauth_verifier) {
		final twitter4j.User user = (twitter4j.User)RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute("twitterUser");
		if (user == null) {
			final Twitter twitter = getTwitterInstance();
			twitter.setOAuthConsumer(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);

			final RequestToken requestToken = (RequestToken)RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute("requestToken");
			try {
				final AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
				twitter.setOAuthAccessToken(accessToken);
				final twitter4j.User twitterUser = twitter.verifyCredentials();
				RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("twitterUser", twitterUser);
				return twitterUser;
			}
			catch (final TwitterException e1) {
				e1.printStackTrace();
			}
			catch (final Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		return user;

	}

	// Builds a Facebook login URL that the client can use
	public static String getFacebookAuthUrl(final String redirectUrl) throws UnsupportedEncodingException {
		RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("facebookRedirect", redirectUrl);
		return "https://www.facebook.com/dialog/oauth?client_id=" + FACEBOOK_APP_ID + "&display=page&redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8");
	}

	// Gets the ID of a the current Facebook user - not a Fave100 user
	public static Long getCurrentFacebookUserId(final String code) {
		Long userID = (Long)RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute("facebookID");
		if (userID == null) {
			String redirectUrl = (String)RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute("facebookRedirect");
			if (redirectUrl == null) {
				redirectUrl = new UrlBuilder(NameTokens.register).with("register", RegisterPresenter.PROVIDER_FACEBOOK).getUrl().replace("yissachar", "localhost");
			}
			try {
				final String authURL = "https://graph.facebook.com/oauth/access_token?client_id=" + FACEBOOK_APP_ID + "&redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8") + "&client_secret=" + FACEBOOK_APP_SECRET + "&code=" + code;
				final URL url = new URL(authURL);
				final String result = readURL(url);
				String accessToken = null;
				Integer expires = null;
				final String[] pairs = result.split("&");
				for (final String pair : pairs) {
					final String[] kv = pair.split("=");
					if (kv.length != 2) {
						throw new RuntimeException("Unexpected auth response");
					}
					else {
						if (kv[0].equals("access_token")) {
							accessToken = kv[1];
						}
						if (kv[0].equals("expires")) {
							expires = Integer.valueOf(kv[1]);
						}
					}
				}
				if (accessToken != null && expires != null) {
					// Successfully retrieved access token, get user id
					final String response = readURL(new URL("https://graph.facebook.com/me?access_token=" + accessToken));
					final JsonParser parser = new JsonParser();
					final JsonElement graphElement = parser.parse(response);
					final JsonObject graphObject = graphElement.getAsJsonObject();
					userID = graphObject.get("id").getAsLong();
					RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute("facebookID", userID);
				}
				else {
					throw new RuntimeException("Access token and expires not found");
				}
			}
			catch (final IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return userID;
	}

	private static String readURL(final URL url) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final InputStream is = url.openStream();
		int r;
		while ((r = is.read()) != -1) {
			baos.write(r);
		}
		return new String(baos.toByteArray());
	}

	/*
	 * Checks if the user is logged into Google (though not necessarily logged
	 * into Fave100)
	 */
	public static boolean isGoogleUserLoggedIn() {
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if (user != null) {
			return true;
		}
		return false;
	}

	// Builds a URL that client can use to log user in to Google Account
	public static String getGoogleLoginURL(final String destinationURL) {
		return UserServiceFactory.getUserService().createLoginURL(destinationURL);
	}

	// Builds a URL that client can use to log user out of Google Account
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
		if (user == null) {
			return userService.createLoginURL(destinationURL);
		}
		return userService.createLogoutURL(destinationURL);
	}

	// Get the logged in Fave100 user
	public static AppUser getLoggedInAppUser() {
		final String username = (String)RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute(AUTH_USER);
		if (username != null) {
			return findAppUser(username);
		}
		else {
			return null;
		}
	}

	public static AppUser createAppUser(final String username, final String password, final String email)
			throws UsernameAlreadyExistsException, EmailIDAlreadyExistsException {
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name is already registered";
		final String emailExistsMsg = "A user with that email is already registered";
		AppUser newAppUser = null;

		try {
			newAppUser = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
					if (findAppUser(username) != null) {
						// Username already exists
						throw new RuntimeException(userExistsMsg);
					}
					else if (ofy().load().type(EmailID.class).id(email).get() != null) {
						// Email address already exists
						throw new RuntimeException(emailExistsMsg);
					}
					else {
						if (Validator.validateUsername(username) == null
								&& Validator.validatePassword(password) == null
								&& Validator.validateEmail(email) == null) {

							// Everything passes validation, create the user
							final AppUser appUser = new AppUser(username);
							appUser.setPassword(password);
							appUser.setEmail(email);
							// Create the user's list
							final FaveList faveList = new FaveList(username, Constants.DEFAULT_HASHTAG);
							// Store email address
							final EmailID emailID = new EmailID(email, appUser);
							ofy().save().entities(appUser, faveList, emailID).now();
							// Automatically log in user
							try {
								return login(username, password);
							}
							catch (final IncorrectLoginException e) {
								return null;
							}
						}
						return null;
					}
				}
			});

		}
		catch (final RuntimeException e) {
			if (e.getMessage().equals(userExistsMsg)) {
				throw new UsernameAlreadyExistsException();
			}
			else if (e.getMessage().equals(emailExistsMsg)) {
				throw new EmailIDAlreadyExistsException();
			}
		}

		return newAppUser;
	}

	public static AppUser createAppUserFromGoogleAccount(final String username)
			throws UsernameAlreadyExistsException, GoogleIdAlreadyExistsException {

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name already exists";
		final String googleIDMsg = "There is already a Fave100 account associated with this Google ID";
		AppUser newAppUser = null;
		try {
			newAppUser = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
					final UserService userService = UserServiceFactory.getUserService();
					final User user = userService.getCurrentUser();
					if (findAppUser(username) != null) {
						throw new RuntimeException(userExistsMsg);
					}
					if (ofy().load().type(GoogleID.class).id(user.getUserId()).get() != null) {
						throw new RuntimeException(googleIDMsg);
					}
					if (Validator.validateUsername(username) == null) {
						// Create the user
						final AppUser appUser = new AppUser(username);
						appUser.setEmail(user.getEmail());
						// Create the user's list
						final FaveList faveList = new FaveList(username, Constants.DEFAULT_HASHTAG);
						// Create the GoogleID lookup
						final GoogleID googleID = new GoogleID(user.getUserId(), appUser);
						ofy().save().entities(appUser, googleID, faveList).now();
						return loginWithGoogle();
					}
					return null;

				}
			});
		}
		catch (final RuntimeException e) {
			if (e.getMessage().equals(userExistsMsg)) {
				throw new UsernameAlreadyExistsException();
			}
			else if (e.getMessage().equals(googleIDMsg)) {
				throw new GoogleIdAlreadyExistsException();
			}
		}

		return newAppUser;
	}

	public static AppUser createAppUserFromTwitterAccount(final String username, final String oauth_verifier)
			throws UsernameAlreadyExistsException, TwitterIdAlreadyExistsException {

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name already exists";
		final String twitterIDMsg = "There is already a Fave100 account associated with this Twitter ID";
		AppUser newAppUser = null;
		try {
			newAppUser = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
					final twitter4j.User user = getTwitterUser(oauth_verifier);
					if (findAppUser(username) != null) {
						throw new RuntimeException(userExistsMsg);
					}
					if (ofy().load().type(TwitterID.class).id(user.getId()).get() != null) {
						throw new RuntimeException(twitterIDMsg);
					}
					if (Validator.validateUsername(username) == null) {
						// Create the user
						final AppUser appUser = new AppUser(username);
						// TODO: Do we need an email for twitter users?
						// Create the user's list
						final FaveList faveList = new FaveList(username, Constants.DEFAULT_HASHTAG);
						// Create the TwitterID lookup
						final TwitterID twitterID = new TwitterID(user.getId(), appUser);
						ofy().save().entities(appUser, twitterID, faveList).now();
						RequestFactoryServlet.getThreadLocalRequest().getSession().setAttribute(AUTH_USER, username);
						return appUser;
					}
					return null;
				}
			});
		}
		catch (final RuntimeException e) {
			if (e.getMessage().equals(userExistsMsg)) {
				throw new UsernameAlreadyExistsException();
			}
			else if (e.getMessage().equals(twitterIDMsg)) {
				throw new TwitterIdAlreadyExistsException();
			}
		}

		return newAppUser;
	}

	public static AppUser createAppUserFromFacebookAccount(final String username, final String state,
			final String code, final String redirectUrl)
			throws UsernameAlreadyExistsException, FacebookIdAlreadyExistsException {

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely

		final String userExistsMsg = "A user with that name already exists";
		final String facebookIDMsg = "There is already a Fave100 account associated with this Facebook ID";
		AppUser newAppUser = null;
		try {
			newAppUser = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
					final Long userFacebookId = getCurrentFacebookUserId(code);
					if (userFacebookId != null) {
						if (findAppUser(username) != null) {
							throw new RuntimeException(userExistsMsg);
						}
						if (ofy().load().type(FacebookID.class).id(userFacebookId).get() != null) {
							throw new RuntimeException(facebookIDMsg);
						}
						if (Validator.validateUsername(username) == null) {
							// Create the user
							final AppUser appUser = new AppUser(username);
							// TODO: Do we need an email for facebook users?
							// Create the user's list
							final FaveList faveList = new FaveList(username, Constants.DEFAULT_HASHTAG);
							// Create the Facebook lookup
							final FacebookID facebookID = new FacebookID(userFacebookId, appUser);
							ofy().save().entities(appUser, facebookID, faveList).now();
							return loginWithFacebook(code);
						}
						return null;
					}
					return null;
				}
			});
		}
		catch (final RuntimeException e) {
			if (e.getMessage().equals(userExistsMsg)) {
				throw new UsernameAlreadyExistsException();
			}
			else if (e.getMessage().equals(facebookIDMsg)) {
				throw new FacebookIdAlreadyExistsException();
			}
		}

		return newAppUser;
	}

	public static String createBlobstoreUrl(final String successPath) {
		final UploadOptions options = UploadOptions.Builder.withMaxUploadSizeBytes(Constants.MAX_AVATAR_SIZE);
		return BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(successPath, options);
	}

	public static String setAvatarForCurrentUser(final String avatar) {
		final AppUser currentUser = getLoggedInAppUser();
		if (currentUser == null)
			return null;
		// TODO: Twitter user can't upload own avatar?
		if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
			BlobstoreServiceFactory.getBlobstoreService().delete(new BlobKey(currentUser.getAvatar()));
		}
		currentUser.setAvatar(avatar);
		ofy().save().entity(currentUser).now();
		return currentUser.getAvatar();
	}

	public String getAvatarImage() {
		if (avatar == null) {
			// If there is no avatar, serve a Gravatar
			if (getEmail() == null)
				return "http://www.gravatar.com/avatar/?d=mm";
			try {
				final byte[] bytes = getEmail().toLowerCase().getBytes("UTF-8");
				final BigInteger i = new BigInteger(1, MessageDigest.getInstance("MD5").digest(bytes));
				final String hash = String.format("%1$032x", i);
				return "http://www.gravatar.com/avatar/" + hash + "?d=mm";
			}
			catch (final Exception e) {
				// Don't care
			}
		}
		// If there is an avatar, try to serve it
		try {
			// Serve the image blob from Google if it exists
			final BlobKey blobKey = new BlobKey(avatar);
			final ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
			final String servingUrl = ImagesServiceFactory.getImagesService().getServingUrl(options);
			// Dev server hack to avoid errors
			if (servingUrl != null) {
				return servingUrl.replace("http://0.0.0.0", "http://127.0.0.1");
			}
		}
		catch (final Exception e) {
			// Blobkey not valid, we'll just serve their avatar
		}
		// Otherwise serve the Twitter, FaceBook, or native avatar
		return avatar;
	}

	public static String getEmailForCurrentUser() throws NotLoggedInException {
		final AppUser currentUser = getLoggedInAppUser();
		if (currentUser == null)
			throw new NotLoggedInException();
		return currentUser.getEmail();
	}

	public static Boolean setProfileData(final String email) throws EmailIDAlreadyExistsException {
		final AppUser currentUser = getLoggedInAppUser();
		if (currentUser == null)
			return false;
		try {
			ofy().transact(new VoidWork() {
				@Override
				public void vrun() {
					EmailID emailID = EmailID.findEmailID(email);
					// Existing email for a different user, throw exception
					if (emailID != null && !emailID.getEmailID().equals(currentUser.getEmail().toLowerCase())) {
						throw new RuntimeException(new EmailIDAlreadyExistsException());
					}
					else if (emailID == null) {
						// No existing email, allow it to be changed

						// First delete the old EmailID
						emailID = EmailID.findEmailID(currentUser.getEmail());
						ofy().delete().entity(emailID).now();
						// Then create a new EmailID with the new email
						emailID = new EmailID(email, currentUser);
						// Manually keep local user email in sync
						currentUser.setEmail(email);
						// Save entities
						ofy().save().entities(emailID, currentUser).now();
					}
				}
			});
		}
		catch (final RuntimeException re) {
			if (re.getCause() instanceof EmailIDAlreadyExistsException) {
				throw (EmailIDAlreadyExistsException)re.getCause();
			}
		}

		return true;
	}

	public static void followUser(final String username) throws NotLoggedInException, CannotFollowYourselfException, AlreadyFollowingException {
		final AppUser currentUser = getLoggedInAppUser();
		if (currentUser == null)
			throw new NotLoggedInException();

		if (currentUser.getUsername().equals(username))
			throw new CannotFollowYourselfException();

		final Ref<AppUser> userRef = Ref.create(Key.create(AppUser.class, username));
		if (currentUser.following.contains(userRef))
			throw new AlreadyFollowingException();

		currentUser.following.add(userRef);
		ofy().save().entity(currentUser).now();
	}

	public static void unfollowUser(final String username) throws NotLoggedInException {
		final AppUser currentUser = getLoggedInAppUser();
		if (currentUser == null)
			throw new NotLoggedInException();

		currentUser.following.remove(Ref.create(Key.create(AppUser.class, username)));
		ofy().save().entity(currentUser).now();
	}

	public static List<AppUser> getFollowing(final String username) throws NotLoggedInException, UserNotFoundException {
		// Only logged in users can see following
		final AppUser currentUser = getLoggedInAppUser();
		if (currentUser == null)
			throw new NotLoggedInException();

		// TODO: Need to restrict how many items are returned. Currently can do a full 5000... but when change need to keep in mind how to detect if following etc.
		// Have to convert to list because GWT is dumb and won't pass Sets properly
		final AppUser user = findAppUser(username);
		if (user == null)
			throw new UserNotFoundException();
		return new ArrayList<AppUser>(ofy().load().refs(user.following).values());
	}

	// Emails user a password reset token if they forget their password
	public static Boolean emailPasswordResetToken(final String username, final String emailAddress) {
		if (!username.isEmpty() && !emailAddress.isEmpty()) {
			final AppUser appUser = findAppUser(username);
			if (appUser != null) {
				// Make sure that Google, Twitter, Facebook users can't "forget password"
				if (appUser.getPassword() == null || appUser.getPassword().isEmpty())
					return false;
				if (appUser.getEmail() == null || appUser.getEmail().isEmpty())
					return false;

				if (appUser.getEmail().equals(emailAddress)) {
					final Properties props = new Properties();
					final Session session = Session.getDefaultInstance(props, null);

					try {
						final PwdResetToken pwdResetToken = new PwdResetToken(appUser.getUsername());
						ofy().save().entity(pwdResetToken).now();
						final Message msg = new MimeMessage(session);
						// TODO: Change email sender to proper one for launch
						msg.setFrom(new InternetAddress("yissachar.radcliffe@caseware.com", "Fave100"));
						msg.addRecipient(Message.RecipientType.TO,
								new InternetAddress(emailAddress, username));
						msg.setSubject("Fave100 Password Change");
						// TODO: wording?
						String msgBody = "To change your Fave100 password, please visit the following URL and change your password within 24 hours.";
						final String pwdResetPlace = new UrlBuilder("passwordreset").with("token", pwdResetToken.getToken()).getUrl();
						msgBody += pwdResetPlace;
						msg.setText(msgBody);

						Transport.send(msg);
						return true;
					}
					catch (final AddressException e) {
						// ...
					}
					catch (final MessagingException e) {
						// ...
					}
					catch (final UnsupportedEncodingException e) {
						// ...
					}
				}
			}
		}
		return false;
	}

	// Allows a user to change their password provided they have a password
	// reset token or the current password
	public static Boolean changePassword(final String newPassword, final String tokenOrPassword) {

		if (Validator.validatePassword(newPassword) != null
				|| tokenOrPassword == null || tokenOrPassword.isEmpty()) {

			return false;
		}

		AppUser appUser = null;
		Boolean changePwd = false;

		// Check if the string is a password reset token
		final PwdResetToken pwdResetToken = PwdResetToken.findPwdResetToken(tokenOrPassword);
		if (pwdResetToken != null) {
			// Check if reset token has expired
			final Date now = new Date();
			if (pwdResetToken.getExpiry().getTime() > now.getTime()) {
				// Token hasn't expired yet, change password
				appUser = pwdResetToken.getAppUser().get();
				if (appUser != null) {
					changePwd = true;
				}
			}
			else {
				// Token expired, delete it
				ofy().delete().entity(pwdResetToken);
			}
		}
		else {
			// No password reset token, check for logged in user
			appUser = getLoggedInAppUser();
			if (appUser != null) {
				// We have a logged in user, check if pwd matches
				if (BCrypt.checkpw(tokenOrPassword, appUser.getPassword()) == true) {
					// Password matches, allow password change
					changePwd = true;
				}
			}
		}

		if (appUser != null && changePwd == true) {
			// Change the password
			appUser.setPassword(newPassword);
			ofy().save().entity(appUser).now();
			if (pwdResetToken != null) {
				// Delete token now that we've used it
				ofy().delete().entity(pwdResetToken);
			}
			return true;
		}

		return false;
	}

	// Getters and setters

	public String getUsernameID() {
		return usernameID;
	}

	public void setUsernameID(final String usernameID) {
		this.usernameID = usernameID;
	}

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

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(final Date joinDate) {
		this.joinDate = joinDate;
	}

	public static void setTwitterConsumerKey(final String key) {
		TWITTER_CONSUMER_KEY = key;
	}

	public static void setTwitterConsumerSecret(final String secret) {
		TWITTER_CONSUMER_SECRET = secret;
	}

	public static void setFacebookApiKey(final String key) {
		FACEBOOK_APP_ID = key;
	}

	public static void setFacebookApiSecret(final String secret) {
		FACEBOOK_APP_SECRET = secret;
	}

}
