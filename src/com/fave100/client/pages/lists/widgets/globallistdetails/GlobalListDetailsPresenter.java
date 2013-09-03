package com.fave100.client.pages.lists.widgets.globallistdetails;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class GlobalListDetailsPresenter extends PresenterWidget<GlobalListDetailsPresenter.MyView> {

	public interface MyView extends View {

		void setInfo(String hashtag);

		void show();

		void hide();

	}

	@Inject
	public GlobalListDetailsPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
	}

	public void setHashtag(final String hashtag) {
		getView().setInfo("#" + hashtag);
	}
}
