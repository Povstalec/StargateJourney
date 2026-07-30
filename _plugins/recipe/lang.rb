# Functions for getting translation strings
#
require 'open-uri'
require 'json'
require 'jekyll/filters/url_filters'

module Recipe
  class Lang
    # @param context [Liquid::Context]
    def initialize(context)
      @context = context
    end

    # Returns English translation for given item/block
    # @param resource [McResource] The minecraft resource
    # @return [String, nil]
    def translate(resource)
      if resource.is_tag and TAG_CACHE.has_key?(resource)
        resource = TAG_CACHE[resource]
      end

      static_name = static_translation_name(resource)
      translation = choose_namespace(resource)
      unless translation.nil?
        key = "#{resource.namespace}.#{resource.name}"

        item_key = "item." + key
        unless translation[item_key].nil?
          return translation[item_key]
        end

        block_key = "block." + key
        unless translation[block_key].nil?
          return translation[block_key]
        end
      end

      if static_name
        return static_name
      end
      Jekyll.logger.error("Missing translations in namespace '#{resource.namespace}' for item '#{resource.namespace}:#{resource.name}'")
      return nil
    end

    private
      include Jekyll::Filters::URLFilters

      NAMESPACE_MAP = {
        "minecraft" => JSON.parse(URI.open("https://assets.mcasset.cloud/#{RECIPE_GAME_VERSION}/assets/minecraft/lang/en_us.json", open_timeout: 5, read_timeout: 10).read).freeze,
        "sgjourney" => JSON.parse(File.read(File.join(PROJECT_DIRECTORY, "implementation_branch", "src/main/resources/assets/sgjourney/lang/en_us.json"))).freeze,
      }

      # @param resource [McResource]
      # @return [Hash, nil]
      def choose_namespace(resource)
        NAMESPACE_MAP[resource.namespace]
      end

      # @param resource [McResource]
      # @return [String, nil]
      def static_translation_name(resource)
        static_entry = Recipe.static_item_entry(resource)
        if static_entry && static_entry["name"]
          return static_entry["name"]
        end
        return nil
      end
  end
end