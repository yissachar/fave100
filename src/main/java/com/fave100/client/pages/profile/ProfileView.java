package com.fave100.client.pages.profile;

import static com.google.gwt.query.client.GQuery.$;

import com.fave100.client.pages.BasePresenter;
import com.fave100.shared.Constants;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ProfileView extends ViewWithUiHandlers<ProfileUiHandlers>
		implements ProfilePresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ProfileView> {
	}

	@UiField HTMLPanel topBar;
	@UiField FormPanel profileForm;
	@UiField TextBox emailInput;
	@UiField CheckBox followingPrivate;
	@UiField Button profileSaveButton;
	@UiField Image avatarImg;
	@UiField FileUpload avatarUpload;
	@UiField Label emailStatusMessage;
	@UiField Label formStatusMessage;
	@UiField SubmitButton avatarSubmitButton;

	@Inject
	public ProfileView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		avatarSubmitButton.setEnabled(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();
			if (content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@Override
	public void createActionUrl(final String url) {
		profileForm.setAction(url);
	}

	@UiHandler("profileSaveButton")
	public void onProfileSaveClick(final ClickEvent event) {
		getUiHandlers().saveUserInfo(emailInput.getValue(), followingPrivate.getValue());
	}

	@UiHandler("avatarUpload")
	public void onFileChange(final ChangeEvent event) {
		final String filename = avatarUpload.getFilename();
		if (filename == null || filename.isEmpty() || filename.equals("Unknown")) {
			avatarSubmitButton.setEnabled(false);
		}
		else {
			avatarSubmitButton.setEnabled(true);
		}
	}

	@UiHandler("profileForm")
	public void onSubmitComplete(final SubmitCompleteEvent event) {
		// TODO: Is there any more robust way of checking for 413 error?
		String results = event.getResults();
		if (results != null) {
			if (results.contains("413 Request Entity Too Large")) {
				// File too large for upload
				setFormStatusMessage("File too large. Max size is " + Constants.MAX_AVATAR_SIZE / 1024 + " KB", 4000, true);
			}
			else {
				results = results.replace("<pre>", "").replace("</pre>", "");
				// Set the users avatar on client	
				final RegExp urlValidator = RegExp.compile("^((ftp|http|https)://[\\w@.\\-\\_]+(:\\d{1,5})?(/[\\w#!:.?+=&%@!\\_\\-/]+)*){1}$");
				if (urlValidator.exec(results) != null) {
					getUiHandlers().setUserAvatar(results);
				}
			}
		}
	}

	@Override
	public void clearEmail() {
		emailInput.setValue("");
	}

	@Override
	public void setEmailValue(final String val) {
		emailInput.setValue(val);
	}

	@Override
	public void setFollowingPrivate(final boolean checked) {
		followingPrivate.setValue(checked);
	}

	@Override
	public void setAvatarImg(final String src) {
		avatarImg.setUrl(src);
	}

	@Override
	public void setEmailError(final String error) {
		emailInput.addStyleName("errorInput");
		emailStatusMessage.setText(error);
	}

	@Override
	public void setFormStatusMessage(final String message) {
		setFormStatusMessage(message, 1000, false);
	}

	public void setFormStatusMessage(final String message, final int delay, final boolean error) {
		if (error) {
			formStatusMessage.addStyleName("error");
		}
		else {
			formStatusMessage.removeStyleName("error");
		}
		formStatusMessage.setText(message);
		formStatusMessage.setVisible(true);
		$(formStatusMessage).delay(delay).fadeOut(1500);
	}

	@Override
	public void clearErrors() {
		formStatusMessage.setText("");
		emailInput.removeStyleName("errorInput");
		emailStatusMessage.setText("");
	}

	@Override
	public void clearAvatarForm() {
		profileForm.reset();
		avatarSubmitButton.setEnabled(false);
	}
}