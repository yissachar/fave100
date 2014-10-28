package com.fave100.client.pages.admin;

import com.fave100.client.generated.entities.AppUser;
import com.gwtplatform.mvp.client.UiHandlers;

interface AdminUiHandlers extends UiHandlers {

	void showAddAdminPrompt();

	void showAddCriticPrompt();

	void showAddListPrompt();

	void removeAdmin(AppUser admin);

	void removeCritic(AppUser critic);

	void removeFeaturedList(String list);

	void setFeatureListsRandom(boolean random);
}