/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU
* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package com.fave100.client.generated.services;

import com.fave100.client.generated.entities.AppUserCollection;
import com.fave100.client.generated.entities.PasswordChangeDetails;
import com.fave100.client.generated.entities.BooleanResult;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import javax.ws.rs.GET;
import com.fave100.client.generated.entities.AppUser;
import com.fave100.client.generated.entities.EmailPasswordResetDetails;
import com.gwtplatform.dispatch.rest.shared.RestService;
import com.fave100.client.generated.entities.StringResult;
import javax.ws.rs.POST;
import com.fave100.client.generated.entities.UserInfo;
import javax.ws.rs.PathParam;
import com.fave100.client.generated.entities.WhylineEdit;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DELETE;

@Path("/")
public interface UserService extends RestService {

    @PUT
    @Path("/user/following/{user}")
    public RestAction<Void> followUser (@PathParam("user") String user);

    @GET
    @Path("/user/following/{user}")
    public RestAction<BooleanResult> isFollowing (@PathParam("user") String user);

    @DELETE
    @Path("/user/following/{user}")
    public RestAction<Void> unfollowUser (@PathParam("user") String user);

    @POST
    @Path("/user/admins/{user}")
    public RestAction<Void> createAdmin (@PathParam("user") String user);

    @DELETE
    @Path("/user/admins/{user}")
    public RestAction<Void> removeAdmin (@PathParam("user") String user);

    @GET
    @Path("/user/critics")
    public RestAction<AppUserCollection> getCritics ();

    @DELETE
    @Path("/user/critics/{user}")
    public RestAction<Void> removeCritic (@PathParam("user") String user);

    @POST
    @Path("/user/critics/{user}")
    public RestAction<Void> createCritic (@PathParam("user") String user);

    @GET
    @Path("/user/admins")
    public RestAction<AppUserCollection> getAdmins ();

    @POST
    @Path("/user/settings")
    public RestAction<BooleanResult> setUserInfo (UserInfo body);

    @GET
    @Path("/user/settings")
    public RestAction<UserInfo> getCurrentUserSettings ();

    @PUT
    @Path("/user/favelists/{list}/items/{id}")
    public RestAction<Void> addFaveItemForCurrentUser (@PathParam("list") String list, @PathParam("id") String id);

    @DELETE
    @Path("/user/favelists/{list}/items/{id}")
    public RestAction<Void> removeFaveItemForCurrentUser (@PathParam("list") String list, @PathParam("id") String id);

    @POST
    @Path("/user/password/reset")
    public RestAction<BooleanResult> emailPasswordResetToken (EmailPasswordResetDetails body);

    @POST
    @Path("/user/favelists/{list}/items/{id}/rank")
    public RestAction<Void> rerankFaveItemForCurrentUser (@PathParam("list") String list, @PathParam("id") String id, Integer body);

    @POST
    @Path("/user/password/change")
    public RestAction<BooleanResult> changePassword (PasswordChangeDetails body);

    @POST
    @Path("/user/favelists/{list}/items/{id}/whyline")
    public RestAction<Void> editWhylineForCurrentUser (WhylineEdit body);

    @GET
    @Path("/user")
    public RestAction<AppUser> getLoggedInUser ();

    @PUT
    @Path("/user/favelists/{list}")
    public RestAction<Void> addFaveListForCurrentUser (@PathParam("list") String list);

    @DELETE
    @Path("/user/favelists/{list}")
    public RestAction<Void> deleteFaveListForCurrentUser (@PathParam("list") String list);

    @POST
    @Path("/user/blobstore_url")
    public RestAction<StringResult> createBlobstoreUrl ();

}