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
import com.gwtplatform.dispatch.rest.shared.RestService;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import com.fave100.client.generated.entities.StringResultCollection;
import com.fave100.client.generated.entities.FaveItemCollection;
import javax.ws.rs.HEAD;
import javax.ws.rs.DELETE;
import com.gwtplatform.dispatch.rest.shared.RestAction;

@Path("/")
public interface FavelistsService extends RestService {

    @GET
    @Path("/favelists/list/{list}")
    public RestAction<FaveItemCollection> getMasterFaveList (@PathParam("list") String list, @QueryParam("mode") String mode);

    @HEAD
    @Path("/favelists/list/{list}")
    public RestAction<Void> checkMasterFaveListExistence (@PathParam("list") String list, @QueryParam("mode") String mode);

    @DELETE
    @Path("/favelists/featured/{list}")
    public RestAction<Void> removeFeaturedList (@PathParam("list") String list);

    @POST
    @Path("/favelists/featured/{list}")
    public RestAction<Void> addFeaturedList (@PathParam("list") String list);

    @GET
    @Path("/favelists/names")
    public RestAction<StringResultCollection> getListNames ();

    @GET
    @Path("/favelists/featured")
    public RestAction<StringResultCollection> getFeaturedLists ();

}