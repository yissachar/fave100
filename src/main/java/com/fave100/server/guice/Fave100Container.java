package com.fave100.server.guice;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletException;

import com.fave100.server.api.UserProvider;
import com.google.inject.Injector;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

@SuppressWarnings("serial")
public class Fave100Container extends GuiceContainer {

	@Inject
	public Fave100Container(Injector injector) {
		super(injector);
	}

	@Override
	protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig webConfig) throws ServletException {
		ResourceConfig config = new DefaultResourceConfig();
		config.getClasses().add(UserProvider.class);
		return config;
	}

}
