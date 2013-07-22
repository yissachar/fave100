package com.fave100.client.pages.song.widgets.whyline;

import java.util.List;

import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.SongProxy;
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

	interface WhylineWaterfallStyle extends CssResource {
		String noWhyline();
	}

	public WhylineWaterfall(final ApplicationRequestFactory requestFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		this.requestFactory = requestFactory;
	}

	public void setWhylines(final SongProxy song) {
		whylines.clear();
		final Request<List<WhylineProxy>> whylineReq = requestFactory.whylineRequest().getWhylinesForSong(song);
		whylineReq.fire(new Receiver<List<WhylineProxy>>() {
			@Override
			public void onSuccess(final List<WhylineProxy> whylineList) {
				for (final WhylineProxy whyline : whylineList) {
					whylines.add(new WhylineWidget(whyline));
				}
				if (whylineList.size() == 0) {
					final Label label = new Label("No whylines yet");
					label.addStyleName(style.noWhyline());
					whylines.add(label);
				}
			}
		});
	}

	public void clearWhylines() {
		whylines.clear();
	}
}
