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
import com.fave100.client.generated.entities.StringResultCollection;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

@Path("/")
public interface FavelistsService extends RestService {

    @GET
    @Path("/favelists/{list}")
    public RestAction<FaveItemCollection> getMasterFaveList (@PathParam("list") String list);

    @GET
    @Path("/favelists/names")
    public RestAction<StringResultCollection> getListNames ();

}