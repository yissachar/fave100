package com.fave100.client.widgets;

import com.google.gwt.user.client.ui.TextBox;

public class FaveTextBox extends TextBox {

	public FaveTextBox() {
		this.getElement().setAttribute("autocorrect", "off");
		this.getElement().setAttribute("autocapitalize", "off");
	}

}
