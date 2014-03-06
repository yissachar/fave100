package com.fave100.server.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fave100.server.domain.appuser.AppUser;
import com.fave100.server.domain.appuser.AppUserDao;
import com.fave100.shared.Constants;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.inject.Inject;

@SuppressWarnings("serial")
public class AvatarUploadServlet extends HttpServlet {

	public static final String PATH = "/avatarUpload";

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
				// Get session token from cookie
				String sessionToken = "";
				for (Cookie cookie : req.getCookies()) {
					if (cookie.getName().equals(Constants.SESSION_HEADER)) {
						sessionToken = cookie.getValue();
					}
				}

				// Get user from session
				final String username = (String)req.getSession().getAttribute(AppUserDao.AUTH_USER);
				Objects.requireNonNull(username);
				String avatar = "";

				AppUserDao appUserDao = new AppUserDao();
				final AppUser currentUser = appUserDao.findAppUser(username);
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
