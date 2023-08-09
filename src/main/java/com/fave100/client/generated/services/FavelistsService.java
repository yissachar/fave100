/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU
* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package com.fave100.client.generated.services;

import com.fave100.client.generated.entities.BooleanResult;
import com.fave100.client.generated.entities.FaveItemCollection;
import com.fave100.client.generated.entities.FeaturedLists;
import com.fave100.client.generated.entities.StringResultCollection;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import javax.ws.rs.*;

@Path("/")
public interface FavelistsService {

    @GET
    @Path("/favelists/names")
    public RestAction<StringResultCollection> getListNames ();

    @DELETE
    @Path("/favelists/featured/{list}")
    public RestAction<Void> removeFeaturedList (@PathParam("list") String list);

    @POST
    @Path("/favelists/featured/{list}")
    public RestAction<Void> addFeaturedList (@PathParam("list") String list);

    @GET
    @Path("/favelists/list/{list}")
    public RestAction<FaveItemCollection> getMasterFaveList (@PathParam("list") String list, @QueryParam("mode") String mode);

    @GET
    @Path("/favelists/featured")
    public RestAction<FeaturedLists> getFeaturedLists ();

    @POST
    @Path("/favelists/featured")
    public RestAction<Void> setFeaturedFavelistsRandomized (BooleanResult body);

    @GET
    @Path("/favelists/list/{list}/modes")
    public RestAction<StringResultCollection> getMasterFaveListModes (@PathParam("list") String list);

}