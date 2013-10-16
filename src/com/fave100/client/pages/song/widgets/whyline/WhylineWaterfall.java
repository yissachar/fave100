package com.fave100.client.pages.song.widgets.whyline;

import java.util.List;

import com.fave100.shared.SongInterface;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.WhylineProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;

public class WhylineWaterfall extends Composite {

	private static WhylineWaterUiBinder uiBinder = GWT
			.create(WhylineWaterUiBinder.class);

	interface WhylineWaterUiBinder extends UiBinder<Widget, WhylineWaterfall> {
	}

	@UiField WhylineWaterfallStyle style;
	@UiField VerticalPanel whylines;
	private ApplicationRequestFactory requestFactory;
	private SongInterface _song;
	private boolean _loaded = false;

	interface WhylineWaterfallStyle extends CssResource {
		String noWhyline();
	}

	public WhylineWaterfall(final ApplicationRequestFactory requestFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		this.requestFactory = requestFactory;
	}

	public void setSong(final SongInterface song) {
		whylines.clear();
		_loaded = false;
		_song = song;
	}

	public void show(final boolean show) {
		if (show) {
			setVisible(true);
			if (!_loaded && _song != null) {
				final Request<List<WhylineProxy>> whylineReq = requestFactory.whylineRequest().getWhylinesForSong(_song.getId());
				whylineReq.fire(new Receiver<List<WhylineProxy>>() {
					@Override
					public void onSuccess(final List<WhylineProxy> whylineList) {
						whylines.clear();
						for (final WhylineProxy whyline : whylineList) {
							whylines.add(new WhylineWidget(whyline));
						}
						if (whylineList.size() == 0) {
							final Label label = new Label("No whylines yet");
							label.addStyleName(style.noWhyline());
							whylines.add(label);
						}
						_loaded = true;
					}
				});
			}
		}
		else {
			setVisible(false);
		}
	}

	public void clearWhylines() {
		whylines.clear();
	}
}
