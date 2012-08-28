package com.fave100.client.widgets;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

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
		String trackName = "<a href='"+object.getTrackViewUrl()+"'";
		trackName += "class='faveListTrackName'>"+object.getTrackName()+"</a>";
		return(trackName);
	}
	
	protected String getArtistName(final SongProxy object) {
		final String artistName = "<span class='faveListArtistName'>"+object.getArtistName()+"</span>";
		return(artistName);
	}
	
	protected String getReleaseDate(final SongProxy object) {
		String releaseDate = "<span class='faveListReleaseDate'>";
	    releaseDate += object.getReleaseDate().substring(0, 4)+"</span>";
	    return releaseDate;
	}
	
	protected String getImageThumb(final SongProxy object) {
		String imageThumb = "<img class='faveListImageThumb'";
		imageThumb += "src='"+object.getArtworkUrl60()+"'/>";
		return imageThumb;
	}
}

