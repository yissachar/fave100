package com.fave100.server.domain.favelist;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import javax.inject.Named;

import com.fave100.server.domain.ApiBase;
import com.google.api.server.spi.config.ApiMethod;
import com.google.inject.Inject;

public class FaveListApi extends ApiBase {

	private FaveListDao faveListDao;

	@Inject
	public FaveListApi(FaveListDao faveListDao) {
		this.faveListDao = faveListDao;
	}

	@ApiMethod(name = "faveList.getFaveList", path = "favelist")
	public List<FaveItem> getFaveList(@Named("username") final String username, @Named("hashtag") final String hashtag) {
		final FaveList faveList = faveListDao.findFaveList(username, hashtag);
		if (faveList == null)
			return null;
		return faveList.getList();
	}

	@ApiMethod(name = "faveList.getMasterFaveList", path = "masterFaveList")
	public List<FaveItem> getMasterFaveList(@Named("hashtag") final String hashtag) {
		return ofy().load().type(Hashtag.class).id(hashtag).get().getList();
	}
}
