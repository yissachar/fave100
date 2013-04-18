package com.fave100.client.widgets;

import java.util.List;

import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.SongProxy;
import com.fave100.shared.requestfactory.WhylineProxy;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;

public class WhylineWaterfall extends Composite {

	private static WhylineWaterUiBinder uiBinder = GWT
			.create(WhylineWaterUiBinder.class);

	interface WhylineWaterUiBinder extends UiBinder<Widget, WhylineWaterfall> {
	}

	@UiField Label header;
	@UiField Grid grid;
	private ApplicationRequestFactory requestFactory;

	public WhylineWaterfall(final ApplicationRequestFactory requestFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		this.requestFactory = requestFactory;
	}

	public void setWhylines(final SongProxy song) {

		final Request<List<WhylineProxy>> whylineReq = requestFactory.whylineRequest().getWhylinesForSong(song);
		whylineReq.fire(new Receiver<List<WhylineProxy>>() {
			@Override
			public void onSuccess(final List<WhylineProxy> whylineList) {
				grid.clear();
				header.setVisible(false);
				if (whylineList.size() > 0)
					header.setVisible(true);
				final int rows = (whylineList.size() / 3) + 1;
				final int columns = (whylineList.size() < 3) ? whylineList.size() : 3;
				grid.resize(rows, columns);
				for (int i = 0; i < rows; i++)
				{
					for (int j = 0; j < columns; j++)
					{
						final int pos = (i == 0 || j == 0) ? i + j : i * j;
						final WhylineProxy whyline = whylineList.get(pos);
						grid.setWidget(i, j, new WhylineWidget(whyline));
					}
				}
			}
		});
	}

}
