package com.fave100.client.pages.lists.widgets.globallistdetails;

import java.util.List;

import com.fave100.client.pages.lists.widgets.autocomplete.list.ListAutocompletePresenter;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class GlobalListDetailsPresenter extends PresenterWidget<GlobalListDetailsPresenter.MyView> {

	public interface MyView extends View {

		void setInfo(String hashtag);

		void setTrendingLists(List<String> lists);
		
		void show();

		void hide();		

	}

	@ContentSlot public static final Type<RevealContentHandler<?>> LIST_AUTOCOMPLETE_SLOT = new Type<RevealContentHandler<?>>();

	private ApplicationRequestFactory _requestFactory;
	@Inject ListAutocompletePresenter listAutocomplete;

	@Inject
	public GlobalListDetailsPresenter(final EventBus eventBus, final MyView view, ApplicationRequestFactory requestFactory) {
		super(eventBus, view);
		_requestFactory = requestFactory;
	}

	public void setHashtag(final String hashtag) {
		getView().setInfo(hashtag);
		final Request<List<String>> getTrendingReq = _requestFactory.faveListRequest().getTrendingFaveLists();
		getTrendingReq.fire(new Receiver<List<String>>() {
			@Override
			public void onSuccess(List<String> lists) {
				getView().setTrendingLists(lists);
			}
		});
	}

	@Override
	public void onReveal() {
		super.onReveal();
		setInSlot(LIST_AUTOCOMPLETE_SLOT, listAutocomplete);
	}
}
