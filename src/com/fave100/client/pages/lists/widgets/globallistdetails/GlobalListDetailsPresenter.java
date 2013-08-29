package com.fave100.client.pages.lists.widgets.globallistdetails;

import java.util.List;

import com.fave100.shared.requestfactory.HashtagProxy;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class GlobalListDetailsPresenter extends PresenterWidget<GlobalListDetailsPresenter.MyView> {

	public interface MyView extends View {

		void setInfo(String hashtag, List<String> avatars, List<String> users, int listCount);

		void show();

		void hide();

	}

	@Inject
	public GlobalListDetailsPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
	}

	public void setHashtag(final HashtagProxy hashtag) {
		getView().setInfo("#" + hashtag.getName(), hashtag.getSampledUsersAvatars(), hashtag.getSampledUsersNames(), hashtag.getListCount());
	}
}
