#!/bin/bash
#keytool -genkey -alias localhost -keyalg RSA -keystore keystore.jks -keysize 2048
keytool -genkey -alias spantus.cloudfoundry.com  -keyalg RSA -keystore cloud.keystore.jks -keysize 2048
