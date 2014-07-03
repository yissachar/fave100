package com.fave100.client.pages.passwordreset;

import com.fave100.client.CurrentUser;
import com.fave100.client.FaveApi;
import com.fave100.client.generated.entities.BooleanResult;
import com.fave100.client.generated.entities.EmailPasswordResetDetails;
import com.fave100.client.generated.entities.PasswordChangeDetails;
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
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

/**
 * Allows users to change their password or request a password reset token
 * 
 * @author yissachar.radcliffe
 * 
 */
public class PasswordResetPresenter
		extends
		PagePresenter<PasswordResetPresenter.MyView, PasswordResetPresenter.MyProxy>
		implements PasswordResetUiHandlers {

	public interface MyView extends View,
			HasUiHandlers<PasswordResetUiHandlers> {
		void showPwdChangeForm(Boolean requireOldPwd);

		void showSendTokenForm();

		void showTokenError();

		void showTokenSuccess();

		void showPwdError(String errorMsg, Boolean inputError);

		void showCurrPwdError(String errorMsg);
	}

	private PlaceManager _placeManager;
	private CurrentUser _currentUser;
	private FaveApi _api;
	private String token;

	@ProxyCodeSplit
	@NameToken(NameTokens.passwordreset)
	public interface MyProxy extends ProxyPlace<PasswordResetPresenter> {
	}

	@Inject
	public PasswordResetPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final PlaceManager placeManager,
									final CurrentUser currentUser, final FaveApi api) {
		super(eventBus, view, proxy);
		_placeManager = placeManager;
		_currentUser = currentUser;
		_api = api;

		getView().setUiHandlers(this);
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(this, this);
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

		if (_currentUser.isLoggedIn()) {
			// User is logged in, allow password change if they enter old
			// password first
			getView().showPwdChangeForm(true);
		}
		else if (!token.isEmpty()) {
			// User is not logged in but has a password change token
			// allow changing password without old password
			getView().showPwdChangeForm(false);
		}
		else {
			// User not logged in and does not have a password change token
			// allow them to request a password change token
			getView().showSendTokenForm();
		}
		getProxy().manualReveal(PasswordResetPresenter.this);
	}

	@Override
	public void sendEmail(final String username, final String emailAddress) {
		EmailPasswordResetDetails emailPasswordResetDetails = new EmailPasswordResetDetails();
		emailPasswordResetDetails.setUsername(username);
		emailPasswordResetDetails.setEmailAddress(emailAddress);

		_api.call(_api.service().user().emailPasswordResetToken(emailPasswordResetDetails), new AsyncCallback<BooleanResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(BooleanResult validInfo) {
				if (!validInfo.getValue()) {
					// Warn user if invalid username or email
					getView().showTokenError();
				}
				else {
					// On success tell user email was sent
					getView().showTokenSuccess();
				}
			}
		});
	}

	@Override
	public void changePassword(final String newPassword, final String newPasswordRepeat, final String currPassword) {

		// Set error message if newPassword doesn't validate
		final String errorMsg = Validator.validatePassword(newPassword);
		if (errorMsg != null) {
			getView().showPwdError(errorMsg, true);
			return;
		}
		else if (!newPassword.equals(newPasswordRepeat)) {
			getView().showPwdError("Passwords must match", true);
			return;
		}

		// Try to change password with old password if exists
		// Otherwise use token to change password if exists
		String passwordOrToken = token;
		if (currPassword != null && !currPassword.isEmpty()) {
			passwordOrToken = currPassword;
		}

		PasswordChangeDetails passwordChangeDetails = new PasswordChangeDetails();
		passwordChangeDetails.setNewPassword(newPassword);
		passwordChangeDetails.setTokenOrPassword(passwordOrToken);

		_api.call(_api.service().user().changePassword(passwordChangeDetails), new RestCallback<BooleanResult>() {

			@Override
			public void setResponse(Response response) {
				if (response.getStatusCode() >= 400) {
					getView().showPwdError(response.getText(), false);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// Already handled in setResponse
			}

			@Override
			public void onSuccess(BooleanResult pwdChanged) {
				if (!pwdChanged.getValue()) {
					String errorMsg = "Incorrect password";
					if (!token.isEmpty()) {
						errorMsg = "Token expired or doesn't exist";
					}
					getView().showPwdError(errorMsg, false);
				}
				else {
					_currentUser.logout();
					_placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.profile).build());
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
