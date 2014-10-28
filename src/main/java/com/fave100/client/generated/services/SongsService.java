/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU
* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package com.fave100.client.generated.services;

import com.fave100.client.generated.entities.UserListResultCollection;
import com.gwtplatform.dispatch.rest.shared.RestService;
import javax.ws.rs.PathParam;
import com.fave100.client.generated.entities.WhylineCollection;
import javax.ws.rs.QueryParam;
import com.fave100.client.generated.entities.FaveItem;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import com.gwtplatform.dispatch.rest.shared.RestAction;

@Path("/")
public interface SongsService extends RestService {

    @GET
    @Path("/songs/{id}/whylines")
    public RestAction<WhylineCollection> getWhylines (@PathParam("id") String id);

    @GET
    @Path("/songs/{id}")
    public RestAction<FaveItem> getSong (@PathParam("id") String id);

    @GET
    @Path("/songs/{id}/favelists")
    public RestAction<UserListResultCollection> getFaveLists (@PathParam("id") String id);

}