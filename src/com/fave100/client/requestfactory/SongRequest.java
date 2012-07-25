package com.fave100.client.requestfactory;

import com.fave100.server.domain.Song;
import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(Song.class)
public interface SongRequest extends RequestContext{
	
	Request<SongProxy> findSong(Long id);
	InstanceRequest<SongProxy, SongProxy> persist();
	InstanceRequest<SongProxy, Void> remove();

}
