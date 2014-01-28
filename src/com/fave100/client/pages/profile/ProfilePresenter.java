package com.fave100.client.pages.profile;

import com.fave100.client.CurrentUser;
import com.fave100.client.LoadingIndicator;
import com.fave100.client.gatekeepers.LoggedInGatekeeper;
import com.fave100.client.generated.entities.BooleanResultDto;
import com.fave100.client.generated.entities.StringResultDto;
import com.fave100.client.generated.entities.UserInfoDto;
import com.fave100.client.generated.services.AppUserService;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.rest.RestSessionDispatch;
import com.fave100.shared.Validator;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
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
	private CurrentUser _currentUser;
	private PlaceManager _placeManager;
	private RestSessionDispatch _dispatcher;
	private AppUserService _appUserService;
	private UserInfoDto oldUserInfo = null;

	@Inject
	public ProfilePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final CurrentUser currentUser,
							final PlaceManager placeManager, final RestSessionDispatch dispatcher, final AppUserService appUserService) {
		super(eventBus, view, proxy);
		_eventBus = eventBus;
		_currentUser = currentUser;
		_placeManager = placeManager;
		_dispatcher = dispatcher;
		_appUserService = appUserService;
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
		_dispatcher.execute(_appUserService.getCurrentUserSettings(), new AsyncCallback<UserInfoDto>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO If not logged in redirect to login				
			}

			@Override
			public void onSuccess(UserInfoDto userInfo) {
				populateFields(userInfo);
				oldUserInfo = userInfo;
			}
		});
	}

	private void populateFields(final UserInfoDto userInfo) {
		getView().setEmailValue(userInfo.getEmail());
		getView().setFollowingPrivate(userInfo.isFollowingPrivate());
	}

	private void setUploadAction() {
		// Create the blobstore URL that the avatar will be uploaded to
		// Need to recreate each time because session expires after successful upload
		_dispatcher.execute(_appUserService.createBlobstoreUrl("/avatarUpload"), new AsyncCallback<StringResultDto>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(StringResultDto url) {
				getView().createActionUrl(url.getValue());
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

		final UserInfoDto userInfo = new UserInfoDto();
		userInfo.setEmail(email);
		userInfo.setFollowingPrivate(followingPrivate);

		if (userInfo.equals(oldUserInfo))
			return;

		final String emailError = Validator.validateEmail(userInfo.getEmail());
		if (emailError == null) {

			LoadingIndicator.show();
			_dispatcher.execute(_appUserService.setUserInfo(userInfo), new AsyncCallback<BooleanResultDto>() {

				@Override
				public void onFailure(Throwable caught) {
					LoadingIndicator.hide();
					getView().setEmailError(caught.getMessage());
				}

				@Override
				public void onSuccess(BooleanResultDto saved) {
					LoadingIndicator.hide();
					if (saved.getValue()) {
						oldUserInfo = userInfo;
						populateFields(userInfo);
						getView().setFormStatusMessage("Profile saved");
					}
					else {
						getView().setFormStatusMessage("Error: Profile not saved");
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
