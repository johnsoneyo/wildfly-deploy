/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.wildfly.deploy;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    @Parameter(defaultValue = "22")
    private int port;
    @Parameter
    private String password;
    @Parameter
    private String remoteFilepath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        StringBuilder sb1 = new StringBuilder();
        String buildFile = sb1.append(projectTargetPath.getPath()).append(".war").toString();

        File file = new File(buildFile);
        if (!file.exists()) {
            throw new RuntimeException("Error. Local file not found");
        }

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        Logger.getLogger(DeployToRemoteWildfly.class.getName()).log(Level.INFO, "preparing the host information for sftp.");

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Logger.getLogger(DeployToRemoteWildfly.class.getName()).log(Level.INFO, "Host connected.");

            channel = session.openChannel("sftp");
            channel.connect();
            Logger.getLogger(DeployToWildfly.class.getName()).log(Level.INFO, "sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(remoteFilepath);
            File f = new File(buildFile);
            channelSftp.put(new FileInputStream(f), f.getName());
            Logger.getLogger(DeployToRemoteWildfly.class.getName()).log(Level.INFO, "File transfered successfully to host >>>>>>>>>>>>.");
        } catch (Exception ex) {
            Logger.getLogger(DeployToRemoteWildfly.class.getName()).log(Level.INFO, "Exception found while tranfer the response.");
        } finally {

            channelSftp.exit();
            Logger.getLogger(DeployToRemoteWildfly.class.getName()).log(Level.INFO, "sftp Channel exited.");
            channel.disconnect();
            Logger.getLogger(DeployToRemoteWildfly.class.getName()).log(Level.INFO, "Channel disconnected.");
            session.disconnect();
            Logger.getLogger(DeployToRemoteWildfly.class.getName()).log(Level.INFO, "Host Session disconnected.");
        }

    }

}
