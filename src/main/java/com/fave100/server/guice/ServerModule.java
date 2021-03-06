package com.fave100.server.guice;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.fave100.server.domain.APIKey;
import com.fave100.server.domain.FeaturedLists;
import com.fave100.server.domain.Song;
import com.fave100.server.domain.Whyline;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.EmailID;
import com.fave100.server.domain.appuser.FacebookID;
import com.fave100.server.domain.appuser.Following;
import com.fave100.server.domain.appuser.GoogleID;
import com.fave100.server.domain.appuser.PwdResetToken;
import com.fave100.server.domain.appuser.TwitterID;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.server.domain.favelist.TrendingList;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyService;

public class ServerModule extends ServletModule {

	static {
		// Must manually register all datastore entities
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(Song.class);
		ObjectifyService.register(EmailID.class);
		ObjectifyService.register(GoogleID.class);
		ObjectifyService.register(TwitterID.class);
		ObjectifyService.register(FacebookID.class);
		ObjectifyService.register(Following.class);
		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(Whyline.class);
		ObjectifyService.register(APIKey.class);
		ObjectifyService.register(PwdResetToken.class);
		ObjectifyService.register(Hashtag.class);
		ObjectifyService.register(FeaturedLists.class);
		ObjectifyService.register(TrendingList.class);

		final APIKey facebookApiKey = ofy().load().type(APIKey.class).id("facebook").now();
		AppUser.setFacebookApiKey(facebookApiKey.getKey().trim());
		AppUser.setFacebookApiSecret(facebookApiKey.getSecret().trim());

		final APIKey twitterApiKey = ofy().load().type(APIKey.class).id("twitter").now();
		AppUser.setTwitterConsumerKey(twitterApiKey.getKey().trim());
		AppUser.setTwitterConsumerSecret(twitterApiKey.getSecret().trim());

		final APIKey youtubeApiKey = ofy().load().type(APIKey.class).id("youtube").now();
		Song.setYoutubeApiKey(youtubeApiKey.getKey().trim());
	}
}
