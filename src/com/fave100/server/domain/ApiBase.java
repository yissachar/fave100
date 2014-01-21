package com.fave100.server.domain;

import com.google.api.server.spi.config.Api;

/*
 * A common base class to ensure all APIs participating in the multiclass API share 
 * the exact same API configuration as per https://developers.google.com/appengine/docs/java/endpoints/multiclass
 * 
 * NOTE: This really should be an abstract class but doing so will clause Endpoints to fail 
 */
@Api(name = "fave100", version = "v1")
public class ApiBase {

}
