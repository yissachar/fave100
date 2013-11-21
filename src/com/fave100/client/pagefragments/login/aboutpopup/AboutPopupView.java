package com.fave100.client.pagefragments.login.aboutpopup;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

public class AboutPopupView extends PopupViewWithUiHandlers<AboutPopupUiHandlers> implements AboutPopupPresenter.MyView {
	public interface Binder extends UiBinder<PopupPanel, AboutPopupView> {
	}

	@UiField FocusPanel lightBoxBackground;
	@UiField HTMLPanel slide1;
	@UiField HTMLPanel slide2;
	@UiField HTMLPanel slide3;
	@UiField HTMLPanel slide4;
	@UiField HTMLPanel slide5;
	@UiField Image leftArrow;
	@UiField Image rightArrow;
	@UiField InlineLabel pageNumLabel;
	private List<HTMLPanel> slides = new ArrayList<HTMLPanel>();
	private int pageNum = 0;

	@Inject
	AboutPopupView(Binder uiBinder, EventBus eventBus) {
		super(eventBus);

		initWidget(uiBinder.createAndBindUi(this));
		slides.add(slide1);
		slides.add(slide2);
		slides.add(slide3);
		slides.add(slide4);
		slides.add(slide5);

		setPageNum();

	}

	@UiHandler("lightBoxBackground")
	void onBackgroundClick(ClickEvent event) {
		pageNum = 0;
		setPageNum();
		hide();
	}

	@UiHandler("leftArrow")
	void onLeftArrowClick(ClickEvent event) {
		if (pageNum > 0) {
			pageNum--;
			setPageNum();
		}
	}

	@UiHandler("rightArrow")
	void onRightArrowClick(ClickEvent event) {
		if (pageNum < slides.size() - 1) {
			pageNum++;
			setPageNum();
		}
	}

	void setPageNum() {
		for (HTMLPanel slide : slides) {
			slide.setVisible(false);
		}
		slides.get(pageNum).setVisible(true);

		leftArrow.getElement().getStyle().setVisibility(Visibility.VISIBLE);
		rightArrow.getElement().getStyle().setVisibility(Visibility.VISIBLE);

		if (pageNum == 0)
			leftArrow.getElement().getStyle().setVisibility(Visibility.HIDDEN);

		if (pageNum == slides.size() - 1)
			rightArrow.getElement().getStyle().setVisibility(Visibility.HIDDEN);

		pageNumLabel.setText((pageNum + 1) + "/" + slides.size());
	}
}
