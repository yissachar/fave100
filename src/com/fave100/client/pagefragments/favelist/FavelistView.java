package com.fave100.client.pagefragments.favelist;

import java.util.List;

import com.fave100.client.pagefragments.favelist.widgets.FavePickWidget;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class FavelistView extends ViewWithUiHandlers<FavelistUiHandlers>
		implements FavelistPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, FavelistView> {
	}

	interface FavelistStyle extends CssResource {
		String personalListItem();

		String rank();

		String rankEditor();

		String detailsContainer();

		String songLink();

		String whyline();
	}

	@UiField FavelistStyle style;
	@UiField HTMLPanel favelist;
	private HTMLPanel draggedElement;

	@Inject
	public FavelistView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setList(final List<FavePickWidget> widgets) {
		favelist.clear();

		if (widgets == null)
			return;

		for (final FavePickWidget widget : widgets) {
			favelist.add(widget);
		}
	}

	@Override
	public void addPick(final FavePickWidget widget) {
		favelist.add(widget);
	}
}
