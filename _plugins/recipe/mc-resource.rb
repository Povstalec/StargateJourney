module Recipe
  # Represents a minecraft resource
  # an item or a block identifier
  # e.g. minecraft:ruby
  class McResource
    attr_reader :namespace, :name

    def self.from(name)
      parsed = name.split(':', 2)
      return McResource.new(parsed[0], parsed[1])
    end

    def initialize(name_space, resource_name)
      @namespace = name_space
      @name = resource_name
    end

    def to_s
      "#{@namespace}:#{@name}"
    end

  end
end