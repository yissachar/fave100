package com.fave100.client.pagefragments.topbar;

import com.fave100.client.CurrentUser;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.pagefragments.login.LoginWidgetPresenter;
import com.fave100.shared.requestfactory.AppUserProxy;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
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
	}

	@ContentSlot
	public static final Type<RevealContentHandler<?>>	LOGIN_SLOT	= new Type<RevealContentHandler<?>>();

	@Inject
	private LoginWidgetPresenter						loginBox;
	private EventBus									eventBus;
	private	ApplicationRequestFactory					requestFactory;
	private CurrentUser									currentUser;

	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view,
			final ApplicationRequestFactory requestFactory,
			final CurrentUser currentUser) {
		super(eventBus, view);
		this.eventBus = eventBus;
		this.requestFactory = requestFactory;
		this.currentUser = currentUser;
		getView().setUiHandlers(this);
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

		// On first page load or page refresh, check for an existing logged in user
		final Request<AppUserProxy> request = requestFactory.appUserRequest().getLoggedInAppUser();
		request.fire(new Receiver<AppUserProxy>() {
			@Override
			public void onSuccess(final AppUserProxy appUser) {
				eventBus.fireEvent(new CurrentUserChangedEvent(appUser));
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
		} else {
			getView().setLoggedOut();
		}
	}
}

interface TopBarUiHandlers extends UiHandlers {

}
