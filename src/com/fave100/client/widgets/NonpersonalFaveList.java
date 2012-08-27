package com.fave100.client.widgets;

import com.fave100.client.pagefragments.SideNotification;
import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveListRequest;
import com.fave100.client.requestfactory.SongProxy;
import com.fave100.server.domain.FaveList;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class NonpersonalFaveList extends FaveListBase{

	public NonpersonalFaveList(final ApplicationRequestFactory requestFactory) {
		super(requestFactory);
		
		// Add fave button
		_cells.add(4, new HasCell<SongProxy, SongProxy>() {
			private final ActionCell<SongProxy> cell = new ActionCell<SongProxy>("+", new Delegate<SongProxy>() {
				@Override
				public void execute(final SongProxy song) {
					final FaveListRequest faveListRequest = requestFactory.faveListRequest();
					final Request<Void> addFaveReq = faveListRequest.addFaveItemForCurrentUser(FaveList.DEFAULT_HASHTAG, song.getId(), song);
					addFaveReq.fire(new Receiver<Void>() {
						@Override
						public void onSuccess(final Void added) {
							SideNotification.show("Added!");												
						}
						
						@Override
						public void onFailure(final ServerFailure failure) {
							SideNotification.show(failure.getMessage().replace("Server Error:", ""), true);
						}
					});
				}
			});

            @Override
            public Cell<SongProxy> getCell() {
                return cell;
            }

			@Override
			public FieldUpdater<SongProxy, SongProxy> getFieldUpdater() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public SongProxy getValue(final SongProxy object) {
				return object;
			}			
		});
		
		// Whyline
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
				String whyline = object.getWhyline();
				if(whyline == null || whyline.isEmpty()) {
					whyline = "";
				}
				return SafeHtmlUtils.fromString(whyline);
			}
		});
		
		// Whyline score
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
				String score = object.getWhylineScore()+"";
				if(object.getWhylineScore() == 0) {
					score = "";
				}
				return SafeHtmlUtils.fromString(score);
			}
		});
		
		createCellList("nonpersonalFaves");
	}	
}
