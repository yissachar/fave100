package com.fave100.server.servlets;

import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

@SuppressWarnings("serial")
public class CustomRequestFactoryServlet extends RequestFactoryServlet{

	static class LoquaciousExceptionHandler implements ExceptionHandler {
		@Override
		public ServerFailure createServerFailure(final Throwable throwable) {
			return new ServerFailure(throwable.getMessage(), throwable.getClass().getName(), null, true);
		}
	}

	public CustomRequestFactoryServlet() {
		super(new LoquaciousExceptionHandler());
	}
}
