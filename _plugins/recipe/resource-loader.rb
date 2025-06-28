require 'open-uri'

module Recipe
  TAG_CACHE = {}
  class ResourceLoader

    # @param context [Liquid::Context] The template context
    def initialize(context)
      @context = context
      @namespace_handlers = {
        "sgjourney" => ResourceLoaderHandler.new(method(:load_sgj_item), method(:load_sgj_tag)),
        "minecraft" => ResourceLoaderHandler.new(method(:load_minecraft_item), method(:load_minecraft_tag))
      }.freeze
    end

    # Loads image for the resource allowing to render it afterward.
    # Note that loaded images are saved to the #RELATIVE_JEKYLL_CRAFTING_ASSETS directory
    # @param resource [McResource]
    def load(resource)
      if resource.has_asset_url
        return
      end

      unless @namespace_handlers.has_key?(resource.namespace)
        LOG.error("Unable to load resource, unknown namespace: #{resource.namespace} for #{resource}")
        return
      end

      @namespace_handlers[resource.namespace].load(resource)
    end

    private # methods below are private

    # @param resource [McResource]
    def load_minecraft_item(resource)
      capital_name = resource.name.split('_').map(&:capitalize).join('_')
      resource.asset_url = "https://minecraft.wiki/images/Invicon_" + capital_name + ".png"
    end

    # @param resource [McResource]
    def load_minecraft_tag(resource)
      tag = McResource.from(JSON.load(URI.open("https://assets.mcasset.cloud/#{RECIPE_GAME_VERSION}/data/minecraft/tags/item/#{resource.name}.json")).freeze["values"][0])
      process_tag(tag, resource)
    end

    # @param resource [McResource]
    def load_sgj_item(resource)
      source_file = File.join(IMPLEMENTATION_BRANCH, 'src/main/resources/assets/sgjourney/textures/item/', "#{resource.name}.png")
      exported_source_file = File.join(PROJECT_DIRECTORY, "exported/sgjourney", "#{resource.name}.png")
      destination_file = resource.resource_file(false) # dynamic asset
      if File.exist?(resource.resource_file(true)) # check if static asset exists
        resource.asset_url = resource.asset_file(true)
      elsif File.exist?(destination_file)
        resource.asset_url = resource.asset_file(false) # dynamic asset already exists
      elsif File.exist?(source_file)
        LOG.debug("Copying #{source_file} to #{destination_file}")
        FileUtils.mkdir_p(File.dirname(source_file))
        FileUtils.cp(source_file, destination_file)
        resource.asset_url = resource.asset_file(false) # dynamic asset
      elsif File.exist?(exported_source_file)
        LOG.info("Copying exported asset to static assets: #{resource.name}.png")
        FileUtils.mkdir_p(File.dirname(resource.resource_file(true))) # static asset directory
        FileUtils.cp(exported_source_file, resource.resource_file(true))
        resource.asset_url = resource.asset_file(true) # static asset
      else
        raise "Missing source file #{source_file} for resource #{resource}"
      end
    end

    # @param resource [McResource]
    def load_sgj_tag(resource)
      tag_file = File.join(IMPLEMENTATION_BRANCH, "src/main/resources/data/#{resource.namespace}/tags/item/", "#{resource.name}.json")
      if File.exist?(tag_file)
        tag = McResource.from(JSON.parse(File.read(tag_file))['values'][0])
        process_tag(tag, resource)
      else
        raise "Missing tag file #{tag_file} for resource #{resource}"
      end
    end

    def process_tag(tag, resource)
      load(tag)

      if tag.nil? or tag.is_tag
        puts resource.inspect
        puts tag.inspect
        raise "Failed to resolve tag to an item"
      end

      TAG_CACHE[resource] = tag
      resource.asset_url = tag.asset_url
      LOG.debug("#{resource} resolved to #{tag}")
    end
  end
  class ResourceLoaderHandler
    # @param load_item [Method]
    # @param load_tag [Method]
    def initialize(load_item, load_tag)
      @load_item = load_item
      @load_tag = load_tag
    end

    # @param resource [McResource]
    def load(resource)
      if resource.is_tag
        if TAG_CACHE.has_key?(resource)
          resource.asset_url = TAG_CACHE[resource].asset_url
          return
        end
        @load_tag.call(resource)
      else
        @load_item.call(resource)
      end
    end
  end
end