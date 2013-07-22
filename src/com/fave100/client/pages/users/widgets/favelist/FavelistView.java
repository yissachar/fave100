package com.fave100.client.pages.users.widgets.favelist;

import java.util.List;

import com.fave100.client.pages.users.widgets.favelist.widgets.FavePickWidget;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
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
	@UiField FlowPanel favelist;

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

	@Override
	public void swapPicks(final int indexA, final int indexB) {
		final Widget widget = favelist.getWidget(indexA);
		favelist.remove(widget);
		favelist.insert(widget, indexB);
		/*final Widget widget2 = favelist.getWidget(indexB);
		final GQuery $set1 = $(widget);
		final GQuery $set2 = $(widget2);

		final int move1 = $set2.offset().top - $(favelist).offset().top * 2;
		final int move2 = $(favelist).offset().top - $set1.outerHeight();
		GWT.log("1:" + move1);
		GWT.log("2:" + move2);
		// let's move stuff
		$set1.css("position", "relative");
		$set2.css("position", "relative");
		$set1.animate("top:" + move1, 1000);
		$set2.animate("top:" + move2, 1000, new Function() {
			@Override
			public void f() {
					$set1.css("position: static");
					$set1.css("top: 0");
					$set2.css("position: static");
					$set2.css("top: 0");
					favelist.remove(widget);
					favelist.insert(widget, indexB);
			}
		});*/

	}
}
