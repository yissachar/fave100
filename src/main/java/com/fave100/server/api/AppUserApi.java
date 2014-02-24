package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

import com.fave100.server.SessionHelper;
import com.fave100.server.UrlBuilder;
import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.BooleanResult;
import com.fave100.server.domain.LoginResult;
import com.fave100.server.domain.Session;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.appuser.FacebookID;
import com.fave100.server.domain.appuser.Following;
import com.fave100.server.domain.appuser.FollowingResult;
import com.fave100.server.domain.appuser.GoogleID;
import com.fave100.server.domain.appuser.LoginCredentials;
import com.fave100.server.domain.appuser.PwdResetToken;
import com.fave100.server.domain.appuser.TwitterID;
import com.fave100.server.domain.appuser.UserInfo;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.exceptions.AlreadyFollowingException;
import com.fave100.server.exceptions.CannotFollowYourselfException;
import com.fave100.server.exceptions.EmailIdAlreadyExistsException;
import com.fave100.server.exceptions.FacebookIdAlreadyExistsException;
import com.fave100.server.exceptions.GoogleIdAlreadyExistsException;
import com.fave100.server.exceptions.InvalidLoginException;
import com.fave100.server.exceptions.NotLoggedInException;
import com.fave100.server.exceptions.TwitterIdAlreadyExistsException;
import com.fave100.server.exceptions.UsernameAlreadyExistsException;
import com.fave100.server.servlets.AvatarUploadServlet;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/" + ApiPaths.APPUSER_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.APPUSER_ROOT, description = "Operations on Users")
public class AppUserApi {

	private AppUserDao appUserDao;

	@Inject
	public AppUserApi(AppUserDao appUserDao) {
		this.appUserDao = appUserDao;
	}

	@GET
	@Path(ApiPaths.GET_APPUSER)
	@ApiOperation(value = "Find a user by their username", response = AppUser.class)
	@ApiResponses(value = {@ApiResponse(code = 404, message = ApiExceptions.USER_NOT_FOUND)})
	public AppUser getAppUser(@ApiParam(value = "The username", required = true) @QueryParam("username") final String username) {
		AppUser appUser = ofy().load().type(AppUser.class).id(username.toLowerCase()).get();
		if (appUser == null)
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(ApiExceptions.USER_NOT_FOUND).build());

		return appUser;
	}

	@POST
	@Path(ApiPaths.CREATE_APPUSER)
	@ApiOperation(value = "Create an AppUser", response = LoginResult.class)
	@ApiResponses(value = {@ApiResponse(code = 403, message = ApiExceptions.USERNAME_ALREADY_EXISTS), @ApiResponse(code = 403, message = ApiExceptions.EMAIL_ID_ALREADY_EXISTS)})
	public LoginResult createAppUser(@Context final HttpServletRequest request,
			@ApiParam(value = "The username", required = true) @QueryParam("username") final String username,
			@ApiParam(value = "The password", required = true) @QueryParam("password") final String password,
			@ApiParam(value = "The email", required = true) @QueryParam("email") final String email) {

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name is already registered";
		final String emailExistsMsg = "A user with that email is already registered";
		LoginResult loginResult = null;
		try {
			loginResult = ofy().transact(new Work<LoginResult>() {
				@Override
				public LoginResult run() {
					if (appUserDao.findAppUser(username) != null) {
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
							return login(request, new LoginCredentials(username, password));
						}
						return null;
					}
				}
			});

		}
		// Username or email already exists
		catch (final RuntimeException e) {
			if (e.getMessage().equals(userExistsMsg)) {
				throw new UsernameAlreadyExistsException();
			}
			else if (e.getMessage().equals(emailExistsMsg)) {
				throw new EmailIdAlreadyExistsException();
			}
		}

		return loginResult;
	}

	@POST
	@Path(ApiPaths.CREATE_APPUSER_FROM_GOOGLE_ACCOUNT)
	@ApiOperation(value = "Create an AppUser from Google", response = LoginResult.class)
	public LoginResult createAppUserFromGoogleAccount(@Context final HttpServletRequest request, @QueryParam("username") final String username) {

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name already exists";
		final String googleIDMsg = "There is already a Fave100 account associated with this Google ID";
		LoginResult loginResult = null;
		try {
			loginResult = ofy().transact(new Work<LoginResult>() {
				@Override
				public LoginResult run() {
					final UserService userService = UserServiceFactory.getUserService();
					final User user = userService.getCurrentUser();
					if (appUserDao.findAppUser(username) != null) {
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
						return loginWithGoogle(request);
					}
					return null;

				}
			});
		}
		// User or Google ID already exists
		catch (final RuntimeException e) {
			if (e.getMessage().equals(userExistsMsg)) {
				throw new UsernameAlreadyExistsException();
			}
			else if (e.getMessage().equals(googleIDMsg)) {
				throw new GoogleIdAlreadyExistsException();
			}
		}

		return loginResult;
	}

	@POST
	@Path(ApiPaths.CREATE_APPUSER_FROM_TWITTER_ACCOUNT)
	@ApiOperation(value = "Create an AppUser from Twitter", response = LoginResult.class)
	public LoginResult createAppUserFromTwitterAccount(
			@Context final HttpServletRequest request,
			@QueryParam("username") final String username,
			@QueryParam("oauthVerifier") final String oauth_verifier) {

		final Session session = SessionHelper.getSession(request);

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name already exists";
		final String twitterIDMsg = "There is already a Fave100 account associated with this Twitter ID";

		AppUser newAppUser = null;
		try {
			newAppUser = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
					final twitter4j.User user = appUserDao.getTwitterUser(request, oauth_verifier);
					if (appUserDao.findAppUser(username) != null) {
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
						session.setAttribute(AppUserDao.AUTH_USER, username);
						ofy().save().entity(session).now();
						return appUser;
					}
					return null;
				}
			});
		}
		// User or Twitter ID already exists
		catch (final RuntimeException e) {
			if (e.getMessage().equals(userExistsMsg)) {
				throw new UsernameAlreadyExistsException();
			}
			else if (e.getMessage().equals(twitterIDMsg)) {
				throw new TwitterIdAlreadyExistsException();
			}
		}

		return new LoginResult(newAppUser, session.getId());
	}

	@POST
	@Path(ApiPaths.CREATE_APPUSER_FROM_FACEBOOK_ACCOUNT)
	@ApiOperation(value = "Create an AppUser from Facebook", response = LoginResult.class)
	public LoginResult createAppUserFromFacebookAccount(
			@Context final HttpServletRequest request,
			@QueryParam("username") final String username,
			@QueryParam("state") final String state,
			@QueryParam("code") final String code,
			@QueryParam("redirectUrl") final String redirectUrl) {

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely

		final String userExistsMsg = "A user with that name already exists";
		final String facebookIDMsg = "There is already a Fave100 account associated with this Facebook ID";

		LoginResult loginResult = null;
		try {
			loginResult = ofy().transact(new Work<LoginResult>() {
				@Override
				public LoginResult run() {
					final Long userFacebookId = appUserDao.getCurrentFacebookUserId(request, code);
					if (userFacebookId != null) {
						if (appUserDao.findAppUser(username) != null) {
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
							return loginWithFacebook(request, code);
						}
						return null;
					}
					return null;
				}
			});
		}
		// User or Facebook ID already exists
		catch (final RuntimeException e) {
			if (e.getMessage().equals(userExistsMsg)) {
				throw new UsernameAlreadyExistsException();
			}
			else if (e.getMessage().equals(facebookIDMsg)) {
				throw new FacebookIdAlreadyExistsException();
			}
		}

		return loginResult;
	}

	@POST
	@Path(ApiPaths.LOGIN)
	@ApiOperation(value = "Login", response = LoginResult.class)
	public LoginResult login(@Context HttpServletRequest request, LoginCredentials loginCredentials) {

		String username = loginCredentials.getUsername();
		String password = loginCredentials.getPassword();

		AppUser loggingInUser = null;
		String errorMessage = "Invalid credentials";
		Session session = SessionHelper.getSession(request);

		if (username.contains("@")) {
			// User trying to login with email address
			final EmailID emailID = EmailID.findEmailID(username);
			// No email found
			if (emailID == null)
				throw new InvalidLoginException();
			// Email found, get corresponding user
			loggingInUser = ofy().load().ref(emailID.getUser()).get();
		}
		else {
			// User trying to login with username
			loggingInUser = appUserDao.findAppUser(username);
		}

		if (loggingInUser != null) {
			if (password == null || password.isEmpty()
					|| !BCrypt.checkpw(password, loggingInUser.getPassword())) {
				// Bad password
				throw new InvalidLoginException();
			}
			// Successful login - store session
			session.setAttribute(AppUserDao.AUTH_USER, username);
			ofy().save().entity(session);
		}
		else {
			// Bad username
			throw new InvalidLoginException();
		}
		return new LoginResult(loggingInUser, session.getId());
	}

	@POST
	@Path(ApiPaths.LOGIN_WITH_GOOGLE)
	@ApiOperation(value = "Login with Google", response = LoginResult.class)
	public LoginResult loginWithGoogle(@Context HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);

		AppUser loggedInUser;
		// Get the Google user
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if (user == null)
			return null;
		// Find the corresponding Fave100 user
		loggedInUser = appUserDao.findAppUserByGoogleId(user.getUserId());
		if (loggedInUser != null) {
			// Successful login - store session
			session.setAttribute(AppUserDao.AUTH_USER, loggedInUser.getUsername());
			ofy().save().entity(session).now();
		}
		return new LoginResult(loggedInUser, session.getId());
	}

	@POST
	@Path(ApiPaths.LOGIN_WITH_TWITTER)
	@ApiOperation(value = "Login with Twitter", response = LoginResult.class)
	public LoginResult loginWithTwitter(@Context HttpServletRequest request, @QueryParam("oauthVerifier") final String oauth_verifier) {
		Session session = SessionHelper.getSession(request);

		// Get the Twitter user
		final twitter4j.User twitterUser = appUserDao.getTwitterUser(request, oauth_verifier);
		if (twitterUser != null) {
			// Find the corresponding Fave100 user
			final AppUser loggedInUser = appUserDao.findAppUserByTwitterId(twitterUser.getId());
			if (loggedInUser != null) {
				// Successful login - store session
				session.setAttribute(AppUserDao.AUTH_USER, loggedInUser.getUsername());
				ofy().save().entity(session).now();
				final String twitterAvatar = twitterUser.getProfileImageURL();
				if (loggedInUser.getAvatar() == null) {
					// Update the user's avatar from Twitter
					// TODO: Verify that twitter avatars work properly
					loggedInUser.setAvatar(twitterAvatar);
					ofy().save().entity(loggedInUser).now();
				}
			}
			return new LoginResult(loggedInUser, session.getId());
		}
		return null;
	}

	@POST
	@Path(ApiPaths.LOGIN_WITH_FACEBOOK)
	@ApiOperation(value = "Login with Facebook", response = LoginResult.class)
	public LoginResult loginWithFacebook(@Context HttpServletRequest request, @QueryParam("code") final String code) {
		// Get the Facebook user
		final Long facebookUserId = appUserDao.getCurrentFacebookUserId(request, code);
		Session session = SessionHelper.getSession(request);
		if (facebookUserId != null) {
			// Find the corresponding Fave100 user
			final AppUser loggedInUser = appUserDao.findAppUserByFacebookId(facebookUserId);
			if (loggedInUser != null) {
				// Successful login - store session				
				session.setAttribute(AppUserDao.AUTH_USER, loggedInUser.getUsername());
				ofy().save().entity(session).now();
				// TODO: Handle Facebook avatars
				/*	final URL twitterAvatar =  twitterUser.getProfileImageURL();
					if(loggedInUser.getAvatar() == null || !loggedInUser.getAvatar().equals(twitterAvatar.toString())) {
						// Update the user's avatar from Twitter
						loggedInUser.setAvatar(twitterAvatar.toString());
						ofy().save().entity(loggedInUser).now();
					}*/
			}
			return new LoginResult(loggedInUser, session.getId());
		}
		return null;
	}

	@POST
	@Path(ApiPaths.LOGOUT)
	@ApiOperation(value = "Logout")
	public void logout(@Context HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);
		session.setAttribute(AppUserDao.AUTH_USER, null);
		session.setAttribute("requestToken", null);
		session.setAttribute("twitterUser", null);
		ofy().delete().entity(session).now();
		return;
	}

	@GET
	@Path(ApiPaths.LOGGED_IN_APPUSER)
	@ApiOperation(value = "Get logged in user", response = AppUser.class)
	public AppUser getLoggedInAppUser(@Context HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);
		final String username = (String)session.getAttribute(AppUserDao.AUTH_USER);
		if (username != null) {
			return appUserDao.findAppUser(username);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns 5 following users from the given index
	 * 
	 * @param username
	 * @param index
	 * @return
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 * @throws ForbiddenException
	 */
	@GET
	@Path(ApiPaths.GET_FOLLOWING)
	@ApiOperation(value = "Get following", response = FollowingResult.class)
	public FollowingResult getFollowing(
			@Context HttpServletRequest request,
			@QueryParam("username") final String username,
			@QueryParam("index") final int index) {

		// Only logged in users can see following		
		final AppUser currentUser = getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final AppUser user = getAppUser(username);
		if (user == null)
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("User does not exist").build());

		if (user.isFollowingPrivate() && !user.getId().equals(currentUser.getId()))
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("List is private").build());

		final Following following = ofy().load().type(Following.class).id(username.toLowerCase()).get();
		if (following != null && following.getFollowing() != null) {
			List<Ref<AppUser>> users = following.getFollowing();
			users = users.subList(index, Math.min(index + Constants.MORE_FOLLOWING_INC, following.getFollowing().size()));
			final boolean moreFollowing = (following.getFollowing().size() - index - users.size()) > 0;
			return new FollowingResult(new ArrayList<AppUser>(ofy().load().refs(users).values()), moreFollowing);
		}

		return new FollowingResult(new ArrayList<AppUser>(), false);
	}

	@GET
	@Path(ApiPaths.IS_FOLLOWING)
	@ApiOperation(value = "Is following", response = BooleanResult.class)
	public BooleanResult isFollowing(@Context HttpServletRequest request, @QueryParam("username") final String username) {
		if (!appUserDao.isAppUserLoggedIn(request))
			throw new NotLoggedInException();

		Session session = SessionHelper.getSession(request);

		final String currentUserUsername = (String)session.getAttribute(AppUserDao.AUTH_USER);
		final Ref<AppUser> userRef = Ref.create(Key.create(AppUser.class, username.toLowerCase()));
		final Following following = ofy().load().type(Following.class).id(currentUserUsername.toLowerCase()).get();

		BooleanResult result = new BooleanResult(following != null && !following.getFollowing().isEmpty() && following.getFollowing().contains(userRef));
		return result;
	}

	/*
	 * Checks if the user is logged into Google (though not necessarily logged into Fave100)
	 */
	@GET
	@Path(ApiPaths.IS_GOOGLE_LOGGED_IN)
	@ApiOperation(value = "Is google user logged in", response = BooleanResult.class)
	public BooleanResult isGoogleUserLoggedIn() {
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		return new BooleanResult(user != null);
	}

	// Builds a URL that client can use to log user in to Google Account
	@GET
	@Path(ApiPaths.GET_GOOGLE_LOGIN_URL)
	@ApiOperation(value = "Get Google login URL", response = StringResult.class)
	public StringResult getGoogleLoginURL(@QueryParam("destinationURL") final String destinationURL) {
		return new StringResult(UserServiceFactory.getUserService().createLoginURL(destinationURL));
	}

	// Builds a Facebook login URL that the client can use
	@GET
	@Path(ApiPaths.GET_FACEBOOK_AUTH_URL)
	@ApiOperation(value = "Get Facebook auth URL", response = StringResult.class)
	public StringResult getFacebookAuthUrl(@Context HttpServletRequest request, @QueryParam("redirectUrl") final String redirectUrl) {
		request.getSession().setAttribute("facebookRedirect", redirectUrl);
		try {
			return new StringResult("https://www.facebook.com/dialog/oauth?client_id=" + AppUserDao.FACEBOOK_APP_ID + "&display=page&redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Unsupported encoding").build());
		}
	}

	// Check if Fave100 user is logged in 
	@GET
	@Path(ApiPaths.IS_APPUSER_LOGGED_IN)
	@ApiOperation(value = "Is app user logged in", response = BooleanResult.class)
	public BooleanResult isAppUserLoggedIn(@Context HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);

		final String username = (String)session.getAttribute(AppUserDao.AUTH_USER);
		return new BooleanResult(username != null);
	}

	// Builds a Twitter login URL that the client can use
	@GET
	@Path(ApiPaths.GET_TWITTER_AUTH_URL)
	@ApiOperation(value = "Get Twitter auth URL", response = StringResult.class)
	public StringResult getTwitterAuthUrl(@Context HttpServletRequest request, final String redirectUrl) {
		final Twitter twitter = appUserDao.getTwitterInstance();
		twitter.setOAuthConsumer(AppUserDao.TWITTER_CONSUMER_KEY, AppUserDao.TWITTER_CONSUMER_SECRET);

		Session session = SessionHelper.getSession(request);

		try {
			final RequestToken requestToken = twitter.getOAuthRequestToken(redirectUrl);
			session.setAttribute("requestToken", requestToken);
			ofy().save().entity(session).now();
			return new StringResult(requestToken.getAuthenticationURL());
		}
		catch (final TwitterException e) {
			e.printStackTrace();
		}
		return null;
	}

	@POST
	@Path(ApiPaths.CREATE_BLOBSTORE_URL)
	@ApiOperation(value = "Create a blobstore upload URL", response = StringResult.class)
	public StringResult createBlobstoreUrl() {
		final UploadOptions options = UploadOptions.Builder.withMaxUploadSizeBytes(Constants.MAX_AVATAR_SIZE);
		return new StringResult(BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(AvatarUploadServlet.PATH, options));
	}

	@GET
	@Path(ApiPaths.USER_SETTINGS)
	@ApiOperation(value = "Get current user settings", response = UserInfo.class)
	public UserInfo getCurrentUserSettings(@Context HttpServletRequest request) {
		final AppUser currentUser = getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		return new UserInfo(currentUser);
	}

	@POST
	@Path(ApiPaths.USER_SETTINGS)
	@ApiOperation(value = "Set user info", response = BooleanResult.class)
	public BooleanResult setUserInfo(@Context HttpServletRequest request, final UserInfo userInfo) {
		final AppUser currentUser = getLoggedInAppUser(request);
		if (currentUser == null)
			return new BooleanResult(false);
		try {
			ofy().transact(new VoidWork() {
				@Override
				public void vrun() {
					final String email = userInfo.getEmail();
					// Change info
					currentUser.setFollowingPrivate(userInfo.isFollowingPrivate());
					// If email wasn't changed, just save the user
					if (email.equals(currentUser.getEmail())) {
						ofy().save().entity(currentUser).now();
					}
					// Otherwise handle email special case
					else {
						EmailID emailID = EmailID.findEmailID(email);
						// Existing email for a different user, throw exception
						if (emailID != null && !emailID.getEmailID().equals(currentUser.getEmail().toLowerCase())) {
							throw new RuntimeException(new EmailIdAlreadyExistsException());
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
				}
			});
		}
		catch (final RuntimeException re) {
			if (re.getCause() instanceof EmailIdAlreadyExistsException) {
				throw new EmailIdAlreadyExistsException();
			}
		}

		return new BooleanResult(true);
	}

	@POST
	@Path(ApiPaths.FOLLOW)
	@ApiOperation(value = "Follow user")
	public void followUser(@Context HttpServletRequest request, @QueryParam("username") final String username) {
		final AppUser currentUser = getLoggedInAppUser(request);

		if (currentUser == null)
			throw new NotLoggedInException();

		// Check if user trying to follow themselves
		if (currentUser.getUsername().equals(username))
			throw new CannotFollowYourselfException();

		final Ref<AppUser> userRef = Ref.create(Key.create(AppUser.class, username.toLowerCase()));
		Following following = ofy().load().type(Following.class).id(currentUser.getId()).get();
		if (following == null) {
			following = new Following(currentUser.getId());
			ofy().save().entity(following).now();
		}

		// Check if already following that user
		if (following.getFollowing().contains(userRef))
			throw new AlreadyFollowingException();

		following.getFollowing().add(userRef);
		ofy().save().entity(following).now();

		return;
	}

	@POST
	@Path(ApiPaths.UNFOLLOW)
	@ApiOperation(value = "Unfollow user")
	public void unfollowUser(@Context HttpServletRequest request, @QueryParam("username") final String username) {

		final AppUser currentUser = getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final Following following = ofy().load().type(Following.class).id(currentUser.getId()).get();
		if (following == null)
			return;

		following.getFollowing().remove(Ref.create(Key.create(AppUser.class, username.toLowerCase())));
		ofy().save().entity(following).now();

		return;
	}

	// Emails user a password reset token if they forget their password
	@GET
	@Path(ApiPaths.EMAIL_PASSWORD_RESET)
	@ApiOperation(value = "Email password reset token", response = BooleanResult.class)
	public BooleanResult emailPasswordResetToken(@QueryParam("username") final String username, @QueryParam("emailAddress") final String emailAddress) {

		if (!username.isEmpty() && !emailAddress.isEmpty()) {
			final AppUser appUser = appUserDao.findAppUser(username);
			if (appUser != null) {
				// Make sure that Google, Twitter, Facebook users can't "forget password"
				if (appUser.getPassword() == null || appUser.getPassword().isEmpty())
					return new BooleanResult(false);
				if (appUser.getEmail() == null || appUser.getEmail().isEmpty())
					return new BooleanResult(false);

				if (appUser.getEmail().equals(emailAddress)) {
					final Properties props = new Properties();
					final javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);

					try {
						final PwdResetToken pwdResetToken = new PwdResetToken(appUser.getUsername());
						ofy().save().entity(pwdResetToken).now();
						final Message msg = new MimeMessage(session);
						msg.setFrom(new InternetAddress("info@fave100.com", "Fave100"));
						msg.addRecipient(Message.RecipientType.TO,
								new InternetAddress(emailAddress, username));
						msg.setSubject("Fave100 Password Change");
						// TODO: wording?
						String msgBody = "To change your Fave100 password, please visit the following URL and change your password within 24 hours.";
						final String pwdResetPlace = new UrlBuilder("passwordreset").with("token", pwdResetToken.getToken()).getUrl();
						msgBody += pwdResetPlace;
						msg.setText(msgBody);

						Transport.send(msg);
						return new BooleanResult(true);
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
		return new BooleanResult(true);
	}

	// Allows a user to change their password provided they have a password reset token or the current password
	@GET
	@Path(ApiPaths.CHANGE_PASSWORD)
	@ApiOperation(value = "Change password", response = BooleanResult.class)
	public BooleanResult changePassword(@Context HttpServletRequest request, @QueryParam("newPassword") final String newPassword,
			@QueryParam("tokenOrPassword") final String tokenOrPassword) {

		if (Validator.validatePassword(newPassword) != null || tokenOrPassword == null || tokenOrPassword.isEmpty()) {
			// TODO: Shouldn't this be an exception instead??
			return new BooleanResult(false);
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
			appUser = getLoggedInAppUser(request);
			if (appUser != null) {
				// We have a logged in user, check if pwd matches
				if (BCrypt.checkpw(tokenOrPassword, appUser.getPassword())) {
					// Password matches, allow password change
					changePwd = true;
				}
			}
			else {
				throw new NotLoggedInException();
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
			return new BooleanResult(true);
		}

		return new BooleanResult(false);
	}
}
