package com.fave100.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListItem;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.HTMLPanel;

public class FaveListBase extends HTMLPanel{
	
	protected List<HasCell<FaveListItem, ?>> cells = new ArrayList<HasCell<FaveListItem,?>>();
	private CellList<FaveListItem> cellList;
	
	public FaveListBase(final ApplicationRequestFactory requestFactory) {
		super("");
		
		cells.add(new HasCell<FaveListItem, SafeHtml>() {
			private SafeHtmlCell cell = new SafeHtmlCell();

            @Override
            public SafeHtmlCell getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<FaveListItem, SafeHtml> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SafeHtml getValue(FaveListItem object) {
				SafeHtmlBuilder builder = new SafeHtmlBuilder();
				builder.appendHtmlConstant("<a href='"+object.getTrackViewUrl()+"'>"+object.getTrackName()+"</a>");
				return builder.toSafeHtml();
			}			
		});
		cells.add(new HasCell<FaveListItem, SafeHtml>() {
			private SafeHtmlCell cell = new SafeHtmlCell();

            @Override
            public Cell<SafeHtml> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<FaveListItem, SafeHtml> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SafeHtml getValue(FaveListItem object) {
				return SafeHtmlUtils.fromString(object.getArtistName());
			}
		});
		cells.add(new HasCell<FaveListItem, SafeHtml>() {
			private SafeHtmlCell cell = new SafeHtmlCell();

            @Override
            public Cell<SafeHtml> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<FaveListItem, SafeHtml> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SafeHtml getValue(FaveListItem object) {				
				return SafeHtmlUtils.fromString(object.getReleaseYear());
			}
		});
		
		createCellList();
		
		this.setStyleName("faveList");
	}
	
	public void createCellList() {
		CompositeCell<FaveListItem> cell = new CompositeCell<FaveListItem>(cells);
		this.clear();
		cellList = new CellList<FaveListItem>(cell);
		this.add(cellList);
	}
	
	public void setRowData(List<? extends FaveListItem> data) {
		cellList.setRowData(data);
	}
}
