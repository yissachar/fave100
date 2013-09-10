package com.fave100.client.pages.lists.widgets.globallistdetails;

import com.fave100.client.pages.lists.widgets.autocomplete.list.ListAutocompletePresenter;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

public class GlobalListDetailsPresenter extends PresenterWidget<GlobalListDetailsPresenter.MyView> {

	public interface MyView extends View {

		void setInfo(String hashtag);

		void show();

		void hide();

	}

	@ContentSlot public static final Type<RevealContentHandler<?>> LIST_AUTOCOMPLETE_SLOT = new Type<RevealContentHandler<?>>();

	@Inject ListAutocompletePresenter listAutocomplete;

	@Inject
	public GlobalListDetailsPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
	}

	public void setHashtag(final String hashtag) {
		getView().setInfo("#" + hashtag);
	}

	@Override
	public void onReveal() {
		super.onReveal();
		setInSlot(LIST_AUTOCOMPLETE_SLOT, listAutocomplete);
	}
}
