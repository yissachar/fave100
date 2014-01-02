package com.fave100.client.pagefragments.footer;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A common footer will be displayed at the bottom of every page.
 * 
 * @author yissachar.radcliffe
 * 
 */
public class Footer extends Composite {

	private static FooterUiBinder uiBinder = GWT.create(FooterUiBinder.class);

	interface FooterUiBinder extends UiBinder<Widget, Footer> {
	}

	@UiField SpanElement year;

	public Footer() {
		initWidget(uiBinder.createAndBindUi(this));
		year.setInnerText(String.valueOf(1900 + new Date().getYear()));
	}

}
