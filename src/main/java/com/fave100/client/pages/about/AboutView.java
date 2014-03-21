package com.fave100.client.pages.about;

import javax.inject.Inject;

import com.fave100.client.pages.PageView;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class AboutView extends PageView<AboutUiHandlers> implements AboutPresenter.MyView {

	interface Binder extends UiBinder<Widget, AboutView> {
	}

	@Inject
	AboutView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
