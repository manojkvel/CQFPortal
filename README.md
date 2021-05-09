Server Setup and Software Deployment

1. Apache Tomcat is being used as the web server for this application.
2. The binaries for the tomcat are provided along the project reports soft copy.
3. Please extract the apache tomcat file to your local drive.
4. Please make sure that you have set the JAVA_HOME environment variable to you java home directory.
5. Application uses Java 1.6 SDK.
6. Navigate to the bin directory of the tomcat installation folder.
7. Example: C:\apache\apache-tomcat-6.0.37\bin
8. Click on startup.bat(alternatively open up command prompt in windows, navigate to C:\apache\apache-tomcat-6.0.37\bin and type startup and press Enter.
9. This action should trigger the start of tomcat web server.
10. Now go to the deployment folder of tomcat. Navigate to webapps dir ., example C:\apache\apache-tomcat-6.0.37\webapps
11. Copy the CQFPortal.war given in the pen drive and paste it in the webapps directory of tomcat.
12. In a few seconds the tomcat deploys the web application archive (war) as exploded form in the webapps directory with a name CQFPortal.
13. Now the app is ready to use.
14. Open up a browser and type the URL http://localhost:8080/CQFPortal.
15. Note: If you are using your own tomcat server and in case of any deployment failure, then check if the invokerServlet is active and privileged in your tomcat config.
16. For further reference please go through http://www.coreservlets.com/Apache-Tomcat-Tutorial/detailed-configuration.html.
