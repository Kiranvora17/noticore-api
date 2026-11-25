#!/usr/bin/env bash
# One-time setup for a fresh Ubuntu server (x86 or ARM). Run as the VM's admin user:
#   bash setup-server.sh
set -euo pipefail

# The stack needs swap as a safety margin on a 1 GB server
total_kb=$(awk '/^MemTotal/ {print $2}' /proc/meminfo)
if [ "$total_kb" -lt 2000000 ] && [ -z "$(swapon --show --noheadings)" ]; then
    echo "==> Creating a 2 GB swap file"
    sudo fallocate -l 2G /swapfile
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    grep -q '^/swapfile ' /etc/fstab ||
        echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab >/dev/null
fi

echo "==> Installing Docker"
if ! command -v docker >/dev/null 2>&1; then
    curl -fsSL https://get.docker.com | sudo sh
fi
sudo usermod -aG docker "$USER"

mkdir -p "$HOME/noticore/backups"

echo "==> Done. Log out and back in so the docker group applies."
