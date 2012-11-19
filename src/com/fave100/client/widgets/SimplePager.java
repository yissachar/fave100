package com.fave100.client.widgets;

import com.fave100.client.events.ResultPageChangedEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
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
	@UiField Label pageNumLabel;

	public SimplePager(final EventBus eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
		setPageNumber(1);
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

		final int lastPageNum = pageNumber;

		if(number > maxPageNumber) {
			pageNumber = maxPageNumber;
		} else if (number < 1) {
			pageNumber = 1;
		} else {
			pageNumber = number;
		}

		if(lastPageNum != pageNumber) {
			eventBus.fireEvent(new ResultPageChangedEvent(pageNumber));
		}

		previousLink.setVisible(true);
		nextLink.setVisible(true);
		if(pageNumber == 1) {
			previousLink.setVisible(false);
		}

		if(pageNumber == maxPageNumber) {
			nextLink.setVisible(false);
		}

		pageNumLabel.setText(String.valueOf(pageNumber));
	}

	public int getMaxPageNumber() {
		return maxPageNumber;
	}

	public void setMaxPageNumber(final int number) {
		maxPageNumber = number;
		// Recall set page number to trigger the proper showing of next/previous
		setPageNumber(pageNumber);
	}

}
