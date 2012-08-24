package com.fave100.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.SongProxy;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class FaveListBase extends HTMLPanel{
	
	protected List<HasCell<SongProxy, ?>> _cells = new ArrayList<HasCell<SongProxy,?>>();
	protected CellList<SongProxy> _cellList;
	
	public FaveListBase(final ApplicationRequestFactory requestFactory) {
		super("");
		
		_cells.add(new HasCell<SongProxy, SafeHtml>() {
			private final SafeHtmlCell cell = new SafeHtmlCell() {
				 @Override
				  public void render(final Context context, final SafeHtml value, final SafeHtmlBuilder sb) {
				    if (value != null) {
				      sb.appendEscaped((context.getIndex()+1)+".");
				    }
				 }
			};
            @Override
            public SafeHtmlCell getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<SongProxy, SafeHtml> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SafeHtml getValue(final SongProxy object) {
				final Label label = new Label();
				label.setStyleName("faveListRank");
				return SafeHtmlUtils.fromTrustedString(label.toString());
			}			
		});
		_cells.add(new HasCell<SongProxy, SafeHtml>() {
			private final SafeHtmlCell cell = new SafeHtmlCell();
            @Override
            public SafeHtmlCell getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<SongProxy, SafeHtml> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SafeHtml getValue(final SongProxy object) {
				final Anchor anchor = new Anchor();
				anchor.setHref(object.getTrackViewUrl());
				anchor.setHTML(object.getTrackName());
				anchor.addStyleName("faveListTrackName");
				return SafeHtmlUtils.fromTrustedString(anchor.toString());
			}			
		});
		_cells.add(new HasCell<SongProxy, SafeHtml>() {
			private final SafeHtmlCell cell = new SafeHtmlCell();

            @Override
            public Cell<SafeHtml> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<SongProxy, SafeHtml> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SafeHtml getValue(final SongProxy object) {
				final Label label = new Label();
				label.setText(object.getArtistName());
				label.setStyleName("faveListArtistName");
				return SafeHtmlUtils.fromTrustedString(label.toString());
			}
		});
		_cells.add(new HasCell<SongProxy, SafeHtml>() {
			private final SafeHtmlCell cell = new SafeHtmlCell();

            @Override
            public Cell<SafeHtml> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<SongProxy, SafeHtml> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SafeHtml getValue(final SongProxy object) {
				final Label label = new Label();
				label.setText(object.getReleaseDate().substring(0, 4));
				label.setStyleName("faveListReleaseDate");
				return SafeHtmlUtils.fromTrustedString(label.toString());
			}
		});
		_cells.add(new HasCell<SongProxy, SafeHtml>() {
			private final SafeHtmlCell cell = new SafeHtmlCell();

            @Override
            public Cell<SafeHtml> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<SongProxy, SafeHtml> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SafeHtml getValue(final SongProxy object) {
				final Image image = new Image();
				image.setUrl(object.getArtworkUrl60());
				image.setStyleName("faveListImageThumb");
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
		final CompositeCell<SongProxy> cell = new CompositeCell<SongProxy>(_cells);
		clear();
		_cellList = new CellList<SongProxy>(cell);
		add(_cellList);
		if(!stylename.isEmpty()) {
			_cellList.getRowContainer().addClassName(stylename);
		}
	}
	
	public void setRowData(final List<SongProxy> data) {
		_cellList.setRowData(data);
	}
}
