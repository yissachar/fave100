package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.Test;

import com.fave100.server.TestHelper;
import com.fave100.server.domain.FeaturedLists;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.shared.Constants;

public class AdminTest extends ApiTest {

	@Test
	public void user_api_should_allow_admin_user_to_create_admin_user() {
		AppUser existingAdmin = new AppUser("admin");
		existingAdmin.setAdmin(true);

		String username = "tobe";
		AppUser newAdmin = new AppUser(username);
		ofy().save().entity(newAdmin).now();

		UserApi.createAdmin(existingAdmin, username);
		assertThat(newAdmin.isAdmin());
	}

	@Test
	public void user_api_should_not_allow_non_admin_user_to_create_admin_user() {
		AppUser nonAdmin = new AppUser("nonAdmin");

		String username = "yarb";
		AppUser newAdmin = new AppUser(username);
		ofy().save().entity(newAdmin).now();

		try {
			UserApi.createAdmin(nonAdmin, username);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void user_api_should_allow_admin_user_to_remove_admin_user() {
		AppUser existingAdmin = new AppUser("admin");
		existingAdmin.setAdmin(true);

		String username = "ramove";
		AppUser admin = new AppUser(username);
		admin.setAdmin(true);
		ofy().save().entity(admin).now();

		UserApi.removeAdmin(existingAdmin, username);
		assertThat(admin.isAdmin());
	}

	@Test
	public void user_api_should_not_allow_non_admin_user_to_remove_admin_user() {
		AppUser nonAdmin = new AppUser("nonAdmin");

		String username = "ramove";
		AppUser admin = new AppUser(username);
		admin.setAdmin(true);
		ofy().save().entity(admin).now();

		try {
			UserApi.removeAdmin(nonAdmin, username);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void user_api_should_allow_admin_user_to_create_critic_user() {
		AppUser existingAdmin = new AppUser("admin");
		existingAdmin.setAdmin(true);

		String username = "critic";
		AppUser newCritic = new AppUser(username);
		ofy().save().entity(newCritic).now();

		UserApi.createCritic(existingAdmin, username);
		assertThat(newCritic.isCritic());
	}

	@Test
	public void user_api_should_not_allow_non_admin_user_to_create_critic_user() {
		AppUser nonAdmin = new AppUser("nonAdmin");

		String username = "critic";
		AppUser newCritic = new AppUser(username);
		ofy().save().entity(newCritic).now();

		try {
			UserApi.createCritic(nonAdmin, username);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void user_api_should_allow_admin_user_to_remove_critic_user() {
		AppUser existingAdmin = new AppUser("admin");
		existingAdmin.setAdmin(true);

		String username = "ramove";
		AppUser critic = new AppUser(username);
		critic.setCritic(true);
		ofy().save().entity(critic).now();

		UserApi.removeCritic(existingAdmin, username);
		assertThat(!critic.isCritic());
	}

	@Test
	public void user_api_should_not_allow_non_admin_user_to_remove_critic_user() {
		AppUser nonAdmin = new AppUser("nonAdmin");

		String username = "ramove";
		AppUser critic = new AppUser(username);
		critic.setCritic(true);
		ofy().save().entity(critic).now();

		try {
			UserApi.removeCritic(nonAdmin, username);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void favelists_api_should_allow_admin_user_to_add_featured_list() {
		AppUser existingAdmin = new AppUser("admin");
		existingAdmin.setAdmin(true);

		String list = "asdf";
		FaveListsApi.addFeaturedList(existingAdmin, list);

		List<String> featuredLists = ofy().load().type(FeaturedLists.class).id(Constants.FEATURED_LISTS_ID).now().getLists();
		assertThat(featuredLists).contains(list);
	}

	@Test
	public void favelists_api_should_not_allow_non_admin_user_to_add_featured_list() {
		AppUser nonAdmin = new AppUser("notAnAdmin");

		try {
			FaveListsApi.addFeaturedList(nonAdmin, "mheag");
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void favelists_api_should_allow_admin_user_to_remove_featured_list() {
		AppUser existingAdmin = new AppUser("admin");
		existingAdmin.setAdmin(true);

		FeaturedLists featuredListsEntity = new FeaturedLists(Constants.FEATURED_LISTS_ID);
		ofy().save().entity(featuredListsEntity).now();

		String list = "asdf";
		FaveListsApi.removeFeaturedList(existingAdmin, list);

		List<String> featuredLists = ofy().load().type(FeaturedLists.class).id(Constants.FEATURED_LISTS_ID).now().getLists();
		assertThat(featuredLists).doesNotContain(list);
	}

	@Test
	public void favelists_api_should_not_allow_non_admin_user_to_remove_featured_list() {
		AppUser nonAdmin = new AppUser("notAnAdmin");

		try {
			FaveListsApi.removeFeaturedList(nonAdmin, "mheag");
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void favelists_api_should_allow_admin_user_to_retrieve_featured_lists() {
		AppUser existingAdmin = new AppUser("admin");
		existingAdmin.setAdmin(true);

		List<String> listNames = new ArrayList<>();
		listNames.add("yert");
		listNames.add("haka");
		listNames.add("bork");
		listNames.add("leni");

		FeaturedLists featuredListsEntity = new FeaturedLists(Constants.FEATURED_LISTS_ID);
		featuredListsEntity.setLists(listNames);
		ofy().save().entity(featuredListsEntity).now();

		FaveListsApi.getFeaturedLists(existingAdmin);

		List<String> featuredLists = FaveListsApi.getFeaturedLists(existingAdmin).getLists();
		assertThat(featuredLists).containsAll(listNames);
	}

	@Test
	public void favelists_api_should_not_allow_non_admin_user_to_retrieve_featured_list() {
		AppUser nonAdmin = new AppUser("notAnAdmin");

		FeaturedLists featuredListsEntity = new FeaturedLists(Constants.FEATURED_LISTS_ID);
		ofy().save().entity(featuredListsEntity).now();

		try {
			FaveListsApi.getFeaturedLists(nonAdmin);
			fail(TestHelper.SHOULD_THROW_EXCEPTION_MSG);
		}
		catch (WebApplicationException e) {
			assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.FORBIDDEN.getStatusCode());
		}
	}
}
