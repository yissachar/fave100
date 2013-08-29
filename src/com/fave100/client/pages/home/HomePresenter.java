package com.fave100.client.pages.home;

import com.fave100.client.CurrentUser;
import com.fave100.client.pagefragments.popups.login.LoginPopupPresenter;
import com.fave100.client.pagefragments.popups.register.RegisterPopupPresenter;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.pages.lists.ListPresenter;
import com.fave100.client.pages.lists.widgets.favelist.FavelistPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Constants;
import com.fave100.shared.Utils;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * Default page that the user will see.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class HomePresenter extends
		BasePresenter<HomePresenter.MyView, HomePresenter.MyProxy>
		implements HomeUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<HomeUiHandlers> {
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.home)
	public interface MyProxy extends ProxyPlace<HomePresenter> {
	}

	private PlaceManager placeManager;
	private CurrentUser currentUser;
	@Inject RegisterPopupPresenter registerPopup;
	@Inject LoginPopupPresenter loginPopup;
	@Inject FavelistPresenter masterList;

	@ContentSlot public static final Type<RevealContentHandler<?>> MASTER_LIST_SLOT = new Type<RevealContentHandler<?>>();

	@Inject
	public HomePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view, proxy);
		this.placeManager = placeManager;
		this.currentUser = currentUser;
		getView().setUiHandlers(HomePresenter.this);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	public boolean useManualReveal() {
		return true;
	}

	@Override
	public void prepareFromRequest(final PlaceRequest request) {
		super.prepareFromRequest(request);

		if (currentUser.isLoggedIn()) {
			final PlaceRequest placeRequest = new PlaceRequest.Builder().nameToken(NameTokens.lists).with(ListPresenter.USER_PARAM, currentUser.getUsername()).build();
			placeManager.revealPlace(placeRequest);
		}
		getProxy().manualReveal(HomePresenter.this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setInSlot(MASTER_LIST_SLOT, masterList);
		masterList.setHashtag(Constants.DEFAULT_HASHTAG);
		masterList.refreshFavelist(false);
	}

	@Override
	public void onHide() {
		super.onHide();
	}

	@Override
	public void showRegister() {
		if (Utils.isTouchDevice())
			placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.register).build());
		else
			addToPopupSlot(registerPopup);
	}

	@Override
	public void showLogin() {
		if (Utils.isTouchDevice())
			placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
		else
			addToPopupSlot(loginPopup);
	}
}

interface HomeUiHandlers extends UiHandlers {
	void showRegister();

	void showLogin();
}