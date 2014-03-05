package com.fave100.server;

import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.inject.Singleton;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fave100.server.api.AuthApi;
import com.fave100.server.api.FaveListsApi;
import com.fave100.server.api.SearchApi;
import com.fave100.server.api.SongApi;
import com.fave100.server.api.TrendingApi;
import com.fave100.server.api.UserApi;
import com.fave100.server.api.UsersApi;
import com.fave100.server.domain.APIKey;
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
import com.fave100.server.filters.EncodingFilter;
import com.fave100.server.servlets.HashtagBuilderServlet;
import com.fave100.server.servlets.HashtagEnqueuerServlet;
import com.fave100.server.servlets.PasswordCleanupServlet;
import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.utils.remoteapi.RemoteApiServlet;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;
import com.leacox.dagger.jersey.DaggerContainer;
import com.leacox.dagger.servlet.ServletModule;

import dagger.Module;
import dagger.Provides;

@Module(
		injects = {
					DaggerContainer.class,
					UsersApi.class,
					AuthApi.class,
					AuthApi.class,
					UserApi.class,
					FaveListsApi.class,
					SongApi.class,
					SearchApi.class,
					TrendingApi.class,
					JacksonJsonProvider.class,
					RemoteApiServlet.class,
					PasswordCleanupServlet.class,
					ObjectifyFilter.class,
					EncodingFilter.class,
					HashtagBuilderServlet.class,
					HashtagEnqueuerServlet.class
		},
		includes = ServletModule.class)
public class ServerModule {

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

		final APIKey facebookApiKey = ofy().load().type(APIKey.class).id("facebook").get();
		AppUser.setFacebookApiKey(facebookApiKey.getKey().trim());
		AppUser.setFacebookApiSecret(facebookApiKey.getSecret().trim());

		final APIKey twitterApiKey = ofy().load().type(APIKey.class).id("twitter").get();
		AppUser.setTwitterConsumerKey(twitterApiKey.getKey().trim());
		AppUser.setTwitterConsumerSecret(twitterApiKey.getSecret().trim());

		final APIKey youtubeApiKey = ofy().load().type(APIKey.class).id("youtube").get();
		Song.setYoutubeApiKey(youtubeApiKey.getKey().trim());

		// Let the UrlBuilder know what URLs to build
		UrlBuilder.isDevMode = (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development);
	}

	@Provides
	@Singleton
	JacksonJsonProvider provideJacksonJsonProvider() {
		return new JacksonJsonProvider();
	}

	@Provides
	@Singleton
	RemoteApiServlet provideRemoteApiServlet() {
		return new RemoteApiServlet();
	}

	@Provides
	@Singleton
	ObjectifyFilter provideObjectifyFilter() {
		return new ObjectifyFilter();
	}
}
