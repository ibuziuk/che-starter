FROM centos:7

ARG VERSION=1.0-SNAPSHOT

ENV JAVA_HOME /etc/alternatives/jre
ENV CHE_STARTER_HOME /opt/che-starter

## Default ENV variable values
ENV OSO_ADDRESS api.starter-us-east-2.openshift.com
ENV OSO_DOMAIN_NAME api.starter-us-east-2.openshift.com
ENV KUBERNETES_CERTS_CA_FILE /opt/che-starter/api.starter-us-east-2.openshift.com.cer

RUN yum update -y && \
    yum install -y \
       java-1.8.0-openjdk java-1.8.0-openjdk-devel git && \
    yum clean all

WORKDIR $CHE_STARTER_HOME

RUN git clone https://github.com/almighty/InstallCert.git && \
     javac $CHE_STARTER_HOME/InstallCert/InstallCert.java

RUN chown -R 1000:0 ${CHE_STARTER_HOME} && chmod -R ug+rw ${CHE_STARTER_HOME}

ADD docker-entrypoint.sh $CHE_STARTER_HOME

VOLUME /tmp

ADD target/che-starter-$VERSION.jar $CHE_STARTER_HOME/app.jar

EXPOSE 10000

ENTRYPOINT ["/opt/che-starter/docker-entrypoint.sh"]
