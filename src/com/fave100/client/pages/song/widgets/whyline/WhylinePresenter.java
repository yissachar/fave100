package com.fave100.client.pages.song.widgets.whyline;

import java.util.ArrayList;
import java.util.List;

import com.fave100.shared.SongInterface;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.UserListResultProxy;
import com.fave100.shared.requestfactory.WhylineProxy;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class WhylinePresenter extends PresenterWidget<WhylinePresenter.MyView> implements WhylineUiHandlers {
	public interface MyView extends View, HasUiHandlers<WhylineUiHandlers> {
		void setWhylines(List<WhylineProxy> whylines);

		void setUserLists(List<UserListResultProxy> userLists);
	}

	private ApplicationRequestFactory _requestFactory;

	@Inject
	WhylinePresenter(EventBus eventBus, MyView view, ApplicationRequestFactory requestFactory) {
		super(eventBus, view);
		_requestFactory = requestFactory;

		getView().setUiHandlers(this);
	}

	@Override
	public void onHide() {
		super.onHide();
		getView().setWhylines(new ArrayList<WhylineProxy>());
		getView().setUserLists(new ArrayList<UserListResultProxy>());
	}

	public void showWhylines(SongInterface song) {
		final Request<List<WhylineProxy>> whylineReq = _requestFactory.whylineRequest().getWhylinesForSong(song.getId());
		whylineReq.fire(new Receiver<List<WhylineProxy>>() {
			@Override
			public void onSuccess(final List<WhylineProxy> whylines) {
				getView().setWhylines(whylines);
				/*if (whylineList.size() == 0) {
					final Label label = new Label("No whylines yet");
					label.addStyleName(style.noWhyline());
					whylines.add(label);
				}*/
			}
		});

		final Request<List<UserListResultProxy>> userListReq = _requestFactory.faveListRequest().getListsContainingSong(song.getId());
		userListReq.fire(new Receiver<List<UserListResultProxy>>() {
			@Override
			public void onSuccess(final List<UserListResultProxy> userLists) {
				getView().setUserLists(userLists);
				/*if (whylineList.size() == 0) {
					final Label label = new Label("No whylines yet");
					label.addStyleName(style.noWhyline());
					whylines.add(label);
				}*/
			}
		});
	}
}
