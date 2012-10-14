package com.fave100.client.pages.search;

import java.util.ArrayList;

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
    	    		// TODO: Date format inconsistent, need to manage discrepancies
    	    		releaseDate = dateElement.getChildNodes().item(0).getNodeValue();
    	    	}
    	    	
    	    	final MusicbrainzResult result = new MusicbrainzResult();
    	    	result.setMbid(mbid);
    	    	result.setTrackName(track);
    	    	result.setArtistName(artist);    	    	
    	    	result.setReleaseDate(releaseDate);
    	    	add(result);
    	    }

    	  } catch (final DOMException e) {
    		  // TODO: Handle failed XML parsing 
    	    //Window.alert("Could not parse XML document.");
    	  }
	}

}
