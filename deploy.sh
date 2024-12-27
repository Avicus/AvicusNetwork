#!/bin/bash

TARGET_IP=$1

if [ -z "$TARGET_IP" ]; then
    echo "Usage: $0 <target_ip>"
    exit 1
fi

function copy_file() {
    source=$1
    target=$2
    echo "Copying $source to /server/$target"
    scp $source root@$TARGET_IP:/server/$target
}

cd "$(dirname "$0")"
copy_file "Atlas/core/target/atlas-core-1.8-SNAPSHOT.jar" "plugins/atlas-core.jar"
copy_file "Atlas/competitive-objectives/target/atlas-competitive-objectives-1.8-SNAPSHOT.jar" "plugins/Atlas/module-sets/atlas-competitive-objectives.jar"
copy_file "Hook/Bukkit/target/hook-bukkit-1.8-SNAPSHOT.jar" "plugins/hook-bukkit.jar"
copy_file "Magma/bukkit/target/magma-bukkit-1.8-SNAPSHOT.jar " "plugins/magma-bukkit.jar"
copy_file "../Compendium/Bukkit/target/compendium-bukkit-1.1-SNAPSHOT.jar" "plugins/compendium-bukkit.jar"
