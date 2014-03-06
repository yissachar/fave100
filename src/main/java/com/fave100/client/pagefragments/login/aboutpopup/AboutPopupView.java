package com.fave100.client.pagefragments.login.aboutpopup;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.resources.img.ImageResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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

	@UiField HTMLPanel slide1;
	@UiField HTMLPanel slide2;
	@UiField HTMLPanel slide3;
	@UiField HTMLPanel slide4;
	@UiField HTMLPanel slide5;
	@UiField Image imgSlide1;
	@UiField Image imgSlide2;
	@UiField Image imgSlide3;
	@UiField Image imgSlide4;
	@UiField Image imgSlide5;
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

	@Override
	public void loadImages() {
		// Code split About Screen images since they are usually not needed
		GWT.runAsync(new RunAsyncCallback() {

			@Override
			public void onSuccess() {
				imgSlide1.setResource(ImageResources.INSTANCE.aboutScreen1());
				imgSlide2.setResource(ImageResources.INSTANCE.aboutScreen2());
				imgSlide3.setResource(ImageResources.INSTANCE.aboutScreen3());
				imgSlide4.setResource(ImageResources.INSTANCE.aboutScreen4());
				imgSlide5.setResource(ImageResources.INSTANCE.aboutScreen5());
			}

			@Override
			public void onFailure(Throwable reason) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void resetPage() {
		pageNum = 0;
		setPageNum();
	}
}
