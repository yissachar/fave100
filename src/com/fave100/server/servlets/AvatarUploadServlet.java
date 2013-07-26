package com.fave100.server.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fave100.server.domain.appuser.AppUser;
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

				final AppUser currentUser = AppUser.findAppUser(username);
				// TODO: Jul-17-2013 Why do we assume the avatar is a blobkey? Can't it be a link to a Twitter avatar, in which case blob delete will fail??
				if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
					BlobstoreServiceFactory.getBlobstoreService().delete(new BlobKey(currentUser.getAvatar()));
				}
				currentUser.setAvatar(blobKey.getKeyString());
				ofy().save().entity(currentUser).now();
				avatar = currentUser.getAvatarImage();

				res.getWriter().write(avatar);
			}
		}
	}
}
