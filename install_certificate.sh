#!/bin/bash

###
# #%L
# che-starter
# %%
# Copyright (C) 2017 Red Hat, Inc.
# %%
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# #L%
###

echo "Installing and compiling InstallCert into /opt/jboss/keycloak/InstallCert..."
git clone https://github.com/almighty/InstallCert.git && \
     javac /opt/jboss/keycloak/InstallCert/InstallCert.java

# Import the certificate
cd /opt/jboss/keycloak/InstallCert/
echo "Import the remote certificate from ${OSO_ADDRESS}"
java InstallCert $OSO_ADDRESS << ANSWERS
1
ANSWERS

if [[ -z "${KEYSTORE_PASSWORD}" ]]; then
  KEYSTORE_PASSWORD="almighty"
fi

echo "Export the certificate into the keystore for ${OSO_DOMAIN_NAME}"
keytool -exportcert -alias $OSO_DOMAIN_NAME-1 -keystore jssecacerts -storepass changeit -file $OSO_DOMAIN_NAME.cer

echo "Change password to system keystore for ${OSO_DOMAIN_NAME}"
keytool -storepasswd -new $KEYSTORE_PASSWORD -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit

echo "Import certificate into system keystore for ${OSO_DOMAIN_NAME}"
keytool -importcert -trustcacerts -alias $OSO_DOMAIN_NAME-1 -keystore $JAVA_HOME/lib/security/cacerts -storepass ${KEYSTORE_PASSWORD} -file $OSO_DOMAIN_NAME.cer << ANSWERS
yes
ANSWERS
