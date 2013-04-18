package com.fave100.shared.requestfactory;

import java.util.List;

import com.fave100.server.domain.Whyline;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(Whyline.class)
public interface WhylineRequest extends RequestContext {
	Request<List<WhylineProxy>> getWhylinesForSong(SongProxy song);
}
