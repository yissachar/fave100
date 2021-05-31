package com.fave100.client.pagefragments.register;

import com.fave100.client.FaveApi;
import com.fave100.client.Notification;
import com.fave100.client.events.user.CurrentUserChangedEvent;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.UserRegistration;
import com.fave100.shared.Validator;
import com.fave100.shared.place.NameTokens;
import com.fave100.shared.place.PlaceParams;
import com.google.gwt.http.client.Response;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rest.client.RestCallback;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class RegisterWidgetPresenter extends PresenterWidget<RegisterWidgetPresenter.MyView>
		implements RegisterWidgetUiHandlers {

	public interface MyView extends View, HasUiHandlers<RegisterWidgetUiHandlers> {
		void clearFields();

		void setNativeUsernameError(String error);

		void setEmailError(String error);

		void setPasswordError(String error);

		void setPasswordRepeatError(String error);

		void clearNativeErrors();

		void setUsernameFocus();
	}

	private EventBus _eventBus;
	private PlaceManager _placeManager;
	private FaveApi _api;

	@Inject
	public RegisterWidgetPresenter(final EventBus eventBus, final MyView view, final PlaceManager placeManager, final FaveApi api) {
		super(eventBus, view);
		_eventBus = eventBus;
		_placeManager = placeManager;
		_api = api;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	protected void onReveal() {
		super.onReveal();
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().clearFields();
		getView().clearNativeErrors();
	}

	@Override
	public void register(String username, String email, final String password, final String passwordRepeat) {

		username = username.trim();
		email = email.trim();

		if (validateFields(username, email, password, passwordRepeat)) {

			// Create a new user with the username and password entered
			UserRegistration registration = new UserRegistration();
			registration.setUsername(username);
			registration.setPassword(password);
			registration.setEmail(email);

			_api.call(_api.service().auth().createAppUser(registration), new RestCallback<AppUser>() {

				@Override
				public void setResponse(Response response) {
					if (response.getStatusCode() >= 400) {
						getView().setNativeUsernameError(response.getText());
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					// Already handled in setResponse
				}

				@Override
				public void onSuccess(AppUser createdUser) {
					_eventBus.fireEvent(new CurrentUserChangedEvent(createdUser));
					if (createdUser != null) {
						appUserCreated(createdUser);
					}
					else {
						getView().setPasswordError("An error occurred");
					}
				}
			});
		}
	}

	public void appUserCreated(AppUser createdUser) {
		_placeManager.revealPlace(new PlaceRequest.Builder()
				.nameToken(NameTokens.lists)
				.with(PlaceParams.USER_PARAM, createdUser.getUsername())
				.build());
		Notification.show("Thanks for registering!");
	}

	private boolean validateFields(final String username, final String email,
			final String password, final String passwordRepeat) {
		// Assume all valid
		getView().clearNativeErrors();
		boolean valid = true;

		// Check for validity

		final String usernameError = Validator.validateUsername(username);
		if (usernameError != null) {
			getView().setNativeUsernameError(usernameError);
			valid = false;
		}

		final String emailError = Validator.validateEmail(email);
		if (emailError != null) {
			getView().setEmailError(emailError);
			valid = false;
		}

		final String passwordError = Validator.validatePassword(password);
		if (passwordError != null) {
			getView().setPasswordError(passwordError);
			valid = false;
		}
		else if (!password.equals(passwordRepeat)) {
			getView().setPasswordRepeatError("Passwords must match");
			valid = false;
		}
		return valid;
	}

	@Override
	public void focus() {
		getView().setUsernameFocus();
	}
}

interface RegisterWidgetUiHandlers extends UiHandlers {

	void register(String username, String email, String password, String passwordRepeat);

	void focus();

}
