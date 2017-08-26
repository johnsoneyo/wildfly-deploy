/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.wildfly.deploy;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        getLog().info("rasco the host information for sftp.");

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            getLog().info("Host connected.");

            channel = session.openChannel("sftp");
            channel.connect();
            getLog().info("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(remoteFilepath);
            File f = new File(buildFile);
            channelSftp.put(new FileInputStream(f), f.getName());
            getLog().info("File transfered successfully to host >>>>>>>>>>>>.");

        } catch (JSchException | SftpException | FileNotFoundException ex) {
            getLog().error("Exception found while tranfer the response.",ex);
        } finally {
            if (channelSftp != null) {
                channelSftp.exit();
            }
            getLog().info("sftp Channel exited.");
            if (channel != null) {
                channel.disconnect();
            }
            getLog().info("Channel disconnected.");
            if (session != null) {
                session.disconnect();
            }
            getLog().info("Host Session disconnected.");
        }

    }

}
