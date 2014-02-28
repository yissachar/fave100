package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.UnsupportedEncodingException;
import java.util.Date;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
import com.fave100.server.domain.appuser.PwdResetToken;
import com.fave100.server.domain.appuser.UserInfo;
import com.fave100.server.exceptions.EmailIdAlreadyExistsException;
import com.fave100.server.exceptions.NotLoggedInException;
import com.fave100.server.servlets.AvatarUploadServlet;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.googlecode.objectify.VoidWork;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/" + ApiPaths.USER_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.USER_ROOT, description = "Account operations")
public class UserApi {

	@GET
	@Path(ApiPaths.CURRENT_USER)
	@ApiOperation(value = "Get the current user", response = AppUser.class)
	public static AppUser getLoggedInUser(@Context HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);
		final String username = (String)session.getAttribute(AppUserDao.AUTH_USER);
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
	@GET
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
	@GET
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

}
