package com.fave100.server.domain.favelist;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import javax.inject.Named;

import com.fave100.shared.Constants;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

@Api(name = "fave100", version = "v1")
public class Fave100Api {

	@ApiMethod(name = "fave100.getMasterFaveList", path = "masterFaveList")
	public List<FaveItem> getMasterFaveList(@Named("hashtag") final String hashtag) {
		return ofy().load().type(Hashtag.class).id(Constants.DEFAULT_HASHTAG).get().getList();
	}
}
