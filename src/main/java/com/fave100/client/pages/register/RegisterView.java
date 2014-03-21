package com.fave100.client.pages.register;

import com.fave100.client.pages.BasePresenter;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class RegisterView extends ViewWithUiHandlers<RegisterUiHandlers>
		implements RegisterPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, RegisterView> {
	}

	@UiField HTMLPanel topBar;
	@UiField HTMLPanel registerContainer;
	@UiField HTMLPanel registerWidget;
	@UiField Label thirdPartyUsernameStatusMessage;
	@UiField TextBox thirdPartyUsernameField;
	@UiField HTMLPanel thirdPartyUsernamePrompt;
	@UiField SubmitButton thirdPartyUsernameSubmitButton;;

	@Inject
	public RegisterView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		super.setInSlot(slot, content);

		if (slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			if (content != null) {
				topBar.add(content);
			}
		}

		if (slot == RegisterPresenter.REGISTER_SLOT) {
			registerWidget.clear();
			if (content != null) {
				registerWidget.add(content);
				registerWidget.addStyleName("fullLoginPage");
			}
		}
	}

	@UiHandler("thirdPartyRegisterForm")
	void onThirdPartyRegisterFormSubmit(final SubmitEvent event) {
		getUiHandlers().registerThirdParty(thirdPartyUsernameField.getValue());
	}

	@Override
	public void clearFields() {
		thirdPartyUsernameField.setValue("");
	}

	@Override
	public void showThirdPartyUsernamePrompt() {
		thirdPartyUsernamePrompt.setVisible(true);
		registerContainer.setVisible(false);
	}

	@Override
	public void hideThirdPartyUsernamePrompt() {
		thirdPartyUsernamePrompt.setVisible(false);
		registerContainer.setVisible(true);
	}

	@Override
	public void setThirdPartyUsernameError(final String error) {
		thirdPartyUsernameStatusMessage.setText(error);
		thirdPartyUsernameField.addStyleName("errorInput");
	}

	@Override
	public void clearThirdPartyErrors() {
		thirdPartyUsernameStatusMessage.setText("");
		thirdPartyUsernameField.removeStyleName("errorInput");
	}
}
