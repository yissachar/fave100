package com.fave100.client.pagefragments.topbar;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
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

	@Inject private LoginWidgetPresenter loginBox;
	private EventBus eventBus;
	private CurrentUser currentUser;

	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view,
							final CurrentUser currentUser) {
		super(eventBus, view);
		this.eventBus = eventBus;
		this.currentUser = currentUser;
		getView().setUiHandlers(this);

		Window.addWindowScrollHandler(new ScrollHandler() {
			@Override
			public void onWindowScroll(final ScrollEvent event) {
				if (event.getScrollTop() == 0) {
					// Window as at top of screen, no need for drop shadow
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
	}

	private void setTopBar() {
		if (currentUser != null && currentUser.isLoggedIn()) {
			getView().setLoggedIn(currentUser.getUsername());
		}
		else {
			getView().setLoggedOut();
		}
	}
}

interface TopBarUiHandlers extends UiHandlers {

}
