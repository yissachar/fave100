/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU
* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package com.fave100.client.generated.services;

import com.fave100.client.generated.entities.*;
import com.gwtplatform.dispatch.rest.shared.RestAction;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/")
public interface AuthService {

    @POST
    @Path("/auth/logout")
    public RestAction<Void> logout ();

    @POST
    @Path("/auth/register")
    public RestAction<AppUser> createAppUser (UserRegistration body);

    @POST
    @Path("/auth/login")
    public RestAction<AppUser> login (LoginCredentials body);

    @POST
    @Path("/auth/google/login")
    public RestAction<AppUser> loginWithGoogle ();

    @POST
    @Path("/auth/facebook/login")
    public RestAction<AppUser> loginWithFacebook (StringResult body);

    @GET
    @Path("/auth/facebook/url")
    public RestAction<StringResult> getFacebookAuthUrl (@QueryParam("redirectUrl") String redirectUrl);

    @POST
    @Path("/auth/twitter/login")
    public RestAction<AppUser> loginWithTwitter (StringResult body);

    @GET
    @Path("/auth/google/url")
    public RestAction<StringResult> getGoogleAuthUrl (@QueryParam("destinationURL") String destinationURL);

    @GET
    @Path("/auth/twitter/url")
    public RestAction<StringResult> getTwitterAuthUrl (@QueryParam("redirectUrl") String redirectUrl);

    @POST
    @Path("/auth/facebook/register")
    public RestAction<AppUser> createAppUserFromFacebookAccount (FacebookRegistration body);

    @POST
    @Path("/auth/twitter/register")
    public RestAction<AppUser> createAppUserFromTwitterAccount (TwitterRegistration body);

    @POST
    @Path("/auth/google/register")
    public RestAction<AppUser> createAppUserFromGoogleAccount (StringResult body);

}