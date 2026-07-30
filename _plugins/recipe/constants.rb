require 'json'

module Recipe
  RELATIVE_JEKYLL_CRAFTING_ASSETS = "assets/img/items/crafting".freeze
  # Absolute path to the project root (hopefully)
  PROJECT_DIRECTORY = Dir.getwd
  IMPLEMENTATION_BRANCH = File.join(PROJECT_DIRECTORY, 'implementation_branch')
  # Minecraft version used for accessing vanilla data at https://mcasset.cloud
  RECIPE_GAME_VERSION = '1.21.1'.freeze
  STATIC_ITEM_LINKS_FILE = File.join(PROJECT_DIRECTORY, "_plugins", "recipe", "static_item_links.json")
  STATIC_ITEM_TEXTURES_FILE = File.join(PROJECT_DIRECTORY, "_plugins", "recipe", "static_item_textures.json")
  static_textures = {}
  if File.exist?(STATIC_ITEM_TEXTURES_FILE)
    static_textures = JSON.parse(File.read(STATIC_ITEM_TEXTURES_FILE))
  end
  STATIC_ITEM_TEXTURES = static_textures.freeze

  # @param resource [McResource]
  # @return [Hash, nil]
  def self.static_item_entry(resource)
    namespace_key = resource.is_tag ? "##{resource.namespace}" : resource.namespace
    entry = STATIC_ITEM_TEXTURES.dig(namespace_key, resource.name)
    if entry.nil?
      alt_key = resource.is_tag ? resource.namespace : "##{resource.namespace}"
      entry = STATIC_ITEM_TEXTURES.dig(alt_key, resource.name)
    end
    entry
  end
end