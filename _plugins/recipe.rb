# Minecraft Recipe Plugin
# Created by lukaskabc

# Allows automatic generation of crafting recipes for SGJourney items
# (recipe ingredients are supported as vanilla and SGJourney items and tags)
# usage:
# {% minecraft_recipe_crafting item:"sgjourney:basic_interface" %}

# additional files are located in _plugins/recipe/

# Some links about custom jekyll plugins:
# https://perseuslynx.dev/blog/jekyll-first-plugin
# https://jekyllrb.com/docs/plugins/tags/

# Vanilla assets are dynamically linked to minecraft.wiki
# Vanilla tags and translation are loaded from https://mcasset.cloud/
# That requires to have RECIPE_GAME_VERSION set in the recipe/constants.rb file

# StargateJourney assets are copied from the implementation_branch directory to assets/img/items/crafting/sgjourney/dynamic
# However, those assets does not contain blocks, since they are rendered differently (from individual sides etc.)
# I dont really want to write a custom renderer (plus custom models are involved so it would require actual java implementation ... and what not)
# So for that reason, there is assets/img/items/crafting/sgjourney/static directory which takes priority over dynamic assets
# That directory must be versioned with git since those files are not contained in the implementation_branch directory and GitHub workflow needs access to them

# Block assets
# https://github.com/Sinytra/ItemAssetExporterMod
# This allows to export item icons in a similar manner as they are rendered in the game GUI
# lukaskabc made a fork https://github.com/lukaskabc/ItemAssetExporterMod with some modifications
# You can clone the fork repository
# in the cloned repository create directory `neoforge/run/mods` and copy the SGJourney mod jar for NeoForge 1.21.1 there
# Run Neoforge client with gradle (the game will export assets during loading and then exit)
#
# After exporting the assets, copy/move the directory `neoforge/run/.assets/item/sgjourney` to the documentation repository to `exported/sgjourney`
# You can use `scripts/update_exported.sh` script to automatically update git tracked files in the static directory with files from the exported directory
# Or you can manually just add files to the `assets/img/items/crafting/sgjourney/static` directory

module Recipe
  LOG = Jekyll.logger
end

Liquid::Template.register_tag('minecraft_recipe_crafting', Recipe::CraftingRecipe)