module Recipe
  # Represents a minecraft resource
  # an item or a block identifier
  # e.g. minecraft:ruby
  class McResource
    # @return [String]
    attr_reader :namespace, :name
    # @return [Boolean]
    attr_reader :is_tag
    attr_writer :asset_url

    # cache for McResources
    RESOURCES = {}

    # @param name [String]
    # @return [McResource]
    def self.from(name)
      unless RESOURCES.key?(name)
        parsed = name.split(':', 2)
        RESOURCES[name] = new(parsed[0], parsed[1])
      end
      return RESOURCES[name]
    end

    # Returns a string representation of the resource "namespace:name"
    # possibly prefixed with "#" if it represents a tag
    def to_s
      (@is_tag ? "#" : "") + "#{@namespace}:#{@name}"
    end

    # @return [String] absolute path to the image file representing the resource in jekyll source assets directory
    def resource_file(static = true)
      File.join(PROJECT_DIRECTORY, asset_file(static))
    end

    # @return [String] path relative to project root pointing to the resource file in the assets directory
    def asset_file(static = true)
      ver = "static"
      unless static
          ver = "dynamic"
      end
      File.join(RELATIVE_JEKYLL_CRAFTING_ASSETS, @namespace, ver, "#{@name}.png")
    end

    # @return [String] absolute web url to the asset
    def asset_url
      if @asset_url.nil?
        throw "Missing file for resource #{self}"
      end
      @asset_url
    end

    def has_asset_url
      not @asset_url.nil?
    end

    private_class_method :new # use :from factory method instead
    private

    # @param name_space [String]
    # @param resource_name [String]
    def initialize(name_space, resource_name)
      if name_space.length > 0 && name_space[0] == "#"
        name_space.delete_prefix!("#")
        @is_tag = true
      else
        @is_tag = false
      end
      @namespace = name_space
      @name = resource_name

      if @namespace.nil?
        raise "Minecraft Resource initialization error: namespace is nil - name_space: '#{name_space}', resource_name: '#{resource_name}'"
      end
      if @name.nil?
        raise "Minecraft Resource initialization error: resource name is nil - name_space: '#{name_space}', resource_name: '#{resource_name}'"
      end
    end

  end
end