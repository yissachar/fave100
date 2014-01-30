package com.fave100.client.pages.passwordreset;

import com.fave100.client.CurrentUser;
import com.fave100.client.generated.entities.BooleanResultDto;
import com.fave100.client.generated.services.RestServiceFactory;
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

	private PlaceManager _placeManager;
	private CurrentUser _currentUser;
	private RestSessionDispatch _dispatcher;
	private RestServiceFactory _restServiceFactory;
	private EventBus _eventBus;
	private String token;

	@ProxyCodeSplit
	@NameToken(NameTokens.passwordreset)
	public interface MyProxy extends ProxyPlace<PasswordResetPresenter> {
	}

	@Inject
	public PasswordResetPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final PlaceManager placeManager,
									final CurrentUser currentUser, final RestSessionDispatch dispatcher, final RestServiceFactory restServiceFactory) {
		super(eventBus, view, proxy);
		_placeManager = placeManager;
		_currentUser = currentUser;
		_eventBus = eventBus;
		_dispatcher = dispatcher;
		_restServiceFactory = restServiceFactory;
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
		_dispatcher.execute(_restServiceFactory.getAppUserService().emailPasswordResetToken(username, emailAddress), new AsyncCallback<BooleanResultDto>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub				
			}

			@Override
			public void onSuccess(BooleanResultDto validInfo) {
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
	public void changePassword(final String newPassword,
			final String newPasswordRepeat, final String currPassword) {

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

		_dispatcher.execute(_restServiceFactory.getAppUserService().changePassword(newPassword, passwordOrToken), new AsyncCallback<BooleanResultDto>() {

			@Override
			public void onFailure(Throwable caught) {
				getView().showPwdError(caught.getMessage(), false);
			}

			@Override
			public void onSuccess(BooleanResultDto pwdChanged) {
				if (!pwdChanged.getValue()) {
					String errorMsg = "Incorrect password";
					if (!token.isEmpty()) {
						errorMsg = "Token expired or doesn't exist";
					}
					getView().showPwdError(errorMsg, false);
				}
				else {
					_placeManager
							.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.login).build());
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
