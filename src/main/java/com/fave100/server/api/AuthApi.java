package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.LoginResult;
import com.fave100.server.domain.Session;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.UserRegistration;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.appuser.FacebookID;
import com.fave100.server.domain.appuser.GoogleID;
import com.fave100.server.domain.appuser.LoginCredentials;
import com.fave100.server.domain.appuser.TwitterID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.exceptions.EmailIdAlreadyExistsException;
import com.fave100.server.exceptions.FacebookIdAlreadyExistsException;
import com.fave100.server.exceptions.GoogleIdAlreadyExistsException;
import com.fave100.server.exceptions.InvalidLoginException;
import com.fave100.server.exceptions.TwitterIdAlreadyExistsException;
import com.fave100.server.exceptions.UsernameAlreadyExistsException;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Work;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/" + ApiPaths.AUTH_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.AUTH_ROOT, description = "Authentication operations")
public class AuthApi {

	@POST
	@Path(ApiPaths.REGISTER)
	@ApiOperation(value = "Register a user", response = LoginResult.class)
	@ApiResponses(value = {@ApiResponse(code = 403, message = ApiExceptions.USERNAME_ALREADY_EXISTS), @ApiResponse(code = 403, message = ApiExceptions.EMAIL_ID_ALREADY_EXISTS)})
	public static LoginResult createAppUser(@Context final HttpServletRequest request,
			UserRegistration userRegistration) {

		final String username = userRegistration.getUsername();
		final String password = userRegistration.getPassword();
		final String email = userRegistration.getEmail();

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name is already registered";
		final String emailExistsMsg = "A user with that email is already registered";
		LoginResult loginResult = null;
		try {
			loginResult = ofy().transact(new Work<LoginResult>() {
				@Override
				public LoginResult run() {
					if (AppUserDao.findAppUser(username) != null) {
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
							return AuthApi.login(request, new LoginCredentials(username, password));
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
	public static LoginResult createAppUserFromGoogleAccount(@Context final HttpServletRequest request, @QueryParam("username") final String username) {

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
					if (AppUserDao.findAppUser(username) != null) {
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
						return AuthApi.loginWithGoogle(request);
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
	public static LoginResult createAppUserFromTwitterAccount(
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
					final twitter4j.User user = AppUserDao.getTwitterUser(request, oauth_verifier);
					if (AppUserDao.findAppUser(username) != null) {
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
	public static LoginResult createAppUserFromFacebookAccount(
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
					final Long userFacebookId = AppUserDao.getCurrentFacebookUserId(request, code);
					if (userFacebookId != null) {
						if (AppUserDao.findAppUser(username) != null) {
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
							return AuthApi.loginWithFacebook(request, code);
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
	public static LoginResult login(@Context HttpServletRequest request, LoginCredentials loginCredentials) {

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
			loggingInUser = AppUserDao.findAppUser(username);
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
	public static LoginResult loginWithGoogle(@Context HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);

		AppUser loggedInUser;
		// Get the Google user
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		if (user == null)
			return null;
		// Find the corresponding Fave100 user
		loggedInUser = AppUserDao.findAppUserByGoogleId(user.getUserId());
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
	public static LoginResult loginWithTwitter(@Context HttpServletRequest request, @QueryParam("oauthVerifier") final String oauth_verifier) {
		Session session = SessionHelper.getSession(request);

		// Get the Twitter user
		final twitter4j.User twitterUser = AppUserDao.getTwitterUser(request, oauth_verifier);
		if (twitterUser != null) {
			// Find the corresponding Fave100 user
			final AppUser loggedInUser = AppUserDao.findAppUserByTwitterId(twitterUser.getId());
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
	public static LoginResult loginWithFacebook(@Context HttpServletRequest request, @QueryParam("code") final String code) {
		// Get the Facebook user
		final Long facebookUserId = AppUserDao.getCurrentFacebookUserId(request, code);
		Session session = SessionHelper.getSession(request);
		if (facebookUserId != null) {
			// Find the corresponding Fave100 user
			final AppUser loggedInUser = AppUserDao.findAppUserByFacebookId(facebookUserId);
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
	public static void logout(@Context HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);
		session.setAttribute(AppUserDao.AUTH_USER, null);
		session.setAttribute("requestToken", null);
		session.setAttribute("twitterUser", null);
		ofy().delete().entity(session).now();
		return;
	}

	// Builds a URL that client can use to log user in to Google Account
	@GET
	@Path(ApiPaths.GET_GOOGLE_AUTH_URL)
	@ApiOperation(value = "Get Google login URL", response = StringResult.class)
	public static StringResult getGoogleAuthUrl(@QueryParam("destinationURL") final String destinationURL) {
		return new StringResult(UserServiceFactory.getUserService().createLoginURL(destinationURL));
	}

	// Builds a Facebook login URL that the client can use
	@GET
	@Path(ApiPaths.GET_FACEBOOK_AUTH_URL)
	@ApiOperation(value = "Get Facebook auth URL", response = StringResult.class)
	public static StringResult getFacebookAuthUrl(@Context HttpServletRequest request, @QueryParam("redirectUrl") final String redirectUrl) {
		request.getSession().setAttribute("facebookRedirect", redirectUrl);
		try {
			return new StringResult("https://www.facebook.com/dialog/oauth?client_id=" + AppUserDao.FACEBOOK_APP_ID + "&display=page&redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Unsupported encoding").build());
		}
	}

	// Builds a Twitter login URL that the client can use
	@GET
	@Path(ApiPaths.GET_TWITTER_AUTH_URL)
	@ApiOperation(value = "Get Twitter auth URL", response = StringResult.class)
	public static StringResult getTwitterAuthUrl(@Context HttpServletRequest request, @QueryParam("redirectUrl") final String redirectUrl) {
		final Twitter twitter = AppUserDao.getTwitterInstance();
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

}
