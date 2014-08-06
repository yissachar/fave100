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
import com.fave100.client.generated.entities.CursoredSearchResult;
import com.fave100.client.generated.entities.YouTubeSearchResultCollection;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

@Path("/")
public interface SearchService extends RestService {

    @GET
    @Path("/search/youtube")
    public RestAction<YouTubeSearchResultCollection> getYouTubeResults (@QueryParam("song") String song, @QueryParam("artist") String artist);

    @GET
    @Path("/search/favelists/{search_term}")
    public RestAction<CursoredSearchResult> searchFaveLists (@QueryParam("search_term") String search_term, @QueryParam("cursor") String cursor);

    @GET
    @Path("/search/users/{search_term}")
    public RestAction<CursoredSearchResult> searchUsers (@QueryParam("search_term") String search_term, @QueryParam("cursor") String cursor);

}