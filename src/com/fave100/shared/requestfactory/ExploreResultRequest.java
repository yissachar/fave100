package com.fave100.shared.requestfactory;

import java.util.List;

import com.fave100.server.domain.ExploreResult;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(ExploreResult.class)
public interface ExploreResultRequest extends RequestContext {

	Request<List<ExploreResultProxy>> getExploreFeed();
}
