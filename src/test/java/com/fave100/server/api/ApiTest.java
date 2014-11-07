package com.fave100.server.api;

import org.junit.After;
import org.junit.Before;

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
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyFilter;
import com.googlecode.objectify.ObjectifyService;

public abstract class ApiTest {

	static {
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
	}

	private final LocalDatastoreServiceTestConfig config = new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(100);
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(config);

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		ObjectifyFilter.complete();
		helper.tearDown();
	}

	protected void setDefaultHighRepJobPolicyUnappliedJobPercentage(float percentage) {
		config.setDefaultHighRepJobPolicyUnappliedJobPercentage(percentage);
	}
}
