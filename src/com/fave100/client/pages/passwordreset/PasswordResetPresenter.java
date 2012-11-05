package com.fave100.client.pages.passwordreset;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.BaseView;
import com.fave100.client.place.NameTokens;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;

public class PasswordResetPresenter
	extends
	BasePresenter<PasswordResetPresenter.MyView, PasswordResetPresenter.MyProxy> 
	implements PasswordResetUiHandlers{

	public interface MyView extends BaseView, HasUiHandlers<PasswordResetUiHandlers> {	
	}
	
	private ApplicationRequestFactory requestFactory;

	@ProxyCodeSplit
	@NameToken(NameTokens.passwordreset)
	public interface MyProxy extends ProxyPlace<PasswordResetPresenter> {
	}

	@Inject
	public PasswordResetPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final ApplicationRequestFactory requestFactory) {
		super(eventBus, view, proxy);
		this.requestFactory = requestFactory;
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
	public void sendEmail(final String username, final String emailAddress) {
		final Request<Boolean> sendEmailReq = requestFactory.appUserRequest().emailPasswordResetToken(username, emailAddress);
		sendEmailReq.fire(new Receiver<Boolean>() {
			@Override
			public void onSuccess(final Boolean validInfo) {Window.alert(validInfo.toString());
				//TODO: Warn user if invalid username or email
				//TODO: On success tell user email was sent
			}
		});
		
	}
}

interface PasswordResetUiHandlers extends UiHandlers {
	void sendEmail(String username, String emailAddress);
}
