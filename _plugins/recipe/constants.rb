module Recipe
  RELATIVE_JEKYLL_CRAFTING_ASSETS = "assets/img/items/crafting".freeze
  # Absolute path to the project root (hopefully)
  PROJECT_DIRECTORY = Dir.getwd
  IMPLEMENTATION_BRANCH = File.join(PROJECT_DIRECTORY, 'implementation_branch')
  # Minecraft version used for accessing vanilla data at https://mcasset.cloud
  RECIPE_GAME_VERSION = '1.21.1'.freeze
  STATIC_ITEM_LINKS_FILE = File.join(PROJECT_DIRECTORY, "_plugins", "recipe", "static_item_links.json")
end