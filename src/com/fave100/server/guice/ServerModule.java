package com.fave100.server.guice;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.fave100.server.domain.APIKey;
import com.fave100.server.domain.Song;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.FacebookID;
import com.fave100.server.domain.appuser.Follower;
import com.fave100.server.domain.appuser.GoogleID;
import com.fave100.server.domain.appuser.PwdResetToken;
import com.fave100.server.domain.appuser.TwitterID;
import com.fave100.server.domain.favelist.FaveList;
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

		final APIKey youtubeApiKey =  ofy().load().type(APIKey.class).id("youtube").get();
		Song.YOUTUBE_API_KEY = youtubeApiKey.getKey();

	}

	@Override
	protected void configureHandlers() {
	}
}
