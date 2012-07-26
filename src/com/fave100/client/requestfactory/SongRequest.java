package com.fave100.client.requestfactory;

import com.fave100.server.domain.Song;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(Song.class)
public interface SongRequest extends RequestContext{
	
	// Currently no need to really do anything with songs on the client
	// but need at least one Request or RequestFactory doesn't "know" about 
	// the class and throws exceptions
	Request<SongProxy> findSong(Long id);

}
