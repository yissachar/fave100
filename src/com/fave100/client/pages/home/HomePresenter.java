package com.fave100.client.pages.home;

import static com.google.gwt.query.client.GQuery.$;

import com.fave100.client.CurrentUser;
import com.fave100.client.pagefragments.popups.login.LoginPopupPresenter;
import com.fave100.client.pagefragments.popups.register.RegisterPopupPresenter;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

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
			final PlaceRequest placeRequest = new PlaceRequest(NameTokens.users)
					.with("u", currentUser.getUsername());
			placeManager.revealPlace(placeRequest);
		}
		getProxy().manualReveal(HomePresenter.this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		$("body").css("overflow", "hidden");
	}

	@Override
	public void onHide() {
		super.onHide();
		$("body").css("overflow", "auto");
	}

	@Override
	public void showRegister() {
		addToPopupSlot(registerPopup);
	}

	@Override
	public void showLogin() {
		addToPopupSlot(loginPopup);
	}

	@Override
	public void explore() {
		final PlaceRequest request = new PlaceRequest(NameTokens.explore);
		placeManager.revealPlace(request);
	}
}

interface HomeUiHandlers extends UiHandlers {
	void showRegister();

	void showLogin();

	void explore();
}