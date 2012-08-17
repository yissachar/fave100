package com.fave100.client.widgets;

import static com.google.gwt.query.client.GQuery.$;

import com.fave100.client.requestfactory.FaveListItem;
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
public class FaveDataGridBase extends DataGrid<FaveListItem>{
	
	// DataGrid StyleSheet override
	public interface DataGridResource extends DataGrid.Resources {
		@Source({ DataGrid.Style.DEFAULT_CSS, "DataGridOverride.css" })
		DataGrid.Style dataGridStyle();
	};
		
	public FaveDataGridBase() {		
		super(0, (DataGridResource) GWT.create(DataGridResource.class));
		
		// Track name Column
		SafeHtmlCell linkCell = new SafeHtmlCell();
		Column<FaveListItem, SafeHtml> trackNameColumn = new Column<FaveListItem, SafeHtml>(linkCell) {
			@Override
			public SafeHtml getValue(FaveListItem faveItem) {
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
		TextColumn<FaveListItem> artistColumn = new TextColumn<FaveListItem>() {
			@Override
			public String getValue(FaveListItem object) {
				return object.getArtistName();
			}
		};
		artistColumn.setCellStyleNames("artistColumn");
		this.addColumn(artistColumn, "Artist");
		
		// Year Column
		TextColumn<FaveListItem> yearColumn = new TextColumn<FaveListItem>() {
			@Override
			public String getValue(FaveListItem object) {
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
		// TODO: Need a better resizing solution, this is not foolproof
		// Manually go through all row elements and set the height of the table
		// because DataGrid does not resize automatically
		int tableSize = 0;
		for(int i = 0; i < getRowCount(); i++) {
			//extra pixels because of border+padding
			//Element elem = getRowElement(i);
			//tableSize += elem.getClientHeight()+5;
			tableSize += $(getRowElement(i)).outerHeight(true);
		}
		tableSize += $(getTableHeadElement()).outerHeight(true);
		// Some breathing room, just in case
		tableSize += 5;
		setHeight(tableSize+"px");
	}
}
