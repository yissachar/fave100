package com.fave100.client.pages.profile;

import com.fave100.client.CurrentUser;
import com.fave100.client.LoadingIndicator;
import com.fave100.client.gatekeepers.LoggedInGatekeeper;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Validator;
import com.fave100.shared.exceptions.user.EmailIDAlreadyExistsException;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
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
		BasePresenter<ProfilePresenter.MyView, ProfilePresenter.MyProxy>
		implements ProfileUiHandlers {

	public interface MyView extends BaseView, HasUiHandlers<ProfileUiHandlers> {
		void createActionUrl(String url);

		void setEmailValue(String val);

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

	private ApplicationRequestFactory requestFactory;
	private CurrentUser currentUser;
	private String oldEmail = "";

	@Inject
	public ProfilePresenter(final EventBus eventBus, final MyView view,
							final MyProxy proxy,
							final ApplicationRequestFactory requestFactory,
							final CurrentUser currentUser) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.currentUser = currentUser;
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
		setUserAvatar(currentUser.getAvatarImage());
	}

	@Override
	public void onHide() {
		super.onHide();
		getView().clearErrors();
		getView().clearEmail();
		oldEmail = "";
		getView().clearAvatarForm();
	}

	private void setEmail() {
		final Request<String> emailReq = requestFactory.appUserRequest().getEmailForCurrentUser();
		emailReq.fire(new Receiver<String>() {
			@Override
			public void onSuccess(final String email) {
				getView().setEmailValue(email);
				oldEmail = email;
			}
		});
	}

	private void setUploadAction() {
		// Create the blobstore URL that the avatar will be uploaded to
		// Need to recreate each time because session expires after succesful
		// upload
		final Request<String> blobRequest = requestFactory.appUserRequest()
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
		currentUser.setAvatar(url);
		getView().setAvatarImg(url);
	}

	@Override
	public void saveProfileData(final String email) {
		getView().clearErrors();

		if (email.equals(oldEmail))
			return;

		final String emailError = Validator.validateEmail(email);
		if (emailError == null) {
			final Request<Boolean> setProfileDataReq = requestFactory
					.appUserRequest().setProfileData(email);
			LoadingIndicator.show();
			setProfileDataReq.fire(new Receiver<Boolean>() {
				@Override
				public void onSuccess(final Boolean saved) {
					LoadingIndicator.hide();
					if (saved == true) {
						oldEmail = email;
						getView().setEmailValue(email);
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

	void saveProfileData(String email);
}
