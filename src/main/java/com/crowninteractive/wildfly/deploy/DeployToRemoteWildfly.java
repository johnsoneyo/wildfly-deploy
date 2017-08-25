/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.wildfly.deploy;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author johnson3yo
 */
@Mojo(name = "deployRemote", requiresProject = false)
public class DeployToRemoteWildfly extends AbstractMojo {

  
    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}")
    private File projectTargetPath;
    
    @Parameter
    private String host;
    @Parameter
    private String user;
    @Parameter 
    private String password;
    @Parameter
    private String remoteFilepath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
      
        StringBuilder sb1 = new StringBuilder();
        String buildFile = sb1.append(projectTargetPath.getPath()).append(".war").toString();
        
        File file = new File(buildFile);
        if (!file.exists())
            throw new RuntimeException("Error. Local file not found");

        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            // Create local file object
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());

            // Create remote file object
            FileObject remoteFile = manager.resolveFile(createConnectionString(host, user, password, remoteFilepath), createDefaultOptions());
            /*
             * use createDefaultOptions() in place of fsOptions for all default
             * options - Ashok.
             */

            // Copy local file to sftp server
            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);

           Logger.getLogger(DeployToRemoteWildfly.class.getName()).log(Level.INFO, " >>> Deploying to your Remote Wildfly >>>> ");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    
    
    }

    public static String createConnectionString(String hostName, String username, String password, String remoteFilePath) {
        return "sftp://" + username + ":" + password + "@" + hostName + "/" + remoteFilePath;
    }

    /**
     * Method to setup default SFTP config
     * 
     * @return the FileSystemOptions object containing the specified
     *         configuration options
     * @throws FileSystemException
     */
    public static FileSystemOptions createDefaultOptions() throws FileSystemException {
        // Create SFTP options
        FileSystemOptions opts = new FileSystemOptions();

        // SSH Key checking
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

        /*
         * Using the following line will cause VFS to choose File System's Root
         * as VFS's root. If I wanted to use User's home as VFS's root then set
         * 2nd method parameter to "true"
         */
        // Root directory set to user home
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

        // Timeout is count by Milliseconds
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

        return opts;
    }
    
}
