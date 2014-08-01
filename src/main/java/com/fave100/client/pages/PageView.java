package com.fave100.client.pages;

import com.google.gwt.uibinder.client.UiField;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class PageView<T extends UiHandlers> extends ViewWithUiHandlers<T>
		implements View {

	@UiField public Page page;
}
