/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/).
 * This is unpublished proprietary source code of Nuxeo SA. All rights reserved.
 * Notice of copyright on this source code does not indicate publication.
 *
 * Contributors:
 *     gildas
 */
package com.gildas.app;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.util.FS;
import org.junit.Test;

/**
 * Test class showing the use of a bare repository with JGit.
 *
 * @author gildas
 */
public class BlogTest {

    @Test
    public void test_bare_repository() throws Exception {
        // Create a folder in the temp folder that will act as the remote repository
        File remoteDir = File.createTempFile("remote", "");
        remoteDir.delete();
        remoteDir.mkdirs();

        // Create a bare repository
        FileKey fileKey = FileKey.exact(remoteDir, FS.DETECTED);
        Repository remoteRepo = fileKey.open(false);
        remoteRepo.create(true);

        // Clone the bare repository
        File cloneDir = File.createTempFile("clone", "");
        cloneDir.delete();
        cloneDir.mkdirs();
        Git git = Git.cloneRepository().setURI(remoteRepo.getDirectory().getAbsolutePath()).setDirectory(cloneDir).call();

        // Let's to our first commit
        // Create a new file
        File newFile = new File(cloneDir, "myNewFile");
        newFile.createNewFile();
        FileUtils.writeStringToFile(newFile, "Test content file");
        // Commit the new file
        git.add().addFilepattern(newFile.getName()).call();
        git.commit().setMessage("First commit").setAuthor("gildas", "gildas@example.com").call();

        // Push the commit on the bare repository
        RefSpec refSpec = new RefSpec("master");
        git.push().setRemote("origin").setRefSpecs(refSpec).call();

        // Create a second working directory
        File cloneDir2 = File.createTempFile("clone", "");
        cloneDir2.delete();
        cloneDir2.mkdirs();
        Git git2 = Git.cloneRepository().setURI(remoteRepo.getDirectory().getAbsolutePath()).setDirectory(cloneDir2).call();

        // Check that the commited file is here
        assertTrue(new File(cloneDir2, "myNewFile").exists());
    }
}
