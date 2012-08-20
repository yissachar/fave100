package com.fave100.client.widgets;

import com.fave100.client.requestfactory.FaveListItem;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class FaveItemCell extends AbstractCell<FaveListItem>{
	
	public FaveItemCell() {
		
	}
	
	@Override
	public void render(Context context, FaveListItem faveItemProxy, SafeHtmlBuilder builder) {
		if(faveItemProxy == null) {
			return;
		}
		
		builder.appendHtmlConstant("<p>");		
		builder.appendEscaped(faveItemProxy.getTrackName());
		//builder.appendEscaped("asdfasdfasdf235");
		builder.appendHtmlConstant("</p>");
	}

}
