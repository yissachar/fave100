package com.fave100.client.pages.search;

import java.util.ArrayList;

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
    	    Window.alert("the length is: "+length);
    	    for(int i = 0; i < length; i++) {
    	    	final Node titleNode = messageDom.getElementsByTagName("title").item(i);
    	    	final String track = titleNode.getChildNodes().item(0).getNodeValue();
    	    	final String artist = "foo";
    	    	final String releaseDate = "bar";
    	    	add(new MusicbrainzResult(track, artist, releaseDate));
    	    	//Window.alert(titleNode.getChildNodes().item(i).getNodeValue());
    	    }

    	  } catch (final DOMException e) {
    	    Window.alert("Could not parse XML document.");
    	  }
	}

}
