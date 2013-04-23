package com.fave100.client.pagefragments.topbar;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.client.place.NameTokens;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
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

		void hideLightbox();
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> LOGIN_SLOT = new Type<RevealContentHandler<?>>();

	@Inject private LoginWidgetPresenter loginBox;
	private EventBus eventBus;
	private PlaceManager placeManager;
	private CurrentUser currentUser;

	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view, final PlaceManager placeManager,
							final CurrentUser currentUser) {
		super(eventBus, view);
		this.eventBus = eventBus;
		this.placeManager = placeManager;
		this.currentUser = currentUser;
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

		setInSlot(LOGIN_SLOT, loginBox);
		setTopBar();

		if (placeManager.getCurrentPlaceRequest().getNameToken().equals(NameTokens.users)) {

		}
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().hideLightbox();
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
	public void setLoginBoxFocus() {
		loginBox.setFocus();
	}

	@Override
	public void clearLoginBox() {
		loginBox.clearLoginDetails();

	}
}

interface TopBarUiHandlers extends UiHandlers {
	void setLoginBoxFocus();

	void clearLoginBox();
}
