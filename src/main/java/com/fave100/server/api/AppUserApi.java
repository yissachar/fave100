package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fave100.server.SessionHelper;
import com.fave100.server.UrlBuilder;
import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.BooleanResult;
import com.fave100.server.domain.Session;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.appuser.Following;
import com.fave100.server.domain.appuser.FollowingResult;
import com.fave100.server.domain.appuser.PwdResetToken;
import com.fave100.server.domain.appuser.UserInfo;
import com.fave100.server.exceptions.AlreadyFollowingException;
import com.fave100.server.exceptions.CannotFollowYourselfException;
import com.fave100.server.exceptions.EmailIdAlreadyExistsException;
import com.fave100.server.exceptions.NotLoggedInException;
import com.fave100.server.servlets.AvatarUploadServlet;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/" + ApiPaths.APPUSER_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.APPUSER_ROOT, description = "Operations on Users")
public class AppUserApi {

	@GET
	@Path(ApiPaths.GET_APPUSER)
	@ApiOperation(value = "Find a user by their username", response = AppUser.class)
	@ApiResponses(value = {@ApiResponse(code = 404, message = ApiExceptions.USER_NOT_FOUND)})
	public static AppUser getAppUser(@ApiParam(value = "The username", required = true) @PathParam("username") final String username) {
		AppUser appUser = ofy().load().type(AppUser.class).id(username.toLowerCase()).get();
		if (appUser == null)
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(ApiExceptions.USER_NOT_FOUND).build());

		return appUser;
	}

	@GET
	@Path("")
	@ApiOperation(value = "Get the current user", response = AppUser.class)
	public static AppUser getLoggedInAppUser(@Context HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);
		final String username = (String)session.getAttribute(AppUserDao.AUTH_USER);
		if (username != null) {
			return AppUserDao.findAppUser(username);
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
	public static FollowingResult getFollowing(
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
	public static BooleanResult isFollowing(@Context HttpServletRequest request, @QueryParam("username") final String username) {
		if (!AppUserDao.isAppUserLoggedIn(request))
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
	public static BooleanResult isGoogleUserLoggedIn() {
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		return new BooleanResult(user != null);
	}

	// Check if Fave100 user is logged in 
	@GET
	@Path(ApiPaths.IS_APPUSER_LOGGED_IN)
	@ApiOperation(value = "Is app user logged in", response = BooleanResult.class)
	public static BooleanResult isAppUserLoggedIn(@Context HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);

		final String username = (String)session.getAttribute(AppUserDao.AUTH_USER);
		return new BooleanResult(username != null);
	}

	@POST
	@Path(ApiPaths.CREATE_BLOBSTORE_URL)
	@ApiOperation(value = "Create a blobstore upload URL", response = StringResult.class)
	public static StringResult createBlobstoreUrl() {
		final UploadOptions options = UploadOptions.Builder.withMaxUploadSizeBytes(Constants.MAX_AVATAR_SIZE);
		return new StringResult(BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(AvatarUploadServlet.PATH, options));
	}

	@GET
	@Path(ApiPaths.USER_SETTINGS)
	@ApiOperation(value = "Get current user settings", response = UserInfo.class)
	public static UserInfo getCurrentUserSettings(@Context HttpServletRequest request) {
		final AppUser currentUser = getLoggedInAppUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		return new UserInfo(currentUser);
	}

	@POST
	@Path(ApiPaths.USER_SETTINGS)
	@ApiOperation(value = "Set user info", response = BooleanResult.class)
	public static BooleanResult setUserInfo(@Context HttpServletRequest request, final UserInfo userInfo) {
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
	public static void followUser(@Context HttpServletRequest request, @QueryParam("username") final String username) {
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
	public static void unfollowUser(@Context HttpServletRequest request, @QueryParam("username") final String username) {

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
	public static BooleanResult emailPasswordResetToken(@QueryParam("username") final String username, @QueryParam("emailAddress") final String emailAddress) {

		if (!username.isEmpty() && !emailAddress.isEmpty()) {
			final AppUser appUser = AppUserDao.findAppUser(username);
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
	public static BooleanResult changePassword(@Context HttpServletRequest request, @QueryParam("newPassword") final String newPassword,
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
