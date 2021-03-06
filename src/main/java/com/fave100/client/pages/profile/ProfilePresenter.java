package com.fave100.client.pages.profile;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.gatekeepers.LoggedInGatekeeper;
import com.fave100.client.generated.entities.BooleanResult;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.entities.UserInfo;
import com.fave100.client.pages.PagePresenter;
import com.fave100.shared.Validator;
import com.fave100.shared.place.NameTokens;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.shared.RestCallback;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

/**
 * Shows the logged in user their profile
 * 
 * @author yissachar.radcliffe
 * 
 */
public class ProfilePresenter extends
		PagePresenter<ProfilePresenter.MyView, ProfilePresenter.MyProxy>
		implements ProfileUiHandlers {

	public interface MyView extends View, HasUiHandlers<ProfileUiHandlers> {
		void createActionUrl(String url);

		void setEmailValue(String val);

		void setFollowingPrivate(boolean checked);

		void setEmailError(String error);

		void clearErrors();

		void setProfileSaveMessage(String message, boolean error);

		void setAvatarImg(String src);

		void clearAvatarForm();

		void clearEmail();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.profile)
	@UseGatekeeper(LoggedInGatekeeper.class)
	public interface MyProxy extends ProxyPlace<ProfilePresenter> {
	}

	private CurrentUser _currentUser;
	private FaveApi _api;
	private UserInfo oldUserInfo = null;

	@Inject
	public ProfilePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final CurrentUser currentUser,
							final FaveApi api) {
		super(eventBus, view, proxy);
		_currentUser = currentUser;
		_api = api;

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
		_api.call(_api.service().user().getCurrentUserSettings(), new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO If not logged in redirect to login				
			}

			@Override
			public void onSuccess(UserInfo userInfo) {
				populateFields(userInfo);
				oldUserInfo = userInfo;
			}
		});
	}

	private void populateFields(final UserInfo userInfo) {
		getView().setEmailValue(userInfo.getEmail());
		getView().setFollowingPrivate(userInfo.isFollowingPrivate());
	}

	private void setUploadAction() {
		// Create the blobstore URL that the avatar will be uploaded to
		// Need to recreate each time because session expires after successful upload
		_api.call(_api.service().user().createBlobstoreUrl(), new AsyncCallback<StringResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(StringResult url) {
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

		final UserInfo userInfo = new UserInfo();
		userInfo.setEmail(email);
		userInfo.setFollowingPrivate(followingPrivate);

		if (userInfo.equals(oldUserInfo))
			return;

		final String emailError = Validator.validateEmail(userInfo.getEmail());
		if (emailError == null) {

			_api.call(_api.service().user().setUserInfo(userInfo), new RestCallback<BooleanResult>() {

				@Override
				public void setResponse(Response response) {
					if (response.getStatusCode() >= 400) {
						getView().setEmailError(response.getText());
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					// Already handled in setResponse
				}

				@Override
				public void onSuccess(BooleanResult saved) {
					if (saved.getValue()) {
						oldUserInfo = userInfo;
						populateFields(userInfo);
						getView().setProfileSaveMessage("Profile saved", false);
					}
					else {
						getView().setProfileSaveMessage("Error: Profile not saved", true);
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
