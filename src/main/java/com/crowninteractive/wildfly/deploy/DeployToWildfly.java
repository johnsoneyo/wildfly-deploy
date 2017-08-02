/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.wildfly.deploy;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 * @author johnson3yo
 */
@Mojo(name = "deploy", requiresProject = false)
public class DeployToWildfly extends AbstractMojo {

    @Parameter(defaultValue = "/opt/wildfly")
    private File wildflyHome;

    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}")
    private File projectTargetPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        String buildFile = sb1.append(projectTargetPath.getPath()).append(".war").toString();
        String deploymentPath = sb2.append(wildflyHome.getPath()).append("/standalone/deployments").toString();
        File destFile  = new File(buildFile);
        File destPath = new File(deploymentPath);
        
        try {
            Logger.getLogger(DeployToWildfly.class.getName()).log(Level.INFO, "Deploying to your local Wildfly ");
            FileUtils.copyFileToDirectory(destFile, destPath);
        } catch (IOException ex) {
            Logger.getLogger(DeployToWildfly.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
