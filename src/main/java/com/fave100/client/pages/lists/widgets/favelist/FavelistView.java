package com.fave100.client.pages.lists.widgets.favelist;

import static com.google.gwt.query.client.GQuery.$;

import java.util.List;

import com.fave100.client.pages.lists.widgets.favelist.widgets.FavePickWidget;
import com.fave100.client.resources.css.GlobalStyle;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class FavelistView extends ViewWithUiHandlers<FavelistUiHandlers>
		implements FavelistPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, FavelistView> {
	}

	interface FavelistStyle extends GlobalStyle {
		String personalListItem();

		String rank();

		String rankEditor();

		String detailsContainer();

		String songLink();

		String whyline();

		String invisible();
	}

	@UiField FavelistStyle style;
	@UiField FlowPanel faveList;
	@UiField Label noItemsMessage;
	@UiField Label listNotFound;

	@Inject
	public FavelistView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		hideNoItemsMessage();
		setListFound(true);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setList(final List<FavePickWidget> widgets) {
		setListFound(true);

		hideNoItemsMessage();
		faveList.clear();
		faveList.removeStyleName(style.invisible());

		if (widgets == null || widgets.size() == 0) {
			noItemsMessage.setVisible(true);
			return;
		}

		for (final FavePickWidget widget : widgets) {
			faveList.add(widget);
		}
	}

	@Override
	public void addPick(final FavePickWidget widget) {
		faveList.add(widget);
		hideNoItemsMessage();

		if (this.asWidget().getElement().getClientHeight() + widget.getElement().getClientHeight() > Window.getClientHeight())
			$(widget).scrollIntoView();
	}

	@Override
	public void swapPicks(final int indexA, final int indexB) {
		final Widget widget = faveList.getWidget(indexA);
		faveList.remove(widget);
		faveList.insert(widget, indexB);
	}

	@Override
	public void hideNoItemsMessage() {
		noItemsMessage.setVisible(false);
	}

	@Override
	public void setListFound(boolean found) {
		listNotFound.setVisible(!found);
		faveList.setVisible(found);
	}

	@Override
	public void clearState() {
		hideNoItemsMessage();
		listNotFound.setVisible(false);
		faveList.addStyleName(style.invisible());
	}
}
