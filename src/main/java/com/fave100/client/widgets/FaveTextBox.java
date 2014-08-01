package com.fave100.client.widgets;

import com.google.gwt.user.client.ui.TextBox;

public class FaveTextBox extends TextBox {

	public FaveTextBox() {
		getElement().setAttribute("autocorrect", "off");
		getElement().setAttribute("autocapitalize", "off");
	}

	public void setPlaceHolder(String placeholder) {
		getElement().setPropertyString("placeholder", placeholder);
	}

}
