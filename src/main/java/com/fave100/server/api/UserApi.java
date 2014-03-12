package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fave100.server.UrlBuilder;
import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.BooleanResult;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.WhylineEdit;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.appuser.Following;
import com.fave100.server.domain.appuser.PwdResetToken;
import com.fave100.server.domain.appuser.UserInfo;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.server.exceptions.AlreadyFollowingException;
import com.fave100.server.exceptions.CannotFollowYourselfException;
import com.fave100.server.exceptions.EmailIdAlreadyExistsException;
import com.fave100.server.exceptions.FaveItemAlreadyInListException;
import com.fave100.server.exceptions.FaveListAlreadyExistsException;
import com.fave100.server.exceptions.FaveListLimitReachedException;
import com.fave100.server.exceptions.FaveListSizeReachedException;
import com.fave100.server.exceptions.NotLoggedInException;
import com.fave100.server.servlets.AvatarUploadServlet;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/" + ApiPaths.USER_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.USER_ROOT, description = "Account operations")
public class UserApi {

	@GET
	@ApiOperation(value = "Get the current user", response = AppUser.class)
	public static AppUser getLoggedInUser(@Context HttpServletRequest request) {
		final String username = (String)request.getSession().getAttribute(AppUserDao.AUTH_USER);
		if (username != null) {
			return AppUserDao.findAppUser(username);
		}
		else {
			return null;
		}
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
		final AppUser currentUser = getLoggedInUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		return new UserInfo(currentUser);
	}

	@POST
	@Path(ApiPaths.USER_SETTINGS)
	@ApiOperation(value = "Set user info", response = BooleanResult.class)
	public static BooleanResult setUserInfo(@Context HttpServletRequest request, final UserInfo userInfo) {
		final AppUser currentUser = getLoggedInUser(request);
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

	// Emails user a password reset token if they forget their password
	@POST
	@Path(ApiPaths.PASSWORD_RESET)
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
	@POST
	@Path(ApiPaths.PASSWORD_CHANGE)
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
			appUser = getLoggedInUser(request);
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

	@PUT
	@Path(ApiPaths.USER_FAVELISTS)
	@ApiOperation(value = "Add a FaveList")
	@ApiResponses(value = {@ApiResponse(code = 400, message = "The list name did not pass validation"), @ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN),
							@ApiResponse(code = 403, message = ApiExceptions.FAVELIST_LIMIT_REACHED), @ApiResponse(code = 403, message = ApiExceptions.FAVELIST_ALREADY_EXISTS)})
	public static void addFaveListForCurrentUser(@Context HttpServletRequest request, @ApiParam(value = "The list name", required = true) @PathParam("list") final String listName) {

		final String error = Validator.validateHashtag(listName);
		if (error != null) {
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
		}

		final AppUser currentUser = getLoggedInUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		// -1 because #alltime is a default list not stored in the hashtags list
		if (currentUser.getHashtags().size() >= Constants.MAX_LISTS_PER_USER - 1)
			throw new FaveListLimitReachedException();

		final String username = currentUser.getUsername();

		if (FaveListDao.findFaveList(username, listName) != null)
			throw new FaveListAlreadyExistsException();

		currentUser.getHashtags().add(listName);
		final FaveList faveList = new FaveList(username, listName);
		// Transaction to ensure no duplicate hashtags created
		ofy().transact(new VoidWork() {
			@Override
			public void vrun() {
				Hashtag hashtag = ofy().load().type(Hashtag.class).id(listName).now();
				// Hashtag already exists, add it to user's lists
				if (hashtag != null) {
					ofy().save().entities(currentUser, faveList).now();
				}
				// Create a new hashtag
				else {
					hashtag = new Hashtag(listName, username);
					ofy().save().entities(currentUser, faveList, hashtag).now();
				}
			}
		});

		return;
	}

	@DELETE
	@Path(ApiPaths.USER_FAVELISTS)
	@ApiOperation(value = "Delete a FaveList")
	@ApiResponses(value = {@ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN)})
	public static void deleteFaveListForCurrentUser(@Context HttpServletRequest request, @PathParam("list") final String listName) {
		final AppUser currentUser = getLoggedInUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		currentUser.getHashtags().remove(listName);
		ofy().save().entity(currentUser).now();

		FaveList listToDelete = ofy().load().type(FaveList.class).id(currentUser.getUsername().toLowerCase() + FaveListDao.SEPERATOR_TOKEN + listName.toLowerCase()).now();

		// Get associated WhyLines and mark for deletion
		List<Ref<Whyline>> whylinesToDelete = new ArrayList<>();
		for (FaveItem faveItem : listToDelete.getList()) {
			Ref<Whyline> whylineRef = faveItem.getWhylineRef();
			if (whylineRef != null)
				whylinesToDelete.add(whylineRef);
		}

		// Delete FaveList
		ofy().delete().entity(listToDelete).now();

		// Delete associated WhyLines
		ofy().delete().entities(whylinesToDelete).now();

		return;
	}

	@PUT
	@Path(ApiPaths.USER_FAVEITEMS)
	@ApiOperation(value = "Add a FaveItem")
	@ApiResponses(value = {@ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN), @ApiResponse(code = 403, message = ApiExceptions.FAVELIST_SIZE_REACHED),
							@ApiResponse(code = 403, message = ApiExceptions.FAVEITEM_ALREADY_IN_LIST)})
	public static void addFaveItemForCurrentUser(@Context HttpServletRequest request, @PathParam("list") final String listName, @PathParam("id") final String songID) {

		final AppUser currentUser = getLoggedInUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = FaveListDao.findFaveList(currentUser.getUsername(), listName);

		// Check FaveList size limit reached
		if (faveList.getList().size() >= FaveListDao.MAX_FAVES)
			throw new FaveListSizeReachedException();

		// Get the song from Lucene lookup
		final FaveItem newFaveItem = SongApi.getSong(songID);
		if (newFaveItem == null)
			return;

		// Check if it is a unique song for this user
		boolean unique = true;
		for (final FaveItem faveItem : faveList.getList()) {
			if (faveItem.getSongID().equals(newFaveItem.getSongID())) {
				unique = false;
			}
		}

		// Check if FaveItem is already is in the list
		if (!unique)
			throw new FaveItemAlreadyInListException();

		// Create the new FaveItem
		faveList.getList().add(newFaveItem);
		ofy().save().entities(faveList).now();

		return;
	}

	@DELETE
	@Path(ApiPaths.USER_FAVEITEMS)
	@ApiOperation(value = "Remove a FaveItem")
	@ApiResponses(value = {@ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN)})
	public static void removeFaveItemForCurrentUser(@Context HttpServletRequest request, @PathParam("list") final String hashtag, @PathParam("id") final String songID) {

		final AppUser currentUser = getLoggedInUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = FaveListDao.findFaveList(currentUser.getUsername(), hashtag);
		if (faveList == null)
			return;

		// Find the song to remove
		FaveItem faveItemToRemove = null;
		for (final FaveItem faveItem : faveList.getList()) {
			if (faveItem.getSongID().equals(songID)) {
				faveItemToRemove = faveItem;
				break;
			}
		}

		if (faveItemToRemove == null)
			return;

		// We must also delete the whyline if it exists
		final Ref<Whyline> currentWhyline = faveItemToRemove.getWhylineRef();
		if (currentWhyline != null) {
			ofy().delete().key(currentWhyline.getKey()).now();
		}
		faveList.getList().remove(faveItemToRemove);
		ofy().save().entities(faveList).now();

		return;
	}

	@POST
	@Path(ApiPaths.EDIT_RANK)
	@ApiOperation(value = "Rerank a FaveItem")
	@ApiResponses(value = {@ApiResponse(code = 400, message = ApiExceptions.INVALID_FAVELIST_INDEX), @ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN)})
	public static void rerankFaveItemForCurrentUser(@Context HttpServletRequest request, @PathParam("list") final String hashtag, @PathParam("id") final String songID,
			final Integer newIndex) {

		final AppUser currentUser = getLoggedInUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final FaveList faveList = FaveListDao.findFaveList(currentUser.getUsername(), hashtag);
		if (faveList == null)
			return;

		// Make sure new index is valid
		if (newIndex < 0 || newIndex >= faveList.getList().size())
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(ApiExceptions.INVALID_FAVELIST_INDEX).build());

		// Find the song to change position
		FaveItem faveItemToRerank = null;
		for (final FaveItem faveItem : faveList.getList()) {
			if (faveItem.getSongID().equals(songID)) {
				faveItemToRerank = faveItem;
				break;
			}
		}

		if (faveItemToRerank == null)
			return;

		faveList.getList().remove(faveItemToRerank);
		faveList.getList().add(newIndex, faveItemToRerank);
		ofy().save().entities(faveList).now();

		return;
	}

	@POST
	@Path(ApiPaths.EDIT_WHYLINE)
	@ApiOperation(value = "Edit a WhyLine")
	@ApiResponses(value = {@ApiResponse(code = 400, message = "WhyLine did not pass validation"), @ApiResponse(code = 401, message = ApiExceptions.NOT_LOGGED_IN)})
	public static void editWhylineForCurrentUser(@Context HttpServletRequest request, WhylineEdit whylineEdit) {

		String listName = whylineEdit.getListName();
		String songId = whylineEdit.getSongId();
		String whyline = whylineEdit.getWhyline();

		Objects.requireNonNull(listName);
		Objects.requireNonNull(songId);
		Objects.requireNonNull(whyline);

		// First check that the whyline is valid
		final String whylineError = Validator.validateWhyline(whyline);
		if (whylineError != null)
			// Whyline does not meet validation
			throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(whyline).build());

		final AppUser currentUser = getLoggedInUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();
		final FaveList faveList = FaveListDao.findFaveList(currentUser.getUsername(), listName);
		Objects.requireNonNull(faveList);

		// Find the song to edit whyline
		FaveItem faveItemToEdit = null;
		for (final FaveItem faveItem : faveList.getList()) {
			if (faveItem.getSongID().equals(songId)) {
				faveItemToEdit = faveItem;
				break;
			}
		}

		Objects.requireNonNull(faveItemToEdit);

		// Set the denormalized whyline for the FaveItem
		faveItemToEdit.setWhyline(whyline);

		// Set the external Whyline
		final Ref<Whyline> currentWhyline = faveItemToEdit.getWhylineRef();
		if (currentWhyline == null) {
			// Create a new Whyline entity
			final Whyline whylineEntity = new Whyline(whyline, faveItemToEdit.getSongID(), currentUser.getUsername());
			ofy().save().entity(whylineEntity).now();
			faveItemToEdit.setWhylineRef(Ref.create(whylineEntity));
		}
		else {
			// Just modify the existing Whyline entity
			final Whyline whylineEntity = (Whyline)ofy().load().value(currentWhyline).now();
			whylineEntity.setWhyline(whyline);
			ofy().save().entity(whylineEntity).now();
		}

		ofy().save().entity(faveList).now();

		return;
	}

	@GET
	@Path(ApiPaths.USER_FOLLOWING)
	@ApiOperation(value = "Is following", response = BooleanResult.class)
	public static BooleanResult isFollowing(@Context HttpServletRequest request, @PathParam("user") final String username) {
		if (!AppUserDao.isAppUserLoggedIn(request))
			throw new NotLoggedInException();

		final String currentUserUsername = (String)request.getSession().getAttribute(AppUserDao.AUTH_USER);
		final Ref<AppUser> userRef = Ref.create(Key.create(AppUser.class, username.toLowerCase()));
		final Following following = ofy().load().type(Following.class).id(currentUserUsername.toLowerCase()).now();

		BooleanResult result = new BooleanResult(following != null && !following.getFollowing().isEmpty() && following.getFollowing().contains(userRef));
		return result;
	}

	@PUT
	@Path(ApiPaths.USER_FOLLOWING)
	@ApiOperation(value = "Follow user")
	public static void followUser(@Context HttpServletRequest request, @PathParam("user") final String username) {
		final AppUser currentUser = getLoggedInUser(request);

		if (currentUser == null)
			throw new NotLoggedInException();

		// Check if user trying to follow themselves
		if (currentUser.getUsername().equals(username))
			throw new CannotFollowYourselfException();

		final Ref<AppUser> userRef = Ref.create(Key.create(AppUser.class, username.toLowerCase()));
		Following following = ofy().load().type(Following.class).id(currentUser.getId()).now();
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

	@DELETE
	@Path(ApiPaths.USER_FOLLOWING)
	@ApiOperation(value = "Unfollow user")
	public static void unfollowUser(@Context HttpServletRequest request, @PathParam("user") final String username) {

		final AppUser currentUser = getLoggedInUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final Following following = ofy().load().type(Following.class).id(currentUser.getId()).now();
		if (following == null)
			return;

		following.getFollowing().remove(Ref.create(Key.create(AppUser.class, username.toLowerCase())));
		ofy().save().entity(following).now();

		return;
	}

}
