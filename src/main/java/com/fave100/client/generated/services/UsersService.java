/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU
* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package com.fave100.client.generated.services;

import com.fave100.client.generated.entities.FaveItemCollection;
import com.gwtplatform.dispatch.rest.shared.RestService;
import javax.ws.rs.PathParam;
import com.fave100.client.generated.entities.FollowingResult;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import com.fave100.client.generated.entities.AppUser;
import javax.ws.rs.QueryParam;

@Path("/")
public interface UsersService extends RestService {

    @GET
    @Path("/users/{user}")
    public RestAction<AppUser> getAppUser (@PathParam("user") String user);

    @GET
    @Path("/users/{user}/following")
    public RestAction<FollowingResult> getFollowing (@PathParam("user") String user, @QueryParam("index") int index);

    @GET
    @Path("/users/{user}/favelists/{list}")
    public RestAction<FaveItemCollection> getFaveList (@PathParam("user") String user, @PathParam("list") String list);

}