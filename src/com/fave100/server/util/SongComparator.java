package com.fave100.server.util;

import java.util.Comparator;

import com.fave100.server.domain.Song;

public class SongComparator implements Comparator<Song>
{
    @Override
    public int compare(final Song x, final Song y)
    {
    	if(x != null && y == null) {
    		return 1;
    	} else if(y != null && x == null) {
    		return -1;
    	} else if(x == null && y == null) {
    		return 0;
    	}

    	if(x.getScore() < y.getScore()) {
    		return -1;
    	} else if(x.getScore() > y.getScore()) {
    		return 1;
    	}
        return 0;
    }
}
