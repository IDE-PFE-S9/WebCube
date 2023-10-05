#!/bin/bash

IP=$(hostname -I | awk '{print $2}')

APT_OPT="-o Dpkg::Progress-Fancy="0" -q -y"
LOG_FILE="/vagrant/setup-coderunner.log"
DEBIAN_FRONTEND="noninteractive"

echo "START - Setup SSH - "$IP

echo "=> Pasting ssh pub key to authorized_key"

SSH_PUB_KEY="/vagrant/ssh-key-jobe.pub"
DEST_FILE="/home/vagrant/.ssh/authorized_key"

cat "$SSH_PUB_KEY" >> $DEST_FILE

cat <<EOF

SSH configured with ssh-key-jobe

EOF

echo "END - SSH configured"