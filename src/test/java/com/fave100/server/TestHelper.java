package com.fave100.server;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class TestHelper {

	public static final String SHOULD_THROW_EXCEPTION_MSG = "Should throw an exception";
	// This is used for mocking, we need to pass in the method name for Mockito to properly stub it
	public static final String GET_LOGGED_IN_USER_METHOD_NAME = "getLoggedInUser";

	public static HttpServletRequest newReq() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpSession session = mock(HttpSession.class);
		when(req.getSession()).thenReturn(session);
		return req;
	}

}
