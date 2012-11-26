package com.fave100.server.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fave100.client.place.NameTokens;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

@SuppressWarnings("serial")
public class AvatarUploadServlet extends RequestFactoryServlet
 {

    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override @Inject
	public void doPost(final HttpServletRequest req, final HttpServletResponse res)
        throws ServletException, IOException {
        final Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);

        final List<BlobKey> bloblist = blobs.get("avatar");
        if(bloblist != null && bloblist.size() > 0) {

        	final BlobKey blobKey =  bloblist.get(0);

        	if(blobKey != null) {
            	// TODO: Figure out why this doesn't work and put back in
            	//res.setContentType("text/html");
            	//res.getWriter().println(blobKey.getKeyString());
            	// TODO: Remove hard coded URL
            	res.sendRedirect("http://yissachar:8888/Fave100.html?gwt.codesvr=127.0.0.1:9997"+"#"+NameTokens.profile+";blob-key=" + blobKey.getKeyString());
            }
        }
    }
}

