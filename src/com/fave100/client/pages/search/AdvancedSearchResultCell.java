package com.fave100.client.pages.search;

import com.fave100.client.pages.myfave100.SuggestionResult;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class AdvancedSearchResultCell extends AbstractCell<SuggestionResult>{

	public AdvancedSearchResultCell() {
	}
	
	@Override
	public void render(final Context context, final SuggestionResult result,
			final SafeHtmlBuilder sb) {
		
			final Image imageThumb = new Image();
			imageThumb.setUrl(result.getArtworkUrl60());
			sb.appendHtmlConstant(imageThumb.toString());
			
			final Label trackName = new Label();
			trackName.setText(result.getTrackName());
			trackName.addStyleName("advancedSearchTrackName");
			sb.appendHtmlConstant(trackName.toString());
			
			final Label releaseDate = new Label();
			releaseDate.setText(result.getReleaseDate().substring(0, 4));
			releaseDate.addStyleName("advancedSearchReleaseDate");
			sb.appendHtmlConstant(releaseDate.toString());		   			
			
			final Label artistName = new Label();
			artistName.setText(result.getArtistName());
			artistName.addStyleName("advancedSearchArtistName");
			sb.appendHtmlConstant(artistName.toString());		
	}	
}
