#!/usr/bin/env bash
# Dumps the database and keeps the 7 most recent copies. Schedule with crontab -e:
#   0 2 * * * bash /home/kiran/noticore/deploy/backup-db.sh
set -euo pipefail

backup_dir="$HOME/noticore/backups"
target="$backup_dir/noticore-$(date +%Y%m%d-%H%M%S).dump"
mkdir -p "$backup_dir"

# Write to a temporary name so a failed dump never counts as a good backup
docker exec noticore-postgres sh -c 'pg_dump -U "$POSTGRES_USER" -d noticore -Fc' > "$target.partial"
mv "$target.partial" "$target"

ls -1t "$backup_dir"/noticore-*.dump | tail -n +8 | xargs -r rm --
echo "Saved $target"
