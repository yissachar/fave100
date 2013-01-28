package com.fave100.client.pages.home;

import java.util.List;

import com.fave100.client.CurrentUser;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.AppUserProxy;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
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
		BasePresenter<HomePresenter.MyView, HomePresenter.MyProxy> {

	public interface MyView extends BaseView {
		void addUserThumb(AppUserProxy appUser);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.home)
	public interface MyProxy extends ProxyPlace<HomePresenter> {
	}

	private ApplicationRequestFactory	requestFactory;
	private PlaceManager				placeManager;
	private CurrentUser					currentUser;

	@Inject
	public HomePresenter(final ApplicationRequestFactory requestFactory,
			final EventBus eventBus, final MyView view, final MyProxy proxy,
			final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		this.currentUser = currentUser;
	}

	@Override
	protected void onBind() {
		super.onBind();

		// Get a list of 4 random users
		final Request<List<AppUserProxy>> randomUsers = requestFactory
				.appUserRequest().getRandomUsers(4);
		randomUsers.fire(new Receiver<List<AppUserProxy>>() {
			@Override
			public void onSuccess(final List<AppUserProxy> userList) {
				for (final AppUserProxy appUser : userList) {
					// Create thumbs to display songs from the user's lists
					getView().addUserThumb(appUser);
				}
			}
		});
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
					.with("u", currentUser.getAppUser().getUsername());
			placeManager.revealPlace(placeRequest);
		}
		getProxy().manualReveal(HomePresenter.this);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
	}
}