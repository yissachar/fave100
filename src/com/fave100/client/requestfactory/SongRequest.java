package com.fave100.client.requestfactory;

import java.util.List;

import com.fave100.server.domain.Song;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(Song.class)
public interface SongRequest extends RequestContext {

	Request<SongProxy> findSongByTitleAndArtist(String title, String artist);
	Request<String> getYouTubeResults(String song, String artist);
	Request<List<SongProxy>> getAutocomplete(String songTerm);
	Request<List<SongProxy>> searchSong(String song, int offset);
	Request<List<SongProxy>> searchArtist(String artist, int offset);
	Request<List<SongProxy>> search(String song, String artist, int offset);
}
