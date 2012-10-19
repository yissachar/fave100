package com.fave100.client.pages.search;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

@SuppressWarnings("serial")
public class MusicbrainzResultList extends ArrayList<MusicbrainzResult>{
	
	public MusicbrainzResultList(final String data) {
		try {
    	    // parse the XML document into a DOM
    	    final Document messageDom = XMLParser.parse(data);	
    	    
    	    // Get all results
    	    final NodeList recordingNodes = messageDom.getElementsByTagName("recording");
    	    final int length = recordingNodes.getLength();
    	    
    	    // Hash map to ensure we only show one results per songTitle:Artist
    	    final HashMap<String, String> uniqueSongMap = new HashMap<String, String>();
    	    
    	    for(int i = 0; i < length; i++) {
    	    	
    	    	String mbid = "";
    	    	String track = "";
    	    	String artist = "";
    	    	String releaseDate = "";
    	    	
    	    	final Element recordingElement = (Element) messageDom.getElementsByTagName("recording").item(i);
    	    	
    	    	// Musicbrainz ID
    	    	mbid = recordingElement.getAttribute("id");
    	    	
    	    	// Track name
    	    	final Element titleElement = (Element) recordingElement.getElementsByTagName("title").item(0);
    	    	track = titleElement.getChildNodes().item(0).getNodeValue();
    	    	
    	    	// Artist name
    	    	final Element artistElement = (Element) recordingElement.getElementsByTagName("artist").item(0);
    	    	if(artistElement != null) {
    	    		final Node artistNameNode = artistElement.getChildNodes().item(0);
    	    		//artist = artistNameNode.getNodeName();
    	    		if(artistNameNode.getNodeName().equals("name")) {
    	    	    	artist = artistNameNode.getChildNodes().item(0).getNodeValue();
    	    	    }
    	    		
    	    	}
    	    	
    	    	// Release date
    	    	final Element dateElement = (Element) recordingElement.getElementsByTagName("date").item(0);
    	    	if(dateElement != null) {
    	    		releaseDate = dateElement.getChildNodes().item(0).getNodeValue();
    	    	}
    	    	
    	    	final String separatorToken = ":%:";
    	    	
    	    	// SongTitle+Artist is unique, add it
    	    	if(uniqueSongMap.get(track+separatorToken+artist) == null) {
    	    		final MusicbrainzResult result = new MusicbrainzResult();
        	    	result.setMbid(mbid);
        	    	result.setTrackName(track);
        	    	result.setArtistName(artist);    	    	
        	    	result.setReleaseDate(releaseDate);
        	    	add(result);
        	    	
        	    	uniqueSongMap.put(track+separatorToken+artist, "unique");
    	    	}
    	    	
    	    }

    	  } catch (final DOMException e) {
    		  // TODO: Handle failed XML parsing 
    	    //Window.alert("Could not parse XML document.");
    	  }
	}

}
