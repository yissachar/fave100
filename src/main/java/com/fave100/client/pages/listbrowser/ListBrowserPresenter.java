package com.fave100.client.pages.listbrowser;

import java.util.List;

import com.fave100.client.FaveApi;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.entities.StringResultCollection;
import com.fave100.client.pages.PagePresenter;
import com.fave100.shared.place.NameTokens;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class ListBrowserPresenter extends PagePresenter<ListBrowserPresenter.MyView, ListBrowserPresenter.MyProxy> implements ListBrowserUiHandlers {

	public interface MyView extends View, HasUiHandlers<ListBrowserUiHandlers> {

		void setLists(List<StringResult> lists);

		void clear();

		void setError(String error);
	}

	@NameToken(NameTokens.alllists)
	@ProxyCodeSplit
	public interface MyProxy extends ProxyPlace<ListBrowserPresenter> {
	}

	private FaveApi _api;

	@Inject
	public ListBrowserPresenter(EventBus eventBus, MyView view, MyProxy proxy, final FaveApi api) {
		super(eventBus, view, proxy);
		_api = api;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();

		_api.call(_api.service().favelists().getListNames(), new AsyncCallback<StringResultCollection>() {

			@Override
			public void onFailure(Throwable caught) {
				getView().setError("An error occurred");
			}

			@Override
			public void onSuccess(StringResultCollection result) {
				getView().setLists(result.getItems());
			}
		});
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().clear();
	}

}
