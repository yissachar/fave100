package com.fave100.server.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fave100.server.domain.appuser.AppUser;
import com.fave100.shared.exceptions.user.NotLoggedInException;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

@SuppressWarnings("serial")
public class AvatarUploadServlet extends RequestFactoryServlet
{

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	@Inject
	public void doPost(final HttpServletRequest req, final HttpServletResponse res)
			throws ServletException, IOException {
		final Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);

		final List<BlobKey> bloblist = blobs.get("avatar");
		if (bloblist != null && bloblist.size() > 0) {

			final BlobKey blobKey = bloblist.get(0);

			if (blobKey != null) {
				final String username = (String)req.getSession().getAttribute(AppUser.AUTH_USER);
				Objects.requireNonNull(username);
				String avatar = "";
				try {
					avatar = AppUser.setAvatarForCurrentUser(blobKey.getKeyString());
				}
				catch (final NotLoggedInException e) {
					// Couldn't save avatar, send back blank
				}
				res.getWriter().write(avatar);
			}
		}
	}
}
