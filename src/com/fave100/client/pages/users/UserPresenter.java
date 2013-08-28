package com.fave100.client.pages.users;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class UserPresenter extends
		BasePresenter<UserPresenter.MyView, UserPresenter.MyProxy>
		implements UsersUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<UsersUiHandlers> {
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.users)
	public interface MyProxy extends ProxyPlace<UserPresenter> {
	}

	private final PlaceManager _placeManager;

	@Inject
	public UserPresenter(final EventBus eventBus, final MyView view,
							final MyProxy proxy, final ApplicationRequestFactory requestFactory,
							final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this._placeManager = placeManager;
		getView().setUiHandlers(this);
	}

	@Override
	public boolean useManualReveal() {
		return true;
	}

	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);

		// Redirect to new URL
		getProxy().manualReveal(UserPresenter.this);
		_placeManager.revealPlace(new PlaceRequest.Builder()
				.nameToken(NameTokens.lists)
				.with(ListPresenter.USER_PARAM, placeRequest.getParameter(ListPresenter.USER_PARAM, ""))
				.with(ListPresenter.LIST_PARAM, placeRequest.getParameter(ListPresenter.LIST_PARAM, Constants.DEFAULT_HASHTAG))
				.build());
	}
}

interface UsersUiHandlers extends UiHandlers {
}
