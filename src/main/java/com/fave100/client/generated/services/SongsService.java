/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU
* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package com.fave100.client.generated.services;

import com.fave100.client.generated.entities.FaveItem;
import com.fave100.client.generated.entities.UserListResultCollection;
import com.fave100.client.generated.entities.WhylineCollection;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/")
public interface SongsService {

    @GET
    @Path("/songs/{id}")
    public RestAction<FaveItem> getSong (@PathParam("id") String id);

    @GET
    @Path("/songs/{id}/whylines")
    public RestAction<WhylineCollection> getWhylines (@PathParam("id") String id);

    @GET
    @Path("/songs/{id}/favelists")
    public RestAction<UserListResultCollection> getFaveLists (@PathParam("id") String id);

}