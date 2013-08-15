package com.fave100.client.pages.users.widgets.listmanager;

import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;

public class ListManagerPresenter extends
		PresenterWidget<ListManagerPresenter.MyView>
		implements ListManagerUiHandlers {

	public interface MyView extends View, HasUiHandlers<ListManagerUiHandlers> {
	}

	private ApplicationRequestFactory _requestFactory;

	@Inject
	public ListManagerPresenter(final EventBus eventBus, final MyView view, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view);
		view.setUiHandlers(ListManagerPresenter.this);
		_requestFactory = requestFactory;
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	public void addHashtag(final String name) {
		final Request<Void> addFavelistReq = _requestFactory.faveListRequest().addFaveListForCurrentUser(name);
		addFavelistReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				// TODO: Update list
			}
		});
	}
}

interface ListManagerUiHandlers extends UiHandlers {

	void addHashtag(String name);
}