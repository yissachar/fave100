package com.fave100.client.requestfactory;

import com.fave100.server.domain.Song;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(Song.class)
public interface SongRequest extends RequestContext{
	
	// Currently no need to anything with songs on the client

}
