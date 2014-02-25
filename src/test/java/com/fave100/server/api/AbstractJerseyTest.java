package com.fave100.server.api;

import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly2.web.GrizzlyWebTestContainerFactory;

public abstract class AbstractJerseyTest extends JerseyTest {

	public AbstractJerseyTest(String resource) throws Exception {
		super(resource);
	}

	@Override
	protected TestContainerFactory getTestContainerFactory() {
		return new GrizzlyWebTestContainerFactory();
	}
}
