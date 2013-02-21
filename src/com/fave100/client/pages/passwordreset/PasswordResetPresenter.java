package com.fave100.client.pages.passwordreset;

import com.fave100.client.CurrentUser;
import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.shared.Validator;
import com.fave100.shared.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

/**
 * Allows users to change their password or request a password reset token
 *
 * @author yissachar.radcliffe
 *
 */
public class PasswordResetPresenter
		extends
		BasePresenter<PasswordResetPresenter.MyView, PasswordResetPresenter.MyProxy>
		implements PasswordResetUiHandlers {

	public interface MyView extends BaseView,
			HasUiHandlers<PasswordResetUiHandlers> {
		void showPwdChangeForm(Boolean requireOldPwd);

		void showSendTokenForm();

		void showTokenError();

		void showTokenSuccess();

		void showPwdError(String errorMsg, Boolean inputError);

		void showCurrPwdError(String errorMsg);
	}

	private ApplicationRequestFactory	requestFactory;
	private PlaceManager				placeManager;
	private CurrentUser					currentUser;
	private String						token;

	@ProxyCodeSplit
	@NameToken(NameTokens.passwordreset)
	public interface MyProxy extends ProxyPlace<PasswordResetPresenter> {
	}

	@Inject
	public PasswordResetPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy,
			final ApplicationRequestFactory requestFactory,
			final PlaceManager placeManager, final CurrentUser currentUser) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
		this.currentUser = currentUser;
		getView().setUiHandlers(this);
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
	public boolean useManualReveal() {
		return true;
	}

	@Override
	public void prepareFromRequest(final PlaceRequest placeRequest) {
		super.prepareFromRequest(placeRequest);

		// Use parameters to determine what to reveal on page
		token = placeRequest.getParameter("token", "");

		if (currentUser.isLoggedIn()) {
			// User is logged in, allow password change if they enter old
			// password first
			getView().showPwdChangeForm(true);
		} else if (!token.isEmpty()) {
			// User is not logged in but has a password change token
			// allow changing password without old password
			getView().showPwdChangeForm(false);
		} else {
			// User not logged in and does not have a password change token
			// allow them to request a password change token
			getView().showSendTokenForm();
		}
		getProxy().manualReveal(PasswordResetPresenter.this);
	}

	@Override
	public void sendEmail(final String username, final String emailAddress) {
		final Request<Boolean> sendEmailReq = requestFactory.appUserRequest()
				.emailPasswordResetToken(username, emailAddress);
		sendEmailReq.fire(new Receiver<Boolean>() {
			@Override
			public void onSuccess(final Boolean validInfo) {
				if (validInfo == false) {
					// Warn user if invalid username or email
					getView().showTokenError();
				} else {
					// On success tell user email was sent
					getView().showTokenSuccess();
				}
			}
		});

	}

	@Override
	public void changePassword(final String newPassword,
			final String newPasswordRepeat, final String currPassword) {

		// Set error message if newPassword doesn't validate
		final String errorMsg = Validator.validatePassword(newPassword);
		if (errorMsg != null) {
			getView().showPwdError(errorMsg, true);
			return;
		} else if (!newPassword.equals(newPasswordRepeat)) {
			getView().showPwdError("Passwords must match", true);
			return;
		}

		// Try to change password with old password if exists
		// Otherwise use token to change password if exists
		String passwordOrToken = token;
		if (currPassword != null && !currPassword.isEmpty()) {
			passwordOrToken = currPassword;
		}

		final Request<Boolean> changePasswordReq = requestFactory
				.appUserRequest().changePassword(newPassword, passwordOrToken);
		changePasswordReq.fire(new Receiver<Boolean>() {
			@Override
			public void onSuccess(final Boolean pwdChanged) {
				if (pwdChanged == false) {
					String errorMsg = "Incorrect password";
					if (!token.isEmpty()) {
						errorMsg = "Token expired or doesn't exist";
					}
					getView().showPwdError(errorMsg, false);
				} else {
					placeManager
							.revealPlace(new PlaceRequest(NameTokens.login));
				}
			}
		});
	}
}

interface PasswordResetUiHandlers extends UiHandlers {
	void sendEmail(String username, String emailAddress);

	void changePassword(String newPassword, final String newPasswordRepeat,
			String currPassword);
}
