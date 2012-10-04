package com.fave100.client.pages.search;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

public class AdvancedSearchResultCell extends AbstractCell<MusicbrainzResult>{

	private ApplicationRequestFactory requestFactory;
	
	public AdvancedSearchResultCell(final ApplicationRequestFactory requestFactory) {
		super("click");
		this.requestFactory = requestFactory;
	}
	
	@Override
	public void render(final Context context, final MusicbrainzResult result,
			final SafeHtmlBuilder sb) {
		
			/*final Image imageThumb = new Image();
			imageThumb.setUrl(result.getArtworkUrl60());
			sb.appendHtmlConstant(imageThumb.toString());*/
			
			sb.appendHtmlConstant("<div class='advancedSearchInfoContainer'>");
			
				final Label trackName = new Label();
				trackName.setText(result.getTrackName());
				trackName.addStyleName("advancedSearchTrackName");
				sb.appendHtmlConstant(trackName.toString());
				
				final Label releaseDate = new Label();
				releaseDate.setText(result.getReleaseDate());
				releaseDate.addStyleName("advancedSearchReleaseDate");
				sb.appendHtmlConstant(releaseDate.toString());		   			
				
				final Label artistName = new Label();
				artistName.setText(result.getArtistName());
				artistName.addStyleName("advancedSearchArtistName");
				sb.appendHtmlConstant(artistName.toString());		
				
			sb.appendHtmlConstant("</div>");
			
			final Button addButton = new Button();
			addButton.setText("+");
			addButton.addStyleName("advancedSearchAddButton");
			sb.appendHtmlConstant(addButton.toString());
	}
	
	@Override
	public void onBrowserEvent(final Context context, final Element parent, final MusicbrainzResult song,
		final NativeEvent event, final ValueUpdater<MusicbrainzResult> valueUpdater) {	
		
		if(song == null) return;		
		super.onBrowserEvent(context, parent, song, event, valueUpdater);					
		if(event.getType().equals("click")) {
			final Element eventTarget = event.getEventTarget().cast();
			if(eventTarget.getClassName().contains("advancedSearchAddButton")) {
			// TODO: Add this back in	
			/*	final FaveListRequest faveListRequest = requestFactory.faveListRequest();
				final SongRequest songRequest = faveListRequest.append(requestFactory.songRequest());
				
				final String hashtag = FaveList.DEFAULT_HASHTAG;
				final Long id = Long.valueOf(song.getTrackId());
				
				// Turn the suggestion result into a song proxy
				SongProxy songProxy = songRequest.create(SongProxy.class);
	       		final AutoBean<SuggestionResult> autoBean = AutoBeanUtils.getAutoBean(song);
				final AutoBean<SongProxy> newBean = AutoBeanUtils.getAutoBean(songProxy);
				AutoBeanCodex.decodeInto(AutoBeanCodex.encode(autoBean), newBean);				
				songProxy = newBean.as();				
						
				final Request<Void> addReq = faveListRequest.addFaveItemForCurrentUser(hashtag, id, songProxy);
				addReq.fire(new Receiver<Void>() {
					@Override
					public void onSuccess(final Void response) {
						SideNotification.show("Added!");
					}		
					@Override
					public void onFailure(final ServerFailure failure) {
						SideNotification.show(failure.getMessage().replace("Server Error:", ""), true);
					}					
				});*/
			}
		}
	}
	
	
}
