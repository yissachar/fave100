package com.fave100.client.widgets;

import com.fave100.client.events.ResultPageChangedEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SimplePager extends Composite {

	private static SimplePagerUiBinder uiBinder = GWT
			.create(SimplePagerUiBinder.class);

	interface SimplePagerUiBinder extends UiBinder<Widget, SimplePager> {
	}

	private int pageNumber = 1;
	private int maxPageNumber = 1;
	private EventBus eventBus;
	@UiField Anchor previousLink;
	@UiField Anchor nextLink;
	@UiField HTMLPanel pageLinks;

	public SimplePager(final EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
		setPageNumber(1, false);
	}

	@UiHandler("previousLink")
	void onPreviousClick(final ClickEvent event) {
		setPageNumber(pageNumber - 1);
	}

	@UiHandler("nextLink")
	void onNextClick(final ClickEvent event) {
		setPageNumber(pageNumber + 1);
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(final int number) {
		setPageNumber(number, true);
	}

	public void setPageNumber(final int number, final boolean dispatch) {

		final int lastPageNum = pageNumber;

		if (number > maxPageNumber) {
			pageNumber = maxPageNumber;
		}
		else if (number < 1) {
			pageNumber = 1;
		}
		else {
			pageNumber = number;
		}

		if (lastPageNum != pageNumber && dispatch == true) {
			eventBus.fireEvent(new ResultPageChangedEvent(pageNumber));
		}

		previousLink.setVisible(true);
		nextLink.setVisible(true);
		if (pageNumber == 1) {
			previousLink.setVisible(false);
		}

		if (pageNumber == maxPageNumber) {
			nextLink.setVisible(false);
		}

		// Remove all pageLink children
		for (int i = pageLinks.getWidgetCount(); i > 0; i--) {
			pageLinks.remove(i - 1);
		}

		// How many page links will we show
		final int maxPageLinks = 10;
		final int numPageLinks = Math.min(maxPageNumber, maxPageLinks);
		int pageLinksRemaining = numPageLinks;
		final int pagesBeforeCurrent = (maxPageNumber > maxPageLinks) ? numPageLinks / 2 : getPageNumber() - 1;
		// Create page links less than current page
		for (int i = getPageNumber() - pagesBeforeCurrent; i < getPageNumber(); i++) {
			if (i <= 0)
				continue;
			createPageLink(String.valueOf(i));
			pageLinksRemaining--;
		}
		// Create current page
		final Label pageNumElement = new Label();
		pageNumElement.setText(String.valueOf(getPageNumber()));
		pageLinks.add(pageNumElement);
		pageLinksRemaining--;
		// Create page links after current page
		for (int i = 1; i <= pageLinksRemaining; i++) {
			createPageLink(String.valueOf(getPageNumber() + i));
		}
	}

	public int getMaxPageNumber() {
		return maxPageNumber;
	}

	public void setMaxPageNumber(final int number) {
		if (number <= 0)
			return;
		maxPageNumber = number;
		// Re-call set page number to trigger the proper showing of next/previous
		setPageNumber(pageNumber, false);
	}

	private void createPageLink(final String pageNum) {
		final Anchor pageLink = new Anchor();
		pageLink.setText(pageNum);
		pageLinks.add(pageLink);
		pageLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				setPageNumber(Integer.parseInt(pageLink.getText()));
			}
		});
	}

}
