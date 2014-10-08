package com.fave100.server.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({SongApiTest.class, AuthApiTest.class, FaveListsApiTest.class, SearchApiTest.class, UsersApiTest.class,
				UserApiTest.class, TrendingApiTest.class, AdminTest.class})
public class ApiTestSuite {

}
