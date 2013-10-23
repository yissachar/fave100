package com.fave100.server.servlets;

import com.fave100.server.guice.GuiceServiceLayer;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

/**
 * This custom RequestFactory servlet allows for the passing of custom server
 * errors to the client in a RequestFActory request.
 * 
 * @author yissachar.radcliffe
 * 
 */
@SuppressWarnings("serial")
public class CustomRequestFactoryServlet extends RequestFactoryServlet {

	@Inject
	CustomRequestFactoryServlet(ExceptionHandler exceptionHandler, GuiceServiceLayer guiceSL) {
		super(exceptionHandler, guiceSL);
	}
}
