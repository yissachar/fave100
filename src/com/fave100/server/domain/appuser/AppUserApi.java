package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;

import com.fave100.server.SessionHelper;
import com.fave100.server.UrlBuilder;
import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.ApiBase;
import com.fave100.server.domain.BooleanResult;
import com.fave100.server.domain.LoginResult;
import com.fave100.server.domain.Session;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.VoidResult;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.BadRequestException;
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
	public LoginResult createAppUser(final HttpServletRequest request, @Named("username") final String username, @Named("password") final String password,
			@Named("email") final String email) throws BadRequestException {
		// TODO: Verify that transaction working and will stop duplicate usernames/googleID completely
		final String userExistsMsg = "A user with that name is already registered";
		final String emailExistsMsg = "A user with that email is already registered";
		LoginResult loginResult = null;
		try {
			loginResult = ofy().transact(new Work<LoginResult>() {
				@Override
				public LoginResult run() {
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

		return loginResult;
	}

	@ApiMethod(name = "appUser.createAppUserFromGoogleAccount", path = "createAppUserFromGoogleAccount")
	public LoginResult createAppUserFromGoogleAccount(final HttpServletRequest request, @Named("username") final String username) throws BadRequestException {

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

		return loginResult;
	}

	@ApiMethod(name = "appUser.createAppUserFromTwitterAccount", path = "createAppUserFromTwitterAccount")
	public LoginResult createAppUserFromTwitterAccount(final HttpServletRequest request, @Named("username") final String username, @Named("oauthVerifier") final String oauth_verifier)
			throws BadRequestException {

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
			throw new BadRequestException(e.getMessage());
		}

		return new LoginResult(newAppUser, session.getId());
	}

	@ApiMethod(name = "appUser.createAppUserFromFacebookAccount", path = "createAppUserFromFacebookAccount")
	public LoginResult createAppUserFromFacebookAccount(final HttpServletRequest request, @Named("username") final String username, @Named("state") final String state,
			@Named("code") final String code, @Named("redirectUrl") final String redirectUrl) throws BadRequestException {

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

		return loginResult;
	}

	@ApiMethod(name = "appUser.login", path = "login")
	public LoginResult login(HttpServletRequest request, @Named("username") final String username, @Named("password") final String password) throws UnauthorizedException {
		AppUser loggingInUser = null;
		String errorMessage = "Invalid credentials";
		Session session = SessionHelper.getSession(request);

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
			session.setAttribute(AppUserDao.AUTH_USER, username);
			ofy().save().entity(session);
		}
		else {
			// Bad username
			throw new UnauthorizedException(errorMessage);
		}
		return new LoginResult(loggingInUser, session.getId());
	}

	@ApiMethod(name = "appUser.loginWithGoogle", path = "googleLogin")
	public LoginResult loginWithGoogle(HttpServletRequest request) {
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

	@ApiMethod(name = "appUser.loginWithTwitter", path = "twitterLogin")
	public LoginResult loginWithTwitter(HttpServletRequest request, @Named("oauthVerifier") final String oauth_verifier) {
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

	@ApiMethod(name = "appUser.loginWithFacebook", path = "facebookLogin")
	public LoginResult loginWithFacebook(HttpServletRequest request, @Named("code") final String code) {
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

	@ApiMethod(name = "appUser.logout", path = "logout")
	public VoidResult logout(HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);
		session.setAttribute(AppUserDao.AUTH_USER, null);
		session.setAttribute("requestToken", null);
		session.setAttribute("twitterUser", null);
		ofy().save().entity(session).now();
		return new VoidResult();
	}

	@ApiMethod(name = "appUser.getLoggedInAppUser", path = "loggedInAppUser")
	public AppUser getLoggedInAppUser(HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);
		final String username = (String)session.getAttribute(AppUserDao.AUTH_USER);
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
	public BooleanResult isFollowing(HttpServletRequest request, @Named("username") final String username) throws UnauthorizedException {
		if (!appUserDao.isAppUserLoggedIn(request))
			throw new UnauthorizedException("Not logged in");

		Session session = SessionHelper.getSession(request);

		final String currentUserUsername = (String)session.getAttribute(AppUserDao.AUTH_USER);
		final Ref<AppUser> userRef = Ref.create(Key.create(AppUser.class, username.toLowerCase()));
		final Following following = ofy().load().type(Following.class).id(currentUserUsername.toLowerCase()).get();

		BooleanResult result = new BooleanResult(following != null && !following.getFollowing().isEmpty() && following.getFollowing().contains(userRef));
		return result;
	}

	/*
	 * Checks if the user is logged into Google (though not necessarily logged
	 * into Fave100)
	 */
	@ApiMethod(name = "appUser.isGoogleLoggedIn", path = "user/google/loggedIn", httpMethod = HttpMethod.GET)
	public BooleanResult isGoogleUserLoggedIn() {
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		return new BooleanResult(user != null);
	}

	// Builds a URL that client can use to log user in to Google Account
	@ApiMethod(name = "appUser.getGoogleLoginURL", path = "user/google/loginUrl")
	public StringResult getGoogleLoginURL(@Named("destinationURL") final String destinationURL) {
		return new StringResult(UserServiceFactory.getUserService().createLoginURL(destinationURL));
	}

	// Builds a Facebook login URL that the client can use
	@ApiMethod(name = "appUser.getFacebookAuthUrl", path = "user/facebook/loginUrl")
	public StringResult getFacebookAuthUrl(HttpServletRequest request, @Named("redirectUrl") final String redirectUrl) throws BadRequestException {
		request.getSession().setAttribute("facebookRedirect", redirectUrl);
		try {
			return new StringResult("https://www.facebook.com/dialog/oauth?client_id=" + AppUserDao.FACEBOOK_APP_ID + "&display=page&redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			throw new BadRequestException("Unsupported encoding");
		}
	}

	// Check if Fave100 user is logged in 
	@ApiMethod(name = "appUser.isAppUserLoggedIn", path = "user/isLoggedIn")
	public BooleanResult isAppUserLoggedIn(HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);

		final String username = (String)session.getAttribute(AppUserDao.AUTH_USER);
		return new BooleanResult(username != null);
	}

	// Builds a Twitter login URL that the client can use
	@ApiMethod(name = "appUser.getTwitterAuthUrl", path = "user/twitterAuthUrl")
	public StringResult getTwitterAuthUrl(HttpServletRequest request, @Named("redirectUrl") final String redirectUrl) {
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

	@ApiMethod(name = "appUser.createBlobstoreUrl", path = "user/createBlobstoreUrl")
	public StringResult createBlobstoreUrl(@Named("successPath") final String successPath) {
		final UploadOptions options = UploadOptions.Builder.withMaxUploadSizeBytes(Constants.MAX_AVATAR_SIZE);
		return new StringResult(BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(successPath, options));
	}

	@ApiMethod(name = "appUser.getCurrentUserSettings", path = "user/settings")
	public UserInfo getCurrentUserSettings(HttpServletRequest request) throws UnauthorizedException {
		final AppUser currentUser = getLoggedInAppUser(request);
		if (currentUser == null)
			throw new UnauthorizedException("Not logged in");

		return new UserInfo(currentUser);
	}

	@ApiMethod(name = "appUser.setUserInfo", path = "user/settings")
	public BooleanResult setUserInfo(HttpServletRequest request, final UserInfo userInfo) throws ForbiddenException {
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
				}
			});
		}
		catch (final RuntimeException re) {
			if (re.getCause() instanceof EmailIDAlreadyExistsException) {
				throw new ForbiddenException("Email already exists");
			}
		}

		return new BooleanResult(true);
	}

	@ApiMethod(name = "appUser.followUser", path = "user/followUser")
	public VoidResult followUser(HttpServletRequest request, @Named("username") final String username) throws UnauthorizedException, ForbiddenException {
		final AppUser currentUser = getLoggedInAppUser(request);

		if (currentUser == null)
			throw new UnauthorizedException("Not logged in");

		// Check if user trying to follow themselves
		if (currentUser.getUsername().equals(username))
			throw new ForbiddenException("You cannot follow yourself");

		final Ref<AppUser> userRef = Ref.create(Key.create(AppUser.class, username.toLowerCase()));
		Following following = ofy().load().type(Following.class).id(currentUser.getId()).get();
		if (following == null) {
			following = new Following(currentUser.getId());
			ofy().save().entity(following).now();
		}

		// Check if already following that user
		if (following.getFollowing().contains(userRef))
			throw new ForbiddenException("You are already following that user");

		following.getFollowing().add(userRef);
		ofy().save().entity(following).now();

		return new VoidResult();
	}

	@ApiMethod(name = "appUser.unfollowUser", path = "user/unfollow")
	public VoidResult unfollowUser(HttpServletRequest request, @Named("username") final String username) throws UnauthorizedException {
		final AppUser currentUser = getLoggedInAppUser(request);
		if (currentUser == null)
			throw new UnauthorizedException("Not logged in");

		final Following following = ofy().load().type(Following.class).id(currentUser.getId()).get();
		if (following == null)
			return new VoidResult();

		following.getFollowing().remove(Ref.create(Key.create(AppUser.class, username.toLowerCase())));
		ofy().save().entity(following).now();

		return new VoidResult();
	}

	// Emails user a password reset token if they forget their password
	@ApiMethod(name = "appUser.emailPasswordResetToken", path = "user/email/reset/")
	public BooleanResult emailPasswordResetToken(@Named("username") final String username, @Named("emailAddress") final String emailAddress) {
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
	@ApiMethod(name = "appUser.changePassword", path = "user/password/change")
	public BooleanResult changePassword(HttpServletRequest request, @Named("newPassoword") final String newPassword, @Named("tokenOrPassword") final String tokenOrPassword)
			throws UnauthorizedException {

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
				throw new UnauthorizedException("Not logged in");
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
