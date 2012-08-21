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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

public class FaveListBase extends HTMLPanel{
	
	protected List<HasCell<FaveListItem, ?>> _cells = new ArrayList<HasCell<FaveListItem,?>>();
	protected CellList<FaveListItem> _cellList;
	
	public FaveListBase(final ApplicationRequestFactory requestFactory) {
		super("");
		
		_cells.add(new HasCell<FaveListItem, SafeHtml>() {
			private final SafeHtmlCell cell = new SafeHtmlCell();
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
			public SafeHtml getValue(final FaveListItem object) {
				final Anchor anchor = new Anchor();
				anchor.setHref(object.getTrackViewUrl());
				anchor.setHTML(object.getTrackName());
				anchor.addStyleName("anchorCSS");
				return SafeHtmlUtils.fromTrustedString(anchor.toString());
			}			
		});
		_cells.add(new HasCell<FaveListItem, SafeHtml>() {
			private final SafeHtmlCell cell = new SafeHtmlCell();

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
			public SafeHtml getValue(final FaveListItem object) {
				return SafeHtmlUtils.fromString(object.getArtistName());
			}
		});
		_cells.add(new HasCell<FaveListItem, SafeHtml>() {
			private final SafeHtmlCell cell = new SafeHtmlCell();

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
			public SafeHtml getValue(final FaveListItem object) {				
				return SafeHtmlUtils.fromString(object.getReleaseYear());
			}
		});
		_cells.add(new HasCell<FaveListItem, SafeHtml>() {
			private final SafeHtmlCell cell = new SafeHtmlCell();

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
			public SafeHtml getValue(final FaveListItem object) {
				final Image image = new Image();
				image.setUrl(object.getArtworkUrl60());
				return SafeHtmlUtils.fromTrustedString(image.toString());
			}
		});
		
		createCellList();
		addStyleName("faveList");
	}
	
	public void createCellList() {
		createCellList("");
	}
	public void createCellList(final String stylename) {
		final CompositeCell<FaveListItem> cell = new CompositeCell<FaveListItem>(_cells);
		clear();
		_cellList = new CellList<FaveListItem>(cell);
		add(_cellList);
		if(!stylename.isEmpty()) {
			_cellList.getRowContainer().addClassName(stylename);
		}
	}
	
	public void setRowData(final List<? extends FaveListItem> data) {
		_cellList.setRowData(data);
	}
}
