#!/bin/bash

# See https://sdkman.io/install
apt-get update
apt-get -q -y install curl zip unzip git
curl -s https://get.sdkman.io | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# See https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html


sdk install java 15.0.1.j9-adpt
sdk install sbt

sbt clean compile test


#echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
#curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo apt-key add
#apt update
#apt install sbt
#
#apt install openjdk-11-jdk