package com.fave100.shared.requestfactory;

import com.fave100.server.domain.Song;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(Song.class)
public interface SongRequest extends RequestContext {

	Request<SongProxy> findSong(String id);

	Request<String> getYouTubeResults(String song, String artist);
}
