/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU
* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package com.fave100.client.generated.services;

import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.FaveItemCollection;
import com.fave100.client.generated.entities.FollowingResult;
import com.fave100.client.generated.entities.StringResult;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import javax.ws.rs.*;

@Path("/")
public interface UsersService {

    @GET
    @Path("/users/{user}/favelists/{list}")
    public RestAction<FaveItemCollection> getFaveList (@PathParam("user") String user, @PathParam("list") String list);

    @GET
    @Path("/users/{user}/following")
    public RestAction<FollowingResult> getFollowing (@PathParam("user") String user, @QueryParam("index") int index);

    @GET
    @Path("/users/{user}")
    public RestAction<AppUser> getAppUser (@PathParam("user") String user);

    @POST
    @Path("/users/{user}/favelists/{list}/critic_url")
    public RestAction<Void> setCriticUrl (@PathParam("user") String user, @PathParam("list") String list, String body);

    @GET
    @Path("/users/{user}/favelists/{list}/critic_url")
    public RestAction<StringResult> getCriticUrl (@PathParam("user") String user, @PathParam("list") String list);

}