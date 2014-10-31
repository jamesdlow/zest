package com.jameslow.zest;

import java.io.File;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.*;
import org.tmatesoft.svn.core.wc.*;

public class SVNClass {
	public static void updateOrCheckout(File path, String remote, String username, String password) throws SVNException {
		if (remote.length() >= 4) {
			String begin = remote.substring(0,4).toLowerCase();
			if ("http".compareTo(begin) == 0) {
				DAVRepositoryFactory.setup();
			} else if ("file".compareTo(begin) == 0) {
				FSRepositoryFactory.setup();
			}
		} else if (remote.length() >= 3 && "svn".compareTo(remote.substring(0,3).toLowerCase()) == 0) {
			SVNRepositoryFactoryImpl.setup();
		}
		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNClientManager clientManager = SVNClientManager.newInstance(options, username, password);
	    SVNStatusClient statusClient = clientManager.getStatusClient();
	    SVNUpdateClient updateClient = clientManager.getUpdateClient();
	    SVNURL url = SVNURL.parseURIEncoded(remote);
	    boolean checkout = false;
	    try {
			SVNStatus status = statusClient.doStatus(path, false);
		} catch (SVNException e) {
			checkout = true;
		}
		if (checkout) {
			//Not every extracted, do checkout
			updateClient.doCheckout(url, path, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.IMMEDIATES, true);
		} else {
			//Update to latest working copy
			updateClient.doUpdate(path, SVNRevision.HEAD, SVNDepth.IMMEDIATES, true, true);
			//Revert local changes
			SVNWCClient wcClient = clientManager.getWCClient();
			File[] paths = new File[1];
			paths[0] = path;
			wcClient.doRevert(paths, SVNDepth.IMMEDIATES, null);
		}
	}
}
