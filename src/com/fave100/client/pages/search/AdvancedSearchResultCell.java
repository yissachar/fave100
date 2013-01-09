package com.fave100.client.pages.search;

import com.fave100.client.pagefragments.topbar.Notification;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListRequest;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class AdvancedSearchResultCell extends AbstractCell<SongProxy>{

	private ApplicationRequestFactory requestFactory;

	public AdvancedSearchResultCell(final ApplicationRequestFactory requestFactory) {
		super("click");
		this.requestFactory = requestFactory;
	}

	@Override
	public void render(final Context context, final SongProxy result,
			final SafeHtmlBuilder sb) {

			/*final Image imageThumb = new Image();
			imageThumb.setUrl(result.getArtworkUrl60());
			sb.appendHtmlConstant(imageThumb.toString());*/

			sb.appendHtmlConstant("<div class='advancedSearchInfoContainer'>");

				final Label trackName = new Label();
				trackName.setText(result.getTrackName());
				trackName.addStyleName("advancedSearchTrackName");
				sb.appendHtmlConstant(trackName.toString());

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
	public void onBrowserEvent(final Context context, final Element parent, final SongProxy song,
		final NativeEvent event, final ValueUpdater<SongProxy> valueUpdater) {

		if(song == null) return;
		super.onBrowserEvent(context, parent, song, event, valueUpdater);
		if(event.getType().equals("click")) {
			final Element eventTarget = event.getEventTarget().cast();
			if(eventTarget.getClassName().contains("advancedSearchAddButton")) {

				final FaveListRequest faveListRequest = requestFactory.faveListRequest();

				final String hashtag = FaveList.DEFAULT_HASHTAG;
				final String id = song.getMbid();

				final Request<Void> addReq = faveListRequest.addFaveItemForCurrentUser(hashtag, id,
					song.getTrackName(), song.getArtistName());

				addReq.fire(new Receiver<Void>() {

					@Override
					public void onSuccess(final Void response) {
						Notification.show("Added");
					}

					@Override
					public void onFailure(final ServerFailure failure) {
						if(failure.getExceptionType().equals(SongLimitReachedException.class.getName())) {
							Notification.show("You cannot have more than 100 songs in list");
						} else if (failure.getExceptionType().equals(SongAlreadyInListException.class.getName())) {
							Notification.show("The song is already in your list");
						}
					}

				});
			}
		}
	}


}
