/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU
* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package com.fave100.client.generated.services;

import com.gwtplatform.dispatch.rest.shared.RestService;
import javax.ws.rs.PathParam;
import com.fave100.client.generated.entities.StringResultCollection;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

@Path("/")
public interface TrendingService extends RestService {

    @GET
    @Path("/trending/favelists")
    public RestAction<StringResultCollection> getTrendingFaveLists ();

}