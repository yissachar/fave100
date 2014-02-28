package com.fave100.server.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fave100.server.SessionHelper;
import com.fave100.server.domain.ApiPaths;
import com.fave100.server.domain.BooleanResult;
import com.fave100.server.domain.Session;
import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.server.domain.appuser.Following;
import com.fave100.server.domain.appuser.FollowingResult;
import com.fave100.server.exceptions.NotLoggedInException;
import com.fave100.shared.Constants;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Ref;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/" + ApiPaths.USERS_ROOT)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/" + ApiPaths.USERS_ROOT, description = "Operations on Users")
public class UsersApi {

	@GET
	@Path(ApiPaths.GET_APPUSER)
	@ApiOperation(value = "Find a user by their username", response = AppUser.class)
	@ApiResponses(value = {@ApiResponse(code = 404, message = ApiExceptions.USER_NOT_FOUND)})
	public static AppUser getAppUser(@ApiParam(value = "The username", required = true) @PathParam("username") final String username) {
		AppUser appUser = ofy().load().type(AppUser.class).id(username.toLowerCase()).get();
		if (appUser == null)
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(ApiExceptions.USER_NOT_FOUND).build());

		return appUser;
	}

	/**
	 * Returns 5 following users from the given index
	 * 
	 * @param username
	 * @param index
	 * @return
	 * @throws UnauthorizedException
	 * @throws NotFoundException
	 * @throws ForbiddenException
	 */
	@GET
	@Path(ApiPaths.GET_FOLLOWING)
	@ApiOperation(value = "Get following", response = FollowingResult.class)
	public static FollowingResult getFollowing(
			@Context HttpServletRequest request,
			@QueryParam("username") final String username,
			@QueryParam("index") final int index) {

		// Only logged in users can see following		
		final AppUser currentUser = UserApi.getLoggedInUser(request);
		if (currentUser == null)
			throw new NotLoggedInException();

		final AppUser user = getAppUser(username);
		if (user == null)
			throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("User does not exist").build());

		if (user.isFollowingPrivate() && !user.getId().equals(currentUser.getId()))
			throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).entity("List is private").build());

		final Following following = ofy().load().type(Following.class).id(username.toLowerCase()).get();
		if (following != null && following.getFollowing() != null) {
			List<Ref<AppUser>> users = following.getFollowing();
			users = users.subList(index, Math.min(index + Constants.MORE_FOLLOWING_INC, following.getFollowing().size()));
			final boolean moreFollowing = (following.getFollowing().size() - index - users.size()) > 0;
			return new FollowingResult(new ArrayList<AppUser>(ofy().load().refs(users).values()), moreFollowing);
		}

		return new FollowingResult(new ArrayList<AppUser>(), false);
	}

	/*
	 * Checks if the user is logged into Google (though not necessarily logged into Fave100)
	 */
	@GET
	@Path(ApiPaths.IS_GOOGLE_LOGGED_IN)
	@ApiOperation(value = "Is google user logged in", response = BooleanResult.class)
	public static BooleanResult isGoogleUserLoggedIn() {
		final UserService userService = UserServiceFactory.getUserService();
		final User user = userService.getCurrentUser();
		return new BooleanResult(user != null);
	}

	// Check if Fave100 user is logged in 
	@GET
	@Path(ApiPaths.IS_APPUSER_LOGGED_IN)
	@ApiOperation(value = "Is app user logged in", response = BooleanResult.class)
	public static BooleanResult isAppUserLoggedIn(@Context HttpServletRequest request) {
		Session session = SessionHelper.getSession(request);

		final String username = (String)session.getAttribute(AppUserDao.AUTH_USER);
		return new BooleanResult(username != null);
	}
}
