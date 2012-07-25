package com.fave100.server;

import com.fave100.server.domain.AppUser;
import com.fave100.server.domain.FaveItem;
import com.fave100.server.domain.Song;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

public class DAO extends DAOBase{
	
	static{
		ObjectifyService.register(FaveItem.class);
		ObjectifyService.register(AppUser.class);
		ObjectifyService.register(Song.class);
	}

}
