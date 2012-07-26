package com.fave100.client.pages.myfave100;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class MouseDownCell extends AbstractCell<String>{

	public MouseDownCell() {
		super("mousedown");
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			String value, SafeHtmlBuilder sb) {
		if(value == null) return;
		
	    SafeHtml safeValue = SafeHtmlUtils.fromString(value);
	    sb.append(safeValue);		
	}
}
