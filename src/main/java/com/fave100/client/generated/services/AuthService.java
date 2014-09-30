/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU
* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package com.fave100.client.generated.services;

import com.fave100.client.generated.entities.UserRegistration;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import com.fave100.client.generated.entities.FacebookRegistration;
import javax.ws.rs.Path;
import com.fave100.client.generated.entities.StringResult;
import com.fave100.client.generated.entities.TwitterRegistration;
import com.fave100.client.generated.entities.LoginCredentials;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.fave100.client.generated.entities.AppUser;
import com.gwtplatform.dispatch.rest.shared.RestService;
import javax.ws.rs.QueryParam;

@Path("/")
public interface AuthService extends RestService {

    @GET
    @Path("/auth/facebook/url")
    public RestAction<StringResult> getFacebookAuthUrl (@QueryParam("redirectUrl") String redirectUrl);

    @POST
    @Path("/auth/facebook/register")
    public RestAction<AppUser> createAppUserFromFacebookAccount (FacebookRegistration body);

    @POST
    @Path("/auth/google/register")
    public RestAction<AppUser> createAppUserFromGoogleAccount (StringResult body);

    @POST
    @Path("/auth/facebook/login")
    public RestAction<AppUser> loginWithFacebook (StringResult body);

    @POST
    @Path("/auth/twitter/register")
    public RestAction<AppUser> createAppUserFromTwitterAccount (TwitterRegistration body);

    @GET
    @Path("/auth/twitter/url")
    public RestAction<StringResult> getTwitterAuthUrl (@QueryParam("redirectUrl") String redirectUrl);

    @POST
    @Path("/auth/twitter/login")
    public RestAction<AppUser> loginWithTwitter (StringResult body);

    @POST
    @Path("/auth/google/login")
    public RestAction<AppUser> loginWithGoogle ();

    @POST
    @Path("/auth/login")
    public RestAction<AppUser> login (LoginCredentials body);

    @POST
    @Path("/auth/register")
    public RestAction<AppUser> createAppUser (UserRegistration body);

    @GET
    @Path("/auth/google/url")
    public RestAction<StringResult> getGoogleAuthUrl (@QueryParam("destinationURL") String destinationURL);

    @POST
    @Path("/auth/logout")
    public RestAction<Void> logout ();

}