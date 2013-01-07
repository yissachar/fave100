package com.fave100.client.widgets.favelist;

import com.fave100.client.pagefragments.SideNotification;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListRequest;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.server.domain.favelist.FaveList;
import com.fave100.shared.exceptions.favelist.SongAlreadyInListException;
import com.fave100.shared.exceptions.favelist.SongLimitReachedException;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class NonpersonalFaveListCell extends FaveListCellBase{

	public NonpersonalFaveListCell(final ApplicationRequestFactory requestFactory) {
		super(requestFactory);
	}

	@Override
	public void render(final Context context, final SongProxy object,
			final SafeHtmlBuilder sb) {
		if(object == null) return;

		sb.appendHtmlConstant(getRank(context));
		sb.appendHtmlConstant(getTrackName(object));
		sb.appendHtmlConstant(getArtistName(object));
		//sb.appendHtmlConstant(getReleaseDate(object));

		final String addButton = "<button class='faveListAddButton'>+</button>";
		sb.appendHtmlConstant(addButton);

		//sb.appendHtmlConstant(getImageThumb(object));

		final String whyline = "<span class='faveListWhyline'>"+object.getWhyline()+"</span>";
		sb.appendHtmlConstant(whyline);
	}

	@Override
	public void onBrowserEvent(final Context context, final Element parent, final SongProxy song,
		final NativeEvent event, final ValueUpdater<SongProxy> valueUpdater) {

		if(song == null) return;
		super.onBrowserEvent(context, parent, song, event, valueUpdater);

		if(event.getType().equals("click")) {
			final Element eventTarget = event.getEventTarget().cast();
			if(eventTarget.getClassName().contains("faveListAddButton")) {
				// Favelist add button was clicked
				final FaveListRequest faveListRequest = requestFactory.faveListRequest();
				final Request<Void> addFaveReq = faveListRequest.addFaveItemForCurrentUser(FaveList.DEFAULT_HASHTAG,
						song.getId(), song.getTrackName(), song.getArtistName());

				addFaveReq.fire(new Receiver<Void>() {
					@Override
					public void onSuccess(final Void added) {
						SideNotification.show("Added!");
					}

					@Override
					public void onFailure(final ServerFailure failure) {
						if(failure.getExceptionType().equals(SongLimitReachedException.class.getName())) {
							SideNotification.show("You cannot have more than 100 songs in list");
						} else if (failure.getExceptionType().equals(SongAlreadyInListException.class.getName())) {
							SideNotification.show("The song is already in your list");
						}
					}
				});

			}
		}
	}

}