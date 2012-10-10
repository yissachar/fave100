package com.fave100.client.pages.search;

import java.util.ArrayList;

import com.google.gwt.xml.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class MusicbrainzResultList extends ArrayList<MusicbrainzResult>{
	
	public MusicbrainzResultList(final String data) {
		try {
    	    // parse the XML document into a DOM
    	    final Document messageDom = XMLParser.parse(data);	            	    
    	    // Get all results
    	    final NodeList releaseNodes = messageDom.getElementsByTagName("release");
    	    final int length = releaseNodes.getLength();
    	    //Window.alert("the length is: "+length);
    	    for(int i = 0; i < length; i++) {
    	    	
    	    	String track = "";
    	    	String artist = "";
    	    	String releaseDate = "";
    	    	
    	    	final Element releaseElement = (Element) messageDom.getElementsByTagName("release").item(i);
    	    	
    	    	// Track name
    	    	final Element titleElement = (Element) releaseElement.getElementsByTagName("title").item(0);
    	    	track = titleElement.getChildNodes().item(0).getNodeValue();
    	    	
    	    	// Artist name
    	    	final Element artistElement = (Element) releaseElement.getElementsByTagName("artist").item(0);
    	    	if(artistElement != null) {
    	    		final Node artistNameNode = artistElement.getChildNodes().item(0);
    	    		//artist = artistNameNode.getNodeName();
    	    		if(artistNameNode.getNodeName().equals("name")) {
    	    	    	artist = artistNameNode.getChildNodes().item(0).getNodeValue();
    	    	    }
    	    		
    	    	}
    	    	
    	    	// Release date
    	    	final Element dateElement = (Element) releaseElement.getElementsByTagName("date").item(0);
    	    	if(dateElement != null) {
    	    		// TODO: Date format inconsistent, need to manage discrepancies
    	    		releaseDate = dateElement.getChildNodes().item(0).getNodeValue();
    	    	}
    	    	
    	    	add(new MusicbrainzResult(track, artist, releaseDate));
    	    }

    	  } catch (final DOMException e) {
    		  // TODO: Handle failed XML parsing 
    	    //Window.alert("Could not parse XML document.");
    	  }
	}

}
