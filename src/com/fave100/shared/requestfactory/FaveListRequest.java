package com.fave100.shared.requestfactory;

import java.util.List;

import com.fave100.server.domain.favelist.FaveList;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(FaveList.class)
public interface FaveListRequest extends RequestContext {

	Request<List<FaveItemProxy>> getFaveListForCurrentUser(String hashtag);

	Request<Void> addFaveItemForCurrentUser(String hashtag, String songID);

	Request<Void> removeFaveItemForCurrentUser(String hashtag, String songID);

	Request<Void> rerankFaveItemForCurrentUser(String hashtag, String songID, int newIndex);

	Request<Void> editWhylineForCurrentUser(String hashtag, String songID, String whyline);

	Request<List<FaveItemProxy>> getFaveList(String username, String hashtag);

}
