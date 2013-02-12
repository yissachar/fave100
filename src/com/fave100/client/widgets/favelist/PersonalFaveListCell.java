package com.fave100.client.widgets.favelist;

import com.fave100.client.requestfactory.ApplicationRequestFactory;
import com.fave100.client.requestfactory.FaveItemProxy;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class PersonalFaveListCell extends FaveListCellBase{

	public PersonalFaveListCell(final ApplicationRequestFactory requestFactory) {
		super(requestFactory);
	}

	@Override
	public void render(final Context context, final FaveItemProxy object,
			final SafeHtmlBuilder sb) {
		if(object == null) return;

		sb.appendHtmlConstant(getRank(context));
		sb.appendHtmlConstant(getTitle(object));
		sb.appendHtmlConstant(getArtist(object));
		//sb.appendHtmlConstant(getReleaseDate(object));

		final String deleteButton = "<button class='faveListDeleteButton'>X</button>";
		sb.appendHtmlConstant(deleteButton);

		//sb.appendHtmlConstant(getImageThumb(object));
	}
}
