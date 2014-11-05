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
import com.fave100.client.generated.entities.FeaturedLists;
import com.fave100.client.generated.entities.StringResultCollection;
import com.fave100.client.generated.entities.BooleanResult;
import javax.ws.rs.Path;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import javax.ws.rs.GET;
import com.fave100.client.generated.entities.AppUser;
import com.gwtplatform.dispatch.rest.shared.RestService;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DELETE;

@Path("/")
public interface FavelistsService extends RestService {

    @GET
    @Path("/favelists/list/{list}/modes")
    public RestAction<StringResultCollection> getMasterFaveListModes (@PathParam("list") String list);

    @GET
    @Path("/favelists/featured")
    public RestAction<FeaturedLists> getFeaturedLists ();

    @POST
    @Path("/favelists/featured")
    public RestAction<Void> setFeaturedFavelistsRandomized (BooleanResult body);

    @POST
    @Path("/favelists/featured/{list}")
    public RestAction<Void> addFeaturedList (@PathParam("list") String list);

    @DELETE
    @Path("/favelists/featured/{list}")
    public RestAction<Void> removeFeaturedList (@PathParam("list") String list);

    @GET
    @Path("/favelists/list/{list}")
    public RestAction<FaveItemCollection> getMasterFaveList (@PathParam("list") String list, @QueryParam("mode") String mode);

    @GET
    @Path("/favelists/names")
    public RestAction<StringResultCollection> getListNames ();

}