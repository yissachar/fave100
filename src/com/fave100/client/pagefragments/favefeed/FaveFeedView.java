package com.fave100.client.pagefragments.favefeed;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class FaveFeedView extends ViewImpl implements FaveFeedPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, FaveFeedView> {
	}
	
	@UiField InlineHTML panel;

	@Inject
	public FaveFeedView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setFaveFeedContent(final SafeHtml html) {
		panel.setHTML(html);
	}
}
