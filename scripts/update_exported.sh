#!/bin/bash

# see `_plugins/recipe.rb` for more information about the exported directory

# List all files tracked by git in ./assets/img/items/crafting/sgjourney/static
tracked_files=$(git ls-files ./assets/img/items/crafting/sgjourney/static)

# For each tracked file, overwrite it with the one from ./exported/sgjourney if it exists
for file in $tracked_files; do
    # Get the base filename
    filename=$(basename "$file")
    # Source file to copy from
    src="./exported/sgjourney/$filename"
    # Destination file (already known as $file)
    if [ -f "$src" ]; then
        cp "$src" "$file"
        echo "Overwritten $file with $src"
    else
        echo "Source file $src does not exist, skipping."
    fi
done