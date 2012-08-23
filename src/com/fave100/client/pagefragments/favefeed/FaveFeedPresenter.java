package com.fave100.client.pagefragments.favefeed;

import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.google.inject.Inject;
import com.google.gwt.event.shared.EventBus;

public class FaveFeedPresenter extends
		PresenterWidget<FaveFeedPresenter.MyView> {

	public interface MyView extends View {
	}

	@Inject
	public FaveFeedPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
	}
}
