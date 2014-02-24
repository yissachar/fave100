package com.fave100.server.domain.appuser;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.scribe.oauth.OAuthService;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.server.SessionHelper;
import com.fave100.server.UrlBuilder;
import com.fave100.server.domain.Session;
import com.fave100.shared.Constants;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AppUserDao {

	public static String TWITTER_CONSUMER_KEY = "";
	public static String TWITTER_CONSUMER_SECRET = "";
	public static String FACEBOOK_APP_ID = "";
	public static String FACEBOOK_APP_SECRET = "";
	public static final String AUTH_USER = "loggedIn";
	private static OAuthService facebookOAuthservice;
	private static TwitterFactory twitterFactory;

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

	public static Twitter getTwitterInstance() {
		if (twitterFactory == null) {
			twitterFactory = new TwitterFactory();
		}
		return twitterFactory.getInstance();
	}

	// Gets a Twitter user - not a Fave100 user
	public static twitter4j.User getTwitterUser(HttpServletRequest request, final String oauth_verifier) {
		Session session = SessionHelper.getSession(request);
		final twitter4j.User user = (twitter4j.User)session.getAttribute("twitterUser");
		if (user == null) {
			final Twitter twitter = getTwitterInstance();
			twitter.setOAuthConsumer(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);

			final RequestToken requestToken = (RequestToken)session.getAttribute("requestToken");
			try {
				final AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
				twitter.setOAuthAccessToken(accessToken);
				final twitter4j.User twitterUser = twitter.verifyCredentials();
				request.getSession().setAttribute("twitterUser", twitterUser);
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

	// Gets the ID of a the current Facebook user - not a Fave100 user
	public static Long getCurrentFacebookUserId(HttpServletRequest request, final String code) {
		Session session = SessionHelper.getSession(request);

		Long userID = (Long)session.getAttribute("facebookID");
		if (userID == null) {
			String redirectUrl = (String)session.getAttribute("facebookRedirect");
			if (redirectUrl == null) {
				redirectUrl = new UrlBuilder(NameTokens.register).with("register", RegisterPresenter.PROVIDER_FACEBOOK).getUrl().replace("yissachar", "localhost");
			}
			try {
				final String authURL = "https://graph.facebook.com/oauth/access_token?client_id=" + FACEBOOK_APP_ID + "&redirect_uri="
						+ URLEncoder.encode(redirectUrl, "UTF-8") + "&client_secret=" + FACEBOOK_APP_SECRET + "&code=" + code;
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
					request.getSession().setAttribute("facebookID", userID);
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

	// Check if Fave100 user is logged in 
	public static Boolean isAppUserLoggedIn(HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);
		final String username = (String)session.getAttribute(AUTH_USER);
		return username != null;
	}

	public static String createBlobstoreUrl(final String successPath) {
		final UploadOptions options = UploadOptions.Builder.withMaxUploadSizeBytes(Constants.MAX_AVATAR_SIZE);
		return BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(successPath, options);
	}

}
