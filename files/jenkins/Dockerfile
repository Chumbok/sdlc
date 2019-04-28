FROM jenkins/jenkins:lts-alpine

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

COPY seed-jobs/JobBuilderJenkinsConfig.xml /usr/share/jenkins/
COPY seed-jobs/AutoJobBuilderJenkinsConfig.xml /usr/share/jenkins/

COPY init.groovy.d/* /usr/share/jenkins/ref/init.groovy.d/

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
