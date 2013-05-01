package com.fave100.client.pages.explore;

import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fave100.client.pages.BasePresenter;
import com.fave100.client.pages.explore.widgets.ExploreItem;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.query.client.Function;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ExploreView extends ViewWithUiHandlers<ExploreUiHandlers> implements ExplorePresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ExploreView> {
	}

	@UiField HTMLPanel topBar;
	@UiField DivElement exploreContainer;
	private List<ExploreItem> visibleItems = new ArrayList<ExploreItem>();
	private int dropPosition = 0;

	@Inject
	public ExploreView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final Widget content) {
		if (slot == BasePresenter.TOP_BAR_SLOT) {
			topBar.clear();

			if (content != null) {
				topBar.add(content);
			}
		}
		super.setInSlot(slot, content);
	}

	@Override
	public void setExploreList(final List<ExploreItem> results) {
		while (exploreContainer.hasChildNodes()) {
			exploreContainer.removeChild(exploreContainer.getLastChild());
		}
		visibleItems.clear();

		// First add all items to container
		int index = 2;// Start from index 2 to make sure not visible
		for (final ExploreItem item : results) {
			exploreContainer.appendChild(item.getElement());
			$(item).css("top", "-" + String.valueOf(index * $(item).outerHeight(true)) + "px");
			index++;
		}

		// Then, in reverse order float them down screen
		dropPosition = -$(exploreContainer).outerHeight(true);//-810;
		Collections.reverse(results);
		int delay = 0;
		index = 0;
		for (final ExploreItem item : results) {
			// Hide all items
			$(item).css("opacity", "0");
			// Animate down and reveal
			$(item).delay(delay, new Function() {
				@Override
				public void f() {
					visibleItems.add(item);
					// Scroll new item to list and then scroll everything down
					dropPosition += $(item).outerHeight(true);
					$(item).animate("top: '" + dropPosition + "px', opacity: 1", 1000, new Function() {
						@Override
						public void f() {
							for (int i = 0; i < visibleItems.size(); i++) {
								final ExploreItem visibleItem = visibleItems.get(i);
								// Too many items on screen, remove last one
								if (visibleItems.size() > 5 && i == 0) {
									final String topCss = "top: '" + String.valueOf(Integer.parseInt($(visibleItem).css("top").replace("px", "")) + $(item).outerHeight(true) * 3) + "px'";
									$(visibleItem).animate(topCss + ", opacity: 0", 1000, new Function() {
										@Override
										public void f() {
											// Remove the item
											visibleItems.remove(visibleItem);
											exploreContainer.removeChild(visibleItem.getElement());
										}
									});
								}
								// Otherwise due a normal scroll
								else {
									final String topCss = "top: '" + String.valueOf(Integer.parseInt($(visibleItem).css("top").replace("px", "")) + $(item).outerHeight(true)) + "px'";
									$(visibleItem).animate(topCss, 1000);
								}
							}
						}
					});
				}
			});
			delay += 2000;
			index++;
		}

	}
}
