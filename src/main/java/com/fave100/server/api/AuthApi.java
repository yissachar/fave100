package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.BooleanResult;
import com.fave100.server.domain.FacebookRegistration;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.TwitterRegistration;
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
	@ApiOperation(value = "Register a user", response = AppUser.class)
	@ApiResponses(value = {@ApiResponse(code = 400, message = ApiExceptions.DID_NOT_PASS_VALIDATION), @ApiResponse(code = 403, message = ApiExceptions.USERNAME_ALREADY_EXISTS),
							@ApiResponse(code = 403, message = ApiExceptions.EMAIL_ID_ALREADY_EXISTS)})
	public static AppUser createAppUser(@Context final HttpServletRequest request, UserRegistration userRegistration) {

		final String username = userRegistration.getUsername();
		final String password = userRegistration.getPassword();
		final String email = userRegistration.getEmail();

		final List<String> errors = new ArrayList<>();

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name is already registered";
		final String emailExistsMsg = "A user with that email is already registered";
		AppUser user = null;
		try {
			user = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
					if (AppUserDao.findAppUser(username) != null) {
						// Username already exists
						throw new RuntimeException(userExistsMsg);
					}
					else if (ofy().load().type(EmailID.class).id(email).get() != null) {
						// Email address already exists
						throw new RuntimeException(emailExistsMsg);
					}
					else {
						String usernameErrors = Validator.validateUsername(username);
						String passwordErrors = Validator.validatePassword(password);
						String emailErrors = Validator.validateEmail(email);

						if (usernameErrors != null)
							errors.add(usernameErrors);

						if (passwordErrors != null)
							errors.add(passwordErrors);

						if (emailErrors != null)
							errors.add(emailErrors);

						if (errors.size() == 0) {
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
						else {
							throw new RuntimeException(ApiExceptions.DID_NOT_PASS_VALIDATION);
						}
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
			else if (e.getMessage().equals(ApiExceptions.DID_NOT_PASS_VALIDATION)) {
				StringBuilder sb = new StringBuilder();
				sb.append(ApiExceptions.DID_NOT_PASS_VALIDATION);
				sb.append(":");
				for (String error : errors) {
					sb.append(error);
					sb.append("\n");
				}

				throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(sb.toString()).build());
			}
		}

		return user;
	}

	@POST
	@Path(ApiPaths.CREATE_APPUSER_FROM_GOOGLE_ACCOUNT)
	@ApiOperation(value = "Create an AppUser from Google", response = AppUser.class)
	public static AppUser createAppUserFromGoogleAccount(@Context final HttpServletRequest request, final String username) {

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name already exists";
		final String googleIDMsg = "There is already a Fave100 account associated with this Google ID";
		AppUser user = null;
		try {
			user = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
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

		return user;
	}

	@POST
	@Path(ApiPaths.CREATE_APPUSER_FROM_TWITTER_ACCOUNT)
	@ApiOperation(value = "Create an AppUser from Twitter", response = AppUser.class)
	public static AppUser createAppUserFromTwitterAccount(@Context final HttpServletRequest request, TwitterRegistration registration) {

		final String username = registration.getUsername();
		final String oauth_verifier = registration.getOauthVerifier();

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
						request.getSession().setAttribute(AppUserDao.AUTH_USER, username);
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

		return newAppUser;
	}

	@POST
	@Path(ApiPaths.CREATE_APPUSER_FROM_FACEBOOK_ACCOUNT)
	@ApiOperation(value = "Create an AppUser from Facebook", response = AppUser.class)
	public static AppUser createAppUserFromFacebookAccount(
			@Context final HttpServletRequest request, FacebookRegistration registration) {

		final String username = registration.getUsername();
		final String code = registration.getCode();

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely

		final String userExistsMsg = "A user with that name already exists";
		final String facebookIDMsg = "There is already a Fave100 account associated with this Facebook ID";

		AppUser user = null;
		try {
			user = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
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

		return user;
	}

	@POST
	@Path(ApiPaths.LOGIN)
	@ApiOperation(value = "Login", response = AppUser.class)
	public static AppUser login(@Context HttpServletRequest request, LoginCredentials loginCredentials) {

		String username = loginCredentials.getUsername();
		String password = loginCredentials.getPassword();

		AppUser loggingInUser = null;
		String errorMessage = "Invalid credentials";

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
			request.getSession().setAttribute(AppUserDao.AUTH_USER, username);
		}
		else {
			// Bad username
			throw new InvalidLoginException();
		}
		return loggingInUser;
	}

	@POST
	@Path(ApiPaths.LOGIN_WITH_GOOGLE)
	@ApiOperation(value = "Login with Google", response = AppUser.class)
	public static AppUser loginWithGoogle(@Context HttpServletRequest request) {

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
			request.getSession().setAttribute(AppUserDao.AUTH_USER, loggedInUser.getUsername());
		}
		return loggedInUser;
	}

	@POST
	@Path(ApiPaths.LOGIN_WITH_TWITTER)
	@ApiOperation(value = "Login with Twitter", response = AppUser.class)
	public static AppUser loginWithTwitter(@Context HttpServletRequest request, final String oauth_verifier) {

		// Get the Twitter user
		final twitter4j.User twitterUser = AppUserDao.getTwitterUser(request, oauth_verifier);
		if (twitterUser != null) {
			// Find the corresponding Fave100 user
			final AppUser loggedInUser = AppUserDao.findAppUserByTwitterId(twitterUser.getId());
			if (loggedInUser != null) {
				// Successful login - store session
				request.getSession().setAttribute(AppUserDao.AUTH_USER, loggedInUser.getUsername());
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

	@POST
	@Path(ApiPaths.LOGIN_WITH_FACEBOOK)
	@ApiOperation(value = "Login with Facebook", response = AppUser.class)
	public static AppUser loginWithFacebook(@Context HttpServletRequest request, final String code) {
		// Get the Facebook user
		final Long facebookUserId = AppUserDao.getCurrentFacebookUserId(request, code);
		if (facebookUserId != null) {
			// Find the corresponding Fave100 user
			final AppUser loggedInUser = AppUserDao.findAppUserByFacebookId(facebookUserId);
			if (loggedInUser != null) {
				// Successful login - store session				
				request.getSession().setAttribute(AppUserDao.AUTH_USER, loggedInUser.getUsername());
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

	@POST
	@Path(ApiPaths.LOGOUT)
	@ApiOperation(value = "Logout")
	public static void logout(@Context HttpServletRequest request) {
		request.getSession().setAttribute(AppUserDao.AUTH_USER, null);
		request.getSession().setAttribute("requestToken", null);
		request.getSession().setAttribute("twitterUser", null);
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

		try {
			final RequestToken requestToken = twitter.getOAuthRequestToken(redirectUrl);
			request.getSession().setAttribute("requestToken", requestToken);
			return new StringResult(requestToken.getAuthenticationURL());
		}
		catch (final TwitterException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Checks if the user is logged into Google (though not necessarily logged into Fave100)
	 */
	@GET
	@Path(ApiPaths.IS_GOOGLE_LOGGED_IN)
	@ApiOperation(value = "Is google user logged in", response = BooleanResult.class)
	public static BooleanResult isGoogleUserLoggedIn() {
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		return new BooleanResult(user != null);
	}

}