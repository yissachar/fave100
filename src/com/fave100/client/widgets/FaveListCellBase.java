package com.fave100.client.widgets;

import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;

public abstract class FaveListCellBase extends AbstractCell<SongProxy>{
	
	protected ApplicationRequestFactory requestFactory;

	public FaveListCellBase(final ApplicationRequestFactory requestFactory) {
		super("click", "mousedown");
		this.requestFactory = requestFactory;
	}

	@Override
	public void render(final Context context, final SongProxy object,
			final SafeHtmlBuilder sb) {
		if(object == null) return;
		
		sb.appendHtmlConstant(getRank(context));
		sb.appendHtmlConstant(getTrackName(object));
		sb.appendHtmlConstant(getArtistName(object));
		sb.appendHtmlConstant(getReleaseDate(object));
		sb.appendHtmlConstant(getImageThumb(object));
	}

	
	
	protected String getRank(final Context context) {
		final String rank = "<span class='faveListRank'>"+(context.getIndex()+1)+".</span>";
		return rank;
	}
	
	protected String getTrackName(final SongProxy object) {
		String songUrl = Window.Location.getPath();
		songUrl += Window.Location.getQueryString()+"#"+NameTokens.song+";song=";		
		songUrl += URL.encodeQueryString(object.getTrackName());
		songUrl += ";artist="+URL.encodeQueryString(object.getArtistName());
		
		String trackName = "<a href='"+songUrl.replace("'", "%27")+"'";
		trackName += "class='faveListTrackName'>"+object.getTrackName()+"</a>";
		return(trackName);
	}
	
	protected String getArtistName(final SongProxy object) {
		final String artistName = "<span class='faveListArtistName'>"+object.getArtistName()+"</span>";
		return(artistName);
	}
	
	protected String getReleaseDate(final SongProxy object) {
		String releaseDate = "<span class='faveListReleaseDate'>";
		final String dateVal = object.getReleaseDate();
		if(dateVal != null && !dateVal.isEmpty()) {
			releaseDate += object.getReleaseDate().substring(0, 4);
		}
	    releaseDate += "</span>";
	    return releaseDate;
	}
	
	protected String getImageThumb(final SongProxy object) {
		String imageThumb = "<img class='faveListImageThumb'";
		imageThumb += "src='"+object.getCoverArtUrl()+"'/>";
		return imageThumb;
	}
}

