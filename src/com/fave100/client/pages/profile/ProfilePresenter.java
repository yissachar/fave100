package com.fave100.client.pages.profile;

import com.fave100.client.CurrentUser;
import com.fave100.client.LoadingIndicator;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.gatekeepers.LoggedInGatekeeper;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Validator;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.fave100.shared.requestfactory.AppUserRequest;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.fave100.shared.requestfactory.UserInfoProxy;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

/**
 * Shows the logged in user their profile
 * 
 * @author yissachar.radcliffe
 * 
 */
public class ProfilePresenter extends
		BasePresenter<ProfilePresenter.MyView, ProfilePresenter.MyProxy>
		implements ProfileUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<ProfileUiHandlers> {
		void createActionUrl(String url);

		void setEmailValue(String val);

		void setFollowingPrivate(boolean checked);

		void setEmailError(String error);

		void clearErrors();

		void setFormStatusMessage(String message);

		void setAvatarImg(String src);

		void clearAvatarForm();

		void clearEmail();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.profile)
	@UseGatekeeper(LoggedInGatekeeper.class)
	public interface MyProxy extends ProxyPlace<ProfilePresenter> {
	}

	private EventBus _eventBus;
	private ApplicationRequestFactory _requestFactory;
	private CurrentUser _currentUser;
	private PlaceManager _placeManager;
	private UserInfoProxy oldUserInfo = null;

	@Inject
	public ProfilePresenter(final EventBus eventBus, final MyView view,
							final MyProxy proxy,
							final ApplicationRequestFactory requestFactory,
							final CurrentUser currentUser,
							final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		_eventBus = eventBus;
		_requestFactory = requestFactory;
		_currentUser = currentUser;
		_placeManager = placeManager;
		getView().setUiHandlers(this);
	}

	// TODO: Should have a button to remove current avatar if they have uploaded a
	// native avatar
	@Override
	public void onBind() {
		super.onBind();
	}

	@Override
	public void onReveal() {
		super.onReveal();
		setEmail();
		setUserAvatar(_currentUser.getAvatarImage());
	}

	@Override
	public void onHide() {
		super.onHide();
		getView().clearErrors();
		getView().clearEmail();
		oldUserInfo = null;
		getView().clearAvatarForm();
	}

	private void setEmail() {
		final Request<UserInfoProxy> emailReq = _requestFactory.appUserRequest().getCurrentUserSettings();
		emailReq.fire(new Receiver<UserInfoProxy>() {
			@Override
			public void onSuccess(final UserInfoProxy userInfo) {
				populateFields(userInfo);
				oldUserInfo = userInfo;
			}

			@Override
			public void onFailure(final ServerFailure failure) {
				if (failure.getExceptionType().equals(NotLoggedInException.class.getName())) {
					_eventBus.fireEvent(new CurrentUserChangedEvent(null));
					_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
				}
			}
		});
	}

	private void populateFields(final UserInfoProxy userInfo) {
		getView().setEmailValue(userInfo.getEmail());
		getView().setFollowingPrivate(userInfo.isFollowingPrivate());
	}

	private void setUploadAction() {
		// Create the blobstore URL that the avatar will be uploaded to
		// Need to recreate each time because session expires after succesful
		// upload
		final Request<String> blobRequest = _requestFactory.appUserRequest()
				.createBlobstoreUrl("/avatarUpload");
		blobRequest.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String url) {
				getView().createActionUrl(url);
			}
		});
	}

	@Override
	public void setUserAvatar(final String url) {
		getView().clearAvatarForm();
		setUploadAction();
		_currentUser.setAvatar(url);
		getView().setAvatarImg(url);
	}

	@Override
	public void saveUserInfo(final String email, final boolean followingPrivate) {
		getView().clearErrors();

		final AppUserRequest appUserRequest = _requestFactory.appUserRequest();

		// Clone user info because request factory is silly
		final UserInfoProxy userInfo = appUserRequest.create(UserInfoProxy.class);
		userInfo.setEmail(email);
		userInfo.setFollowingPrivate(followingPrivate);

		if (userInfo.equals(oldUserInfo))
			return;

		final String emailError = Validator.validateEmail(userInfo.getEmail());
		if (emailError == null) {

			LoadingIndicator.show();
			appUserRequest.setUserInfo(userInfo).fire(new Receiver<Boolean>() {
				@Override
				public void onSuccess(final Boolean saved) {
					LoadingIndicator.hide();
					if (saved == true) {
						oldUserInfo = userInfo;
						populateFields(userInfo);
						getView().setFormStatusMessage("Profile saved");
					}
					else {
						getView().setFormStatusMessage("Error: Profile not saved");
					}
				}

				@Override
				public void onFailure(final ServerFailure failure) {

					LoadingIndicator.hide();
					if (failure.getExceptionType().equals(EmailIDAlreadyExistsException.class.getName())) {
						getView().setEmailError("A user with that email already exists");
					}
				}
			});
		}
		else {
			getView().setEmailError(emailError);
		}
	}
}

interface ProfileUiHandlers extends UiHandlers {
	void setUserAvatar(String avatar);

	void saveUserInfo(String email, boolean followingPrivate);
}
