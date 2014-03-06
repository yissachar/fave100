package com.fave100.server.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({FaveListCreationTest.class, FaveListDeletionTest.class, FollowingTest.class, SongApiTest.class, UserCreationTest.class, WhyLineApiTest.class})
public class ApiTestSuite {

}
