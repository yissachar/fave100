package com.fave100.client.pagefragments.topbar;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.events.LoginDialogRequestedEvent;
import com.fave100.client.events.favelist.HideSideBarEvent;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.pagefragments.playlist.PlaylistPresenter;
import com.fave100.client.pages.lists.UnifiedSearchSuggestionSelectedAction;
import com.fave100.client.pages.register.RegisterPresenter;
import com.fave100.client.widgets.search.SearchPresenter;
import com.fave100.shared.place.NameTokens;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

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

		void setMobileView(String currentPlace);

		void setFullSearch(boolean full);
	}

	@ContentSlot public static final Type<RevealContentHandler<?>> SEARCH_SLOT = new Type<RevealContentHandler<?>>();

	private EventBus _eventBus;
	private CurrentUser _currentUser;
	private PlaceManager _placeManager;
	private PlaylistPresenter _playlistPresenter;
	private FaveApi _api;
	@Inject private SearchPresenter _unifiedSearch;

	@Inject
	public TopBarPresenter(final EventBus eventBus, final MyView view, final PlaceManager placeManager, final CurrentUser currentUser, final FaveApi api,
							PlaylistPresenter playlistPresenter) {
		super(eventBus, view);
		_eventBus = eventBus;
		_currentUser = currentUser;
		_placeManager = placeManager;
		_playlistPresenter = playlistPresenter;
		_api = api;

		getView().setUiHandlers(this);

		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				checkMobileView();
			}
		});

	}

	@Override
	protected void onBind() {
		super.onBind();
		_unifiedSearch.setSuggestionSelectedAction(new UnifiedSearchSuggestionSelectedAction(_placeManager, _playlistPresenter));
		registerCallbacks();

		CurrentUserChangedEvent.register(_eventBus,
				new CurrentUserChangedEvent.Handler() {
					@Override
					public void onCurrentUserChanged(
							final CurrentUserChangedEvent event) {
						setTopBar();
					}
				});

		// Every 30 minutes check to see if user session expired and refresh UI
		final Timer timer = new Timer() {
			@Override
			public void run() {
				if (!_currentUser.isLoggedIn())
					return;

				_api.call(_api.service().user().getLoggedInUser(), new AsyncCallback<AppUser>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(AppUser user) {
						if (user == null)
							_eventBus.fireEvent(new CurrentUserChangedEvent(null));
					}
				});
			}
		};
		timer.scheduleRepeating(30 * 60 * 1000);

		addRegisteredHandler(NavigationEvent.getType(), new NavigationHandler() {

			@Override
			public void onNavigation(NavigationEvent navigationEvent) {
				checkMobileView();
				getView().setFullSearch(false);
			}
		});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		setTopBar();
		checkMobileView();
		setInSlot(SEARCH_SLOT, _unifiedSearch);
	}

	private void checkMobileView() {
		String currentPlace = _placeManager.getCurrentPlaceRequest().getNameToken();
		getView().setMobileView(currentPlace);
	}

	private void setTopBar() {
		if (_currentUser != null && _currentUser.isLoggedIn()) {
			getView().setLoggedIn(_currentUser.getUsername());
		}
		else {
			getView().setLoggedOut();
		}
	}

	@Override
	public void logout() {
		_currentUser.logout();
	}

	@Override
	public void fireHideSideBarEvent() {
		_eventBus.fireEvent(new HideSideBarEvent());
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
		_placeManager.revealPlace(new PlaceRequest.Builder()
				.nameToken(NameTokens.register)
				.with(RegisterPresenter.PROVIDER_PARAM, RegisterPresenter.PROVIDER_GOOGLE)
				.build());
	}

	public void twitterCallback(final String verifier) {
		_placeManager.revealPlace(new PlaceRequest.Builder()
				.nameToken(NameTokens.register)
				.with(RegisterPresenter.PROVIDER_PARAM, RegisterPresenter.PROVIDER_TWITTER)
				.with(RegisterPresenter.OAUTH_VERIFIER_PARAM, verifier)
				.build());
	}

	public void facebookCallback(final String code) {
		_placeManager.revealPlace(new PlaceRequest.Builder()
				.nameToken(NameTokens.register)
				.with(RegisterPresenter.PROVIDER_PARAM, RegisterPresenter.PROVIDER_FACEBOOK)
				.with(RegisterPresenter.CODE_PARAM, code)
				.build());
	}

	@Override
	public void showLoginDialog() {
		_eventBus.fireEvent(new LoginDialogRequestedEvent());
	}

	@Override
	public void focusSearch() {
		_unifiedSearch.focus();
	}
}

interface TopBarUiHandlers extends UiHandlers {

	void showLoginDialog();

	void logout();

	void fireHideSideBarEvent();

	void focusSearch();
}
