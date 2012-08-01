package com.fave100.client.widgets;

import com.fave100.client.requestfactory.FaveItemProxy;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;

/**
 * DataGrid to display a list of FaveItems.
 * @author yissachar.radcliffe
 *
 */
public class FaveDataGrid extends DataGrid<FaveItemProxy>{
	
	// DataGrid StyleSheet override
	public interface DataGridResource extends DataGrid.Resources {
		@Source({ DataGrid.Style.DEFAULT_CSS, "DataGridOverride.css" })
		DataGrid.Style dataGridStyle();
	};
		
	public FaveDataGrid() {		
		super(0, (DataGridResource) GWT.create(DataGridResource.class));
		
		// Track name Column
		SafeHtmlCell linkCell = new SafeHtmlCell();
		Column<FaveItemProxy, SafeHtml> trackNameColumn = new Column<FaveItemProxy, SafeHtml>(linkCell) {
			@Override
			public SafeHtml getValue(FaveItemProxy faveItem) {
				// Create a link to the item in iTunes
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				if(faveItem.getTrackViewUrl() != null && faveItem.getTrackViewUrl() != "") {
					sb.appendHtmlConstant("<a href='"+faveItem.getTrackViewUrl()+"'>"+faveItem.getTrackName()+"</a>");
				} else if(faveItem.getTrackName() != null){
					sb.appendHtmlConstant(faveItem.getTrackName());
				}
				return sb.toSafeHtml();
			}
		};
		trackNameColumn.setCellStyleNames("trackNameColumn");
		this.addColumn(trackNameColumn, "Name");
		
		// Artist Column
		TextColumn<FaveItemProxy> artistColumn = new TextColumn<FaveItemProxy>() {
			@Override
			public String getValue(FaveItemProxy object) {
				return object.getArtistName();
			}
		};
		artistColumn.setCellStyleNames("artistColumn");
		this.addColumn(artistColumn, "Artist");
		
		// Year Column
		TextColumn<FaveItemProxy> yearColumn = new TextColumn<FaveItemProxy>() {
			@Override
			public String getValue(FaveItemProxy object) {
				if(object.getReleaseYear() != null) {
					return object.getReleaseYear();
				} else {
					return null;
				}
			}
		};
		yearColumn.setCellStyleNames("yearColumn");
		this.addColumn(yearColumn, "Year");	
	}
	
	public void resizeFaveList() {
		// Manually go through all row elements and set the height of the table
		// because DataGrid does not resize automatically
		int tableSize = 0;
		for(int i = 0; i < getRowCount(); i++) {
			//extra pixels because of border+padding
			tableSize += getRowElement(i).getClientHeight()+5;
		}
		int minSize = 120;
		if(tableSize < minSize) {
			// Force a minimum size
			setHeight(minSize+"px");
		} else {
			// Add a couple of extra pixels for good measure
			setHeight(tableSize+20+"px");
		}
	}
}
