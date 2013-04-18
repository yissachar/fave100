package com.fave100.server.servlets;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fave100.server.domain.appuser.PwdResetToken;
import com.googlecode.objectify.cmd.QueryKeys;

/**
 * This servlet deletes expired PwdResetTokens. It should be called as a cron
 * job only.
 *
 * @author yissachar.radcliffe
 *
 */
@SuppressWarnings("serial")
public class PasswordCleanupServlet extends HttpServlet
{
    @Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse res)
        throws ServletException, IOException {

    	// Load up to 100 expired PwdResetToken entity keys and delete them
    	final QueryKeys<PwdResetToken> keys = ofy().load()
    			.type(PwdResetToken.class)
    			.filter("expiry <", new Date())
    			.limit(100)
    			.keys();
    	ofy().delete().keys(keys);
    }
}

