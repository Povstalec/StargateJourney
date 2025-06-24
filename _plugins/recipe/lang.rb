# Functions for getting translation strings
#
require 'open-uri'
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

      translation = choose_namespace(resource)
      if translation.nil?
        return nil
      end

      key = "#{resource.namespace}.#{resource.name}"

      item_key = "item." + key
      unless translation[item_key].nil?
        return translation[item_key]
      end

      block_key = "block." + key
      unless translation[block_key].nil?
        return translation[block_key]
      end

      LOG.error("Translation not found for item/block '#{resource}'")
      nil
    end

    private
      include Jekyll::Filters::URLFilters

      NAMESPACE_MAP = {
        "minecraft" => JSON.load(URI.open("https://assets.mcasset.cloud/#{RECIPE_GAME_VERSION}/assets/minecraft/lang/en_us.json")).freeze,
        "sgjourney" => JSON.load(open(File.join(PROJECT_DIRECTORY, "implementation_branch", "src/main/resources/assets/sgjourney/lang/en_us.json")).read).freeze,
      }

      # @param resource [McResource]
      # @return [Hash, nil]
      def choose_namespace(resource)
        if NAMESPACE_MAP[resource.namespace].nil?
          LOG.error("Missing translations for namespace '#{resource.namespace}'")
          nil
        else
          NAMESPACE_MAP[resource.namespace]
        end
      end
  end
end