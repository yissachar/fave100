package com.fave100.client.pagefragments.topbar;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.pagefragments.popups.login.LoginPopupPresenter;
import com.fave100.client.pagefragments.popups.register.RegisterPopupPresenter;
import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Utils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;

/**
 * Top navigation bar that will be included on every page.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class TopBarPresenter extends PresenterWidget<TopBarPresenter.MyView>
		implements TopBarUiHandlers {

	public interface MyView extends View, HasUiHandlers<TopBarUiHandlers> {
		void setLoggedIn(String username);

		void setLoggedOut();

		void setTopBarDropShadow(boolean show);
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> LOGIN_SLOT = new Type<RevealContentHandler<?>>();

	@Inject private LoginPopupPresenter loginBox;
	@Inject private RegisterPopupPresenter registerBox;
	private EventBus eventBus;
	private CurrentUser currentUser;
	private PlaceManager placeManager;

	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view, final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view);
		this.eventBus = eventBus;
		this.currentUser = currentUser;
		this.placeManager = placeManager;
		getView().setUiHandlers(this);

		Window.addWindowScrollHandler(new ScrollHandler() {
			@Override
			public void onWindowScroll(final ScrollEvent event) {
				// Window as at top of screen or on users page, no need for drop shadow
				if (event.getScrollTop() == 0
						|| placeManager.getCurrentPlaceRequest().getNameToken().equals(NameTokens.users)) {
					getView().setTopBarDropShadow(false);
				}
				else {
					// Top bar is scrolling, show drop shadow to indicate perspective
					getView().setTopBarDropShadow(true);
				}
			}
		});
	}

	@Override
	protected void onBind() {
		super.onBind();
		registerCallbacks();

		CurrentUserChangedEvent.register(eventBus,
				new CurrentUserChangedEvent.Handler() {
					@Override
					public void onCurrentUserChanged(
							final CurrentUserChangedEvent event) {
						setTopBar();
					}
				});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setTopBar();
	}

	private void setTopBar() {
		if (currentUser != null && currentUser.isLoggedIn()) {
			getView().setLoggedIn(currentUser.getUsername());
		}
		else {
			getView().setLoggedOut();
		}
	}

	@Override
	public void showLoginBox() {
		if (Utils.isTouchDevice())
			placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
		else
			addToPopupSlot(loginBox);
	}

	@Override
	public void showRegisterBox() {
		if (Utils.isTouchDevice())
			placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.register).build());
		else
			addToPopupSlot(registerBox);
	}

	public native void registerCallbacks()/*-{
		$wnd.googleCallback = (function(obj) {
			return function() {
				$entry(obj.@com.fave100.client.pagefragments.topbar.TopBarPresenter::googleCallback()).call(obj);
			};
		})(this);
		$wnd.twitterCallback = (function(obj) {
			return function(verifier) {
				$entry(obj.@com.fave100.client.pagefragments.topbar.TopBarPresenter::twitterCallback(Ljava/lang/String;)).call(obj, verifier);
			};
		})(this);
		$wnd.facebookCallback = (function(obj) {
			return function(code) {
				$entry(obj.@com.fave100.client.pagefragments.topbar.TopBarPresenter::facebookCallback(Ljava/lang/String;)).call(obj, code);
			};
		})(this);
	}-*/;

	public void googleCallback() {
		placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.register).with("provider", RegisterPresenter.PROVIDER_GOOGLE).build());
	}

	public void twitterCallback(final String verifier) {
		GWT.log("Oauth from top bar: " + verifier);
		placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.register).with("provider", RegisterPresenter.PROVIDER_TWITTER).with("oauth_verifier", verifier).build());
	}

	public void facebookCallback(final String code) {
		placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.register).with("provider", RegisterPresenter.PROVIDER_FACEBOOK).with("code", code).build());
	}
}

interface TopBarUiHandlers extends UiHandlers {
	void showLoginBox();

	void showRegisterBox();
}
