package com.fave100.client.pages.lists.widgets.favelist.widgets;

import com.fave100.client.Notification;
import com.fave100.shared.Constants;
import com.fave100.shared.Validator;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FavePickWhyLineInput extends Composite {

	private static FavePickWhyLineInputUiBinder uiBinder = GWT.create(FavePickWhyLineInputUiBinder.class);

	interface FavePickWhyLineInputUiBinder extends UiBinder<Widget, FavePickWhyLineInput> {
	}

	@UiField Label whyLineLabel;
	@UiField TextBox whyLineTextBox;
	private FavePickWidget _favePickWidget;

	public FavePickWhyLineInput(FavePickWidget favePickWidget) {
		initWidget(uiBinder.createAndBindUi(this));
		_favePickWidget = favePickWidget;

		whyLineTextBox.setMaxLength(Constants.MAX_WHYLINE_LENGTH);
		setFieldsText(_favePickWidget.getWhyLine());
		whyLineTextBox.setVisible(false);
	}

	@UiHandler("whyLineLabel")
	void onLabelClick(ClickEvent event) {
		focus();
	}

	@UiHandler("whyLineLabel")
	void onLabelMouseOver(MouseOverEvent event) {
		if (whyLineLabel.getText().trim().isEmpty()) {
			whyLineLabel.setText("Click to enter Why-Line");
		}
	}

	@UiHandler("whyLineLabel")
	void onLabelMouseOut(MouseOutEvent event) {
		setFieldsText(_favePickWidget.getWhyLine());
	}

	@UiHandler("whyLineTextBox")
	void onTextBoxBlur(BlurEvent event) {
		saveAndSwitchToLabel();
	}

	@UiHandler("whyLineTextBox")
	void onTextBoxKeyDown(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			saveAndSwitchToLabel();
		}
		else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
			switchToLabel();
		}
	}

	private void saveAndSwitchToLabel() {
		String whyLine = whyLineTextBox.getText().trim();

		final String error = Validator.validateWhyline(whyLine);
		if (error != null) {
			Notification.show(error, true);
		}
		else if (!whyLine.equals(_favePickWidget.getWhyLine())) {
			_favePickWidget.updateWhyLine(whyLine);
			setFieldsText(whyLine);
		}

		switchToLabel();
	}

	private void switchToLabel() {
		_favePickWidget.hideWhyLineHelpBubble();
		whyLineLabel.setVisible(true);
		whyLineTextBox.setVisible(false);
	}

	private void setFieldsText(String whyLine) {
		whyLineLabel.setText((whyLine == null || whyLine.isEmpty()) ? " " : _favePickWidget.getWhyLine());
		whyLineTextBox.setText(_favePickWidget.getWhyLine());
	}

	public void focus() {
		whyLineLabel.setVisible(false);
		whyLineTextBox.setVisible(true);
		whyLineTextBox.setFocus(true);
		whyLineTextBox.selectAll();
	}

}
