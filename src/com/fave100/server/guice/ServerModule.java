package com.fave100.server.guice;

import com.fave100.server.domain.Activity;
import com.fave100.server.domain.AppUser;
import com.fave100.server.domain.FaveList;
import com.fave100.server.domain.Follower;
import com.fave100.server.domain.GoogleID;
import com.fave100.server.domain.Song;
import com.fave100.server.domain.TwitterID;
import com.fave100.server.domain.Whyline;
import com.googlecode.objectify.ObjectifyService;
import com.gwtplatform.dispatch.server.guice.HandlerModule;

public class ServerModule extends HandlerModule {

	static{
		// Must manually register all datastore entities
		//ObjectifyService.register(FaveItem.class);
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(Song.class);
		ObjectifyService.register(GoogleID.class);
		ObjectifyService.register(TwitterID.class);
		ObjectifyService.register(Follower.class);
		ObjectifyService.register(Activity.class);
		ObjectifyService.register(FaveList.class);
		ObjectifyService.register(Whyline.class);
	}
	
	@Override
	protected void configureHandlers() {
	}
}
