package com.fave100.client.pages;

import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class PageView<T extends UiHandlers> extends ViewWithUiHandlers<T>
		implements View {

	@UiField public Page page;

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		super.setInSlot(slot, content);

		if (content == null)
			return;

		if (slot == PagePresenter.TOP_BAR_SLOT) {
			page.setTopBar(content);
		}
	}

}
