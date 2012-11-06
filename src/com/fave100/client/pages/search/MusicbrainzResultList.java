package com.fave100.client.pages.search;

import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;

@SuppressWarnings("serial")
public class MusicbrainzResultList extends ArrayList<MusicbrainzResult>{
	
	public MusicbrainzResultList(final AutocompleteJSON json) {
		
		// Use JS Overlays to turn JSON into MusicbrainzResult objects
		
   		final JsArray<JSONResult> results = json.getEntries();
        for (int i = 0; i < results.length(); i++) {
        	
        	final JSONResult jsonResult = results.get(i);
        	
        	final MusicbrainzResult result = new MusicbrainzResult();
        	result.setTrackName(jsonResult.getTrackName());
        	result.setArtistName(jsonResult.getArtistName());
        	result.setMbid(jsonResult.getMbid());
	    	add(result);
	    }	
	}
}