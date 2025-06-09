module Recipe
  RELATIVE_JEKYLL_CRAFTING_ASSETS = "assets/img/items/crafting".freeze
  # Absolute path to the project root (hopefully)
  PROJECT_DIRECTORY = Dir.getwd
  IMPLEMENTATION_BRANCH = File.join(PROJECT_DIRECTORY, 'implementation_branch')
end