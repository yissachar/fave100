package com.fave100.client.pagefragments.favelist.widgets;

import com.fave100.client.pages.song.SongPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.UrlBuilder;
import com.fave100.shared.requestfactory.FaveItemProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class FavePickWidget extends Composite {

	private static Binder uiBinder = GWT.create(Binder.class);

	public interface Binder extends UiBinder<Widget, FavePickWidget> {
	}

	@UiField SimplePanel rankPanel;
	@UiField Anchor song;
	@UiField InlineLabel artist;
	@UiField SimplePanel whyLinePanel;

	private final FaveItemProxy _item;
	private final int _rank;
	//TODO
	private final boolean _editable;

	public FavePickWidget(final FaveItemProxy item, final int rank, final boolean editable) {
		_item = item;
		_rank = rank;
		_editable = editable;

		initWidget(uiBinder.createAndBindUi(this));

		fillWidget();
	}

	private void fillWidget() {
		//TODO: do editable
		final InlineLabel songPick = new InlineLabel(Integer.toString(_rank));
		rankPanel.setWidget(songPick);

		song.setText(_item.getSong());
		song.setHref("#" + new UrlBuilder(NameTokens.song).with(SongPresenter.ID_PARAM, _item.getSongID()).getPlaceToken());

		artist.setText(_item.getArtist());

		//TODO: editable
		whyLinePanel.setWidget(new Label(_item.getWhyline()));

	}
}
