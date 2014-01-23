package com.fave100.shared.requestfactory;

import com.fave100.server.domain.favelist.FaveListDao;
import com.fave100.server.guice.GuiceServiceLocator;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(value = FaveListDao.class, locator = GuiceServiceLocator.class)
public interface FaveListRequest extends RequestContext {

}
