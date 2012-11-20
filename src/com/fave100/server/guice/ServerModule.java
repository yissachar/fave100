package com.fave100.server.guice;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.fave100.server.domain.APIKey;
import com.fave100.server.domain.Activity;
import com.fave100.server.domain.AppUser;
import com.fave100.server.domain.FacebookID;
import com.fave100.server.domain.FaveList;
import com.fave100.server.domain.Follower;
import com.fave100.server.domain.GoogleID;
import com.fave100.server.domain.PwdResetToken;
import com.fave100.server.domain.Song;
import com.fave100.server.domain.TwitterID;
import com.fave100.server.domain.Whyline;
import com.googlecode.objectify.ObjectifyService;
import com.gwtplatform.dispatch.server.guice.HandlerModule;

public class ServerModule extends HandlerModule {

	static{
		// Must manually register all datastore entities
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(Song.class);
		ObjectifyService.register(GoogleID.class);
		ObjectifyService.register(TwitterID.class);
		ObjectifyService.register(FacebookID.class);
		ObjectifyService.register(Follower.class);
		ObjectifyService.register(Activity.class);
		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(Whyline.class);
		ObjectifyService.register(APIKey.class);
		ObjectifyService.register(PwdResetToken.class);

		// Set API keys
		final APIKey facebookApiKey = ofy().load().type(APIKey.class).id("facebook").get();
		AppUser.FACEBOOK_APP_ID = facebookApiKey.getKey();
		AppUser.FACEBOOK_APP_SECRET = facebookApiKey.getSecret();
		final APIKey twitterApiKey = ofy().load().type(APIKey.class).id("twitter").get();
		AppUser.TWITTER_CONSUMER_KEY = twitterApiKey.getKey();
		AppUser.TWITTER_CONSUMER_SECRET = twitterApiKey.getSecret();

	}

	@Override
	protected void configureHandlers() {
	}
}
