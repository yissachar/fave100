package com.fave100.client.requestfactory;

import java.util.List;

import com.fave100.server.domain.FaveList;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(FaveList.class)
public interface FaveListRequest extends RequestContext {
	
	Request<FaveListProxy> findFaveList(String id);
	
	Request<List<SongProxy>> getFaveItemsForCurrentUser(String hashtag);
	Request<Void> removeFaveItemForCurrentUser(String hashtag, int index);
	Request<Void> rerankFaveItemForCurrentUser(String hashtag, int currentIndex, int newIndex);
	Request<Void> editWhylineForCurrentUser(String hashtag, int index, String whyline);
	
	Request<List<SongProxy>> getMasterFaveList();	
	Request<List<SongProxy>> getFaveList(String username, String hashtag);

	Request<Void> addFaveItemForCurrentUser(String hashtag, String id,
			String songTitle, String artist);
	
}
