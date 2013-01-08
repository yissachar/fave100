package com.fave100.client.widgets;

import java.util.List;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.client.requestfactory.WhylineProxy;
import com.google.gwt.user.client.ui.Grid;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;

public class WhylineWaterfall extends Grid {

	private ApplicationRequestFactory requestFactory;

	@Inject
	public WhylineWaterfall(final ApplicationRequestFactory requestFactory) {
		super(0, 0);
		this.requestFactory = requestFactory;
	}

	public void setWhylines(final SongProxy song) {

		final Request<List<WhylineProxy>> whylineReq = requestFactory.whylineRequest().getWhylinesForSong(song);
		whylineReq.fire(new Receiver<List<WhylineProxy>>() {
			@Override
			public void onSuccess(final List<WhylineProxy> whylineList) {
				clear();
				final int rows = (whylineList.size() / 3) + 1;
				final int columns = (whylineList.size() < 3) ? whylineList.size() : 3;
				resize(rows, columns);
				for(int i = 0; i < rows; i++)
				{
					for(int j = 0; j < columns; j++)
					{
						final int pos = (i == 0 || j == 0) ? i + j : i * j;
						final WhylineProxy whyline = whylineList.get(pos);
						setWidget(i, j, new WhylineWidget(whyline));
					}
				}
			}
		});
	}

}
