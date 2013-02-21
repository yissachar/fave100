package com.fave100.client.pages.logout;

import com.fave100.client.Notification;
import com.fave100.client.events.CurrentUserChangedEvent;
import com.fave100.client.gatekeepers.LoggedInGatekeeper;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.requestfactory.AppUserRequest;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

/**
 * A page to logout the current user.
 * @author yissachar.radcliffe
 *
 */
public class LogoutPresenter extends
		Presenter<LogoutPresenter.MyView, LogoutPresenter.MyProxy> {

	public interface MyView extends View {
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.logout)
	@UseGatekeeper(LoggedInGatekeeper.class)
	public interface MyProxy extends ProxyPlace<LogoutPresenter> {
	}

	private EventBus					eventBus;
	private ApplicationRequestFactory	requestFactory;
	private PlaceManager				placeManager;

	@Inject
	public LogoutPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final PlaceManager placeManager,
			final ApplicationRequestFactory requestFactory) {
		super(eventBus, view, proxy);

		this.eventBus = eventBus;
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	protected void onReveal() {
		super.onReveal();

		// Whenever this page is visited, log out the current user
		final AppUserRequest appUserRequest = requestFactory.appUserRequest();
		final Request<Void> logoutReq = appUserRequest.logout();
		logoutReq.fire(new Receiver<Void>() {
			@Override
			public void onSuccess(final Void response) {
				eventBus.fireEvent(new CurrentUserChangedEvent(null));
				Notification.show("Logged out successfully");
				placeManager.revealPlace(new PlaceRequest(NameTokens.home));
			}
		});
	}
}
