# CROWN INTERACTIVE WILDFLY DEPLOYMENT PLUGIN
# README.md will be updated later
# Specify plugin in your project pom and configure wildflyHome to your local wildfly version


             <plugin>
                <groupId>com.crowninteractive</groupId>
                <artifactId>wildfly_deploy-maven-plugin</artifactId>
                <version>1.0</version>
                <configuration>
                     <!--Local Wildfly Path--> 
                    <wildflyHome>/home/johnson3yo/crownprojects/wildfly-11.0.0.Alpha1</wildflyHome>
                    <projectTargetPath>${project.build.directory}/${project.build.finalName}</projectTargetPath>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>deploy</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

# Deploy Remote 

       <plugin>
                <groupId>com.crowninteractive</groupId>
                <artifactId>wildfly_deploy-maven-plugin</artifactId>
                <version>1.0</version>
                <configuration>
                    <user>root</user>
                    <password>int3ractiv3</password>
                    <remoteFilepath>/opt/wildfly/standalone/deployments</remoteFilepath>
                    <host>81.26.67.111</host>
                    <projectTargetPath>${project.build.directory}/${project.build.finalName}</projectTargetPath>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>deployRemote</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
