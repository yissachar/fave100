package com.fave100.shared.requestfactory;

import java.util.List;

import com.fave100.server.domain.favelist.FaveList;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(FaveList.class)
public interface FaveListRequest extends RequestContext {

	Request<List<FaveItemProxy>> getFaveItemsForCurrentUser(String hashtag);
	Request<Void> addFaveItemForCurrentUser(String hashtag,	String songTitle, String artist);
	Request<Void> removeFaveItemForCurrentUser(String hashtag, int index);
	Request<Void> rerankFaveItemForCurrentUser(String hashtag, int currentIndex, int newIndex);
	Request<Void> editWhylineForCurrentUser(String hashtag, int index, String whyline);

	Request<List<FaveItemProxy>> getFaveList(String username, String hashtag);

}
