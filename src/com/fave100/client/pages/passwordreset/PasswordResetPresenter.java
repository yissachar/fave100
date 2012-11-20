package com.fave100.client.pages.passwordreset;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
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

public class PasswordResetPresenter
	extends
	BasePresenter<PasswordResetPresenter.MyView, PasswordResetPresenter.MyProxy>
	implements PasswordResetUiHandlers{

	public interface MyView extends BaseView, HasUiHandlers<PasswordResetUiHandlers> {
		void showPwdChangeForm();
		void showSendTokenForm();
		void showTokenError();
		void showTokenSuccess();
		void showPwdError();
	}

	private ApplicationRequestFactory requestFactory;
	private PlaceManager placeManager;
	private String token;

	@ProxyCodeSplit
	@NameToken(NameTokens.passwordreset)
	public interface MyProxy extends ProxyPlace<PasswordResetPresenter> {
	}

	@Inject
	public PasswordResetPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory,
			final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
		this.placeManager = placeManager;
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
		if(token.isEmpty()) {
			getView().showSendTokenForm();
		} else {
			// We have a token, allow user to change the password
			getView().showPwdChangeForm();
		}
		getProxy().manualReveal(PasswordResetPresenter.this);
	}

	@Override
	public void sendEmail(final String username, final String emailAddress) {
		final Request<Boolean> sendEmailReq = requestFactory.appUserRequest().emailPasswordResetToken(username, emailAddress);
		sendEmailReq.fire(new Receiver<Boolean>() {
			@Override
			public void onSuccess(final Boolean validInfo) {
				if(validInfo == false) {
					// Warn user if invalid username or email
					getView().showTokenError();
				} else {
					//  On success tell user email was sent
					getView().showTokenSuccess();
				}
			}
		});

	}

	@Override
	public void changePassword(final String password) {
		final Request<Boolean> changePasswordReq = requestFactory.appUserRequest().changePassword(password, token);
		changePasswordReq.fire(new Receiver<Boolean>() {
			@Override
			public void onSuccess(final Boolean pwdChanged) {
				if(pwdChanged == false) {
					getView().showPwdError();
				} else {
					placeManager.revealPlace(new PlaceRequest(NameTokens.login));
				}
			}
		});
	}
}

interface PasswordResetUiHandlers extends UiHandlers {
	void sendEmail(String username, String emailAddress);
	void changePassword(String password);
}
