package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.fave100.server.TestHelper;
import com.fave100.server.domain.StringResult;
import com.fave100.server.domain.UserRegistration;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.favelist.FaveItem;
import com.fave100.server.domain.favelist.Hashtag;
import com.fave100.server.servlets.HashtagBuilderServlet;

public class FaveListsApiTest extends ApiTest {

	@Before
	@Override
	public void setUp() {
		// Ensure queries will show up immediately
		setDefaultHighRepJobPolicyUnappliedJobPercentage((float)0.01);
		super.setUp();
	}

	@Test
	public void favelists_api_should_build_master_list() throws ServletException, IOException {
		HttpServletRequest req = TestHelper.newReq();
		AppUser user = AuthApi.createAppUser(req, new UserRegistration("dummy", "great-security", "faveman@gmail.com"));

		String listName = "grokky";
		String songId = "rejXKg";
		UserApi.addFaveListForCurrentUser(user, listName);
		UserApi.addFaveItemForCurrentUser(user, listName, songId);

		HashtagBuilderServlet servlet = new HashtagBuilderServlet();
		when(req.getParameter(HashtagBuilderServlet.HASHTAG_PARAM)).thenReturn(listName);
		servlet.doPost(req, mock(HttpServletResponse.class));

		List<FaveItem> faveItems = FaveListsApi.getMasterFaveList(listName).getItems();
		assertEquals("Master list must contain only one entity", 1, faveItems.size());
		assertEquals("Master list must contain only the proper fave item", songId, faveItems.get(0).getId());
	}

	@Test
	public void favelists_api_should_get_master_list() {
		String listName = "hababi";
		String songId = "radSongId";
		Hashtag hashtag = new Hashtag(listName, "Joan d'Arc");

		List<FaveItem> faveItems = new ArrayList<>();
		faveItems.add(new FaveItem("This", "be", songId));
		hashtag.setList(faveItems);
		ofy().save().entity(hashtag).now();

		List<FaveItem> masterList = FaveListsApi.getMasterFaveList(listName).getItems();
		assertEquals("Master list must contain only one entity", 1, masterList.size());
		assertEquals("Master list must contain only the proper fave item", songId, masterList.get(0).getId());
	}

	@Test
	public void favelists_api_should_get_all_list_names_with_at_least_ten_items() {
		String list1 = "list1";
		String list2 = "list2";
		Hashtag hashtag1 = new Hashtag(list1, "whocares");
		Hashtag hashtag2 = new Hashtag(list2, "notme");

		List<FaveItem> faveItems = new ArrayList<>();
		for (int i = 0; i < FaveListsApi.QUALITY_LIST_SIZE; i++) {
			faveItems.add(new FaveItem("really", "doesn't", "matter"));
		}
		hashtag1.setList(faveItems);
		hashtag2.setList(faveItems);
		ofy().save().entities(hashtag1, hashtag2).now();

		List<StringResult> listNames = FaveListsApi.getListNames().getItems();
		assertEquals("List names must include 2 list names", 2, listNames.size());
		assertEquals("List names must include list1", list1, listNames.get(0).getValue());
		assertEquals("List names must include list2", list2, listNames.get(1).getValue());

	}

	@Test
	public void favelists_api_should_not_get_any_list_names_with_less_than_ten_items() {
		String listName = "list";
		Hashtag hashtag = new Hashtag(listName, "Raggaz");

		List<FaveItem> faveItems = new ArrayList<>();
		for (int i = 0; i < FaveListsApi.QUALITY_LIST_SIZE - 1; i++) {
			faveItems.add(new FaveItem("just", "under", "limit"));
		}
		hashtag.setList(faveItems);
		ofy().save().entity(hashtag).now();

		List<StringResult> listNames = FaveListsApi.getListNames().getItems();
		assertEquals(0, listNames.size());
	}

}