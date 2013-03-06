package com.fave100.shared.requestfactory;

import com.fave100.server.domain.Song;
import com.fave100.shared.SongInterface;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(Song.class)
public interface SongProxy extends ValueProxy, SongInterface {
}
