package com.fave100.client.pagefragments.popups.addsong;

import java.util.List;

import com.gwtplatform.mvp.client.UiHandlers;

public interface AddSongUiHandlers extends UiHandlers {
	void listsSelected(List<String> selectedLists);
}
