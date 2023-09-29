#!/bin/bash

IP=$(hostname -I | awk '{print $2}')

APT_OPT="-o Dpkg::Progress-Fancy="0" -q -y"
LOG_FILE="/vagrant/setup-coderunner.log"
DEBIAN_FRONTEND="noninteractive"

echo "START - Setup JOBE (CodeRunner) Server - "$IP

echo "=> [1]: Install Prequire"

# Update package lists and install prerequire
apt-get update $APT_OPT \
  >> $LOG_FILE 2>&1

apt-get install $APT_OPT \
  apt-transport-https \
  ca-certificates \
  curl \
  software-properties-common \
  gnupg \
  >> $LOG_FILE 2>&1

echo "=> [2]: Add Docker's GPG key and the stable repository"

# Add Docker's GPG key and the stable repository
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"

echo "=> [3]: Install Docker"

# Update package lists again and install Docker
apt-get update $APT_OPT \
  >> $LOG_FILE 2>&1

apt-get install $APT_OPT \
  docker-ce \
  >> $LOG_FILE 2>&1

echo "=> [4]: Starting Docker ..."

# Start Docker and enable it to start on boot
sudo systemctl start docker 
sudo systemctl enable docker

echo "=> [5]: Pull and Run Docker JOBE image ..."

# Run the specified Docker image
sudo docker run --name jobe -p 4000:80 -d trampgeek/jobeinabox:latest


cat <<EOF

JOBE Server installed at http://192.168.56.10:4000

EOF

echo "END - Install JOBE (CodeRunner) Server"