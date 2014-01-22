package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.ApiBase;
import com.fave100.server.domain.BooleanResult;
import com.fave100.server.domain.VoidResult;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.Work;

public class AppUserApi extends ApiBase {

	private AppUserDao appUserDao;

	@Inject
	public AppUserApi(AppUserDao appUserDao) {
		this.appUserDao = appUserDao;
	}

	@ApiMethod(name = "appUser.getAppUser", path = "appUser")
	public AppUser getAppUser(@Named("username") final String username) {
		return ofy().load().type(AppUser.class).id(username.toLowerCase()).get();
	}

	@ApiMethod(name = "appUser.createAppUser", path = "createAppUser")
	public AppUser createAppUser(final HttpServletRequest request, @Named("username") final String username, @Named("password") final String password,
			@Named("email") final String email) throws BadRequestException {
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name is already registered";
		final String emailExistsMsg = "A user with that email is already registered";
		AppUser newAppUser = null;

		try {
			newAppUser = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
					if (getAppUser(username) != null) {
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
								return login(request, username, password);
							}
							catch (UnauthorizedException e) {
								return null;
							}
						}
						return null;
					}
				}
			});

		}
		// Username or email already exists
		catch (final RuntimeException e) {
			throw new BadRequestException(e.getMessage());
		}

		return newAppUser;
	}

	@ApiMethod(name = "appUser.createAppUserFromGoogleAccount", path = "createAppUserFromGoogleAccount")
	public AppUser createAppUserFromGoogleAccount(final HttpServletRequest request, @Named("username") final String username) throws BadRequestException {

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
					if (getAppUser(username) != null) {
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
			throw new BadRequestException(e.getMessage());
		}

		return newAppUser;
	}

	@ApiMethod(name = "appUser.createAppUserFromTwitterAccount", path = "createAppUserFromTwitterAccount")
	public AppUser createAppUserFromTwitterAccount(final HttpServletRequest request, @Named("username") final String username, @Named("oauthVerifier") final String oauth_verifier)
			throws BadRequestException {

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name already exists";
		final String twitterIDMsg = "There is already a Fave100 account associated with this Twitter ID";
		AppUser newAppUser = null;
		try {
			newAppUser = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
					final twitter4j.User user = appUserDao.getTwitterUser(oauth_verifier);
					if (getAppUser(username) != null) {
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
			throw new BadRequestException(e.getMessage());
		}

		return newAppUser;
	}

	@ApiMethod(name = "appUser.createAppUserFromFacebookAccount", path = "createAppUserFromFacebookAccount")
	public AppUser createAppUserFromFacebookAccount(final HttpServletRequest request, @Named("username") final String username, @Named("state") final String state,
			@Named("code") final String code, @Named("redirectUrl") final String redirectUrl) throws BadRequestException {

		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely

		final String userExistsMsg = "A user with that name already exists";
		final String facebookIDMsg = "There is already a Fave100 account associated with this Facebook ID";
		AppUser newAppUser = null;
		try {
			newAppUser = ofy().transact(new Work<AppUser>() {
				@Override
				public AppUser run() {
					final Long userFacebookId = appUserDao.getCurrentFacebookUserId(code);
					if (userFacebookId != null) {
						if (getAppUser(username) != null) {
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
			throw new BadRequestException(e.getMessage());
		}

		return newAppUser;
	}

	@ApiMethod(name = "appUser.login", path = "login")
	public AppUser login(HttpServletRequest request, @Named("username") final String username, @Named("password") final String password) throws UnauthorizedException {
		AppUser loggingInUser = null;
		String errorMessage = "Invalid credentials";

		if (username.contains("@")) {
			// User trying to login with email address
			final EmailID emailID = EmailID.findEmailID(username);
			// No email found
			if (emailID == null)
				throw new UnauthorizedException(errorMessage);
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
				throw new UnauthorizedException(errorMessage);
			}
			// Successful login - store session
			request.getSession().setAttribute(AppUserDao.AUTH_USER, username);
		}
		else {
			// Bad username
			throw new UnauthorizedException(errorMessage);
		}
		return loggingInUser;
	}

	@ApiMethod(name = "appUser.loginWithGoogle", path = "googleLogin")
	public AppUser loginWithGoogle(HttpServletRequest request) {
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
			request.getSession().setAttribute(AppUserDao.AUTH_USER, loggedInUser.getUsername());
		}
		return loggedInUser;
	}

	@ApiMethod(name = "appUser.loginWithTwitter", path = "twitterLogin")
	public AppUser loginWithTwitter(HttpServletRequest request, @Named("oauthVerifier") final String oauth_verifier) {
		// Get the Twitter user
		final twitter4j.User twitterUser = appUserDao.getTwitterUser(oauth_verifier);
		if (twitterUser != null) {
			// Find the corresponding Fave100 user
			final AppUser loggedInUser = appUserDao.findAppUserByTwitterId(twitterUser.getId());
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

	@ApiMethod(name = "appUser.loginWithFacebook", path = "facebookLogin")
	public AppUser loginWithFacebook(HttpServletRequest request, @Named("code") final String code) {
		// Get the Facebook user
		final Long facebookUserId = appUserDao.getCurrentFacebookUserId(code);
		if (facebookUserId != null) {
			// Find the corresponding Fave100 user
			final AppUser loggedInUser = appUserDao.findAppUserByFacebookId(facebookUserId);
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

	@ApiMethod(name = "appUser.logout", path = "logout")
	public VoidResult logout(HttpServletRequest request) {
		request.getSession().setAttribute(AppUserDao.AUTH_USER, null);
		request.getSession().setAttribute("requestToken", null);
		request.getSession().setAttribute("twitterUser", null);
		return new VoidResult();
	}

	@ApiMethod(name = "appUser.getLoggedInAppUser", path = "loggedInAppUser")
	public AppUser getLoggedInAppUser(HttpServletRequest request) {
		final String username = (String)request.getSession().getAttribute(AppUserDao.AUTH_USER);
		if (username != null) {
			return getAppUser(username);
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
	@ApiMethod(name = "appUser.getFollowing", path = "following")
	public FollowingResult getFollowing(HttpServletRequest request, @Named("username") final String username, @Named("index") final int index) throws UnauthorizedException, NotFoundException, ForbiddenException {
		// Only logged in users can see following		
		final AppUser currentUser = getLoggedInAppUser(request);
		if (currentUser == null)
			throw new UnauthorizedException("Not logged in");

		final AppUser user = getAppUser(username);
		if (user == null)
			throw new NotFoundException("User does not exist");

		if (user.isFollowingPrivate() && !user.getId().equals(currentUser.getId()))
			throw new ForbiddenException("List is private");

		final Following following = ofy().load().type(Following.class).id(username.toLowerCase()).get();
		if (following != null && following.getFollowing() != null) {
			List<Ref<AppUser>> users = following.getFollowing();
			users = users.subList(index, Math.min(index + Constants.MORE_FOLLOWING_INC, following.getFollowing().size()));
			final boolean moreFollowing = (following.getFollowing().size() - index - users.size()) > 0;
			return new FollowingResult(new ArrayList<AppUser>(ofy().load().refs(users).values()), moreFollowing);
		}

		return new FollowingResult(new ArrayList<AppUser>(), false);
	}

	@ApiMethod(name = "appUser.isFollowing", path = "isFollowing", httpMethod = HttpMethod.GET)
	public BooleanResult isFollowing(@Named("username") final String username) throws UnauthorizedException {
		if (!appUserDao.isAppUserLoggedIn())
			throw new UnauthorizedException("Not logged in");

		final String currentUserUsername = (String)RequestFactoryServlet.getThreadLocalRequest().getSession().getAttribute(AppUserDao.AUTH_USER);
		final Ref<AppUser> userRef = Ref.create(Key.create(AppUser.class, username.toLowerCase()));
		final Following following = ofy().load().type(Following.class).id(currentUserUsername.toLowerCase()).get();

		BooleanResult result = new BooleanResult(following != null && !following.getFollowing().isEmpty() && following.getFollowing().contains(userRef));
		return result;
	}
}
