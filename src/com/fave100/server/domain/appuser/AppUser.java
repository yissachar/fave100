package com.fave100.server.domain.appuser;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fave100.server.bcrypt.BCrypt;
import com.fave100.server.domain.DatastoreObject;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * A Fave100 user.
 * 
 * @author yissachar.radcliffe
 * 
 */

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppUser extends DatastoreObject {

	// Store a case-sensitive username field, as well as lowercase username lookup
	@Id private String usernameID;
	private String username;

	private String password;
	// A case sensitive email address stored directly with user for easy access
	// Must be manually kept in sync with EmailID
	private String email;
	private String avatar;
	private Date joinDate;
	// A denormalized list of all the hashtags the user has
	private List<String> hashtags = new ArrayList<String>();
	private boolean followingPrivate = false;
	private boolean followersPrivate = false;

	@SuppressWarnings("unused")
	private AppUser() {
	}

	public AppUser(final String username) {
		this.username = username;
		this.usernameID = username.toLowerCase();
		this.joinDate = new Date();
	}

	public String getAvatarImage() {
		return (getAvatarImage(80));
	}

	public String getAvatarImage(final int size) {
		if (avatar == null) {
			// If there is no avatar, serve a Gravatar
			final String params = "?d=mm&s=" + size;
			if (getEmail() == null)
				return "http://www.gravatar.com/avatar/" + params;
			try {
				final byte[] bytes = getEmail().toLowerCase().getBytes("UTF-8");
				final BigInteger i = new BigInteger(1, MessageDigest.getInstance("MD5").digest(bytes));
				final String hash = String.format("%1$032x", i);
				return "http://www.gravatar.com/avatar/" + hash + params;
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
			String servingUrl = ImagesServiceFactory.getImagesService().getServingUrl(options);
			// Dev server hack to avoid errors
			servingUrl += "=s" + size + "-c";
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

	// Getters and setters

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public String getUsernameID() {
		return usernameID;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setUsernameID(final String usernameID) {
		this.usernameID = usernameID;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public String getEmail() {
		return email;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setEmail(final String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public String getId() {
		return username;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public String getPassword() {
		return password;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setPassword(final String password) {
		this.password = BCrypt.hashpw(password, BCrypt.gensalt());
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public String getAvatar() {
		return avatar;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setAvatar(final String avatar) {
		this.avatar = avatar;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Date getJoinDate() {
		return joinDate;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setJoinDate(final Date joinDate) {
		this.joinDate = joinDate;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public static void setTwitterConsumerKey(final String key) {
		AppUserDao.TWITTER_CONSUMER_KEY = key;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public static void setTwitterConsumerSecret(final String secret) {
		AppUserDao.TWITTER_CONSUMER_SECRET = secret;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public static void setFacebookApiKey(final String key) {
		AppUserDao.FACEBOOK_APP_ID = key;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public static void setFacebookApiSecret(final String secret) {
		AppUserDao.FACEBOOK_APP_SECRET = secret;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public boolean isFollowingPrivate() {
		return followingPrivate;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setFollowingPrivate(final boolean followingPrivate) {
		this.followingPrivate = followingPrivate;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public boolean isFollowersPrivate() {
		return followersPrivate;
	}

	@JsonIgnore
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setFollowersPrivate(final boolean followersPrivate) {
		this.followersPrivate = followersPrivate;
	}

	public List<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(final List<String> hashtags) {
		this.hashtags = hashtags;
	}

}
