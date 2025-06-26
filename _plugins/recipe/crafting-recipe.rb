module Recipe
  class CraftingRecipe < Liquid::Tag
    include Jekyll::Filters::URLFilters
    RECIPES = {}

    # flattens sgjourney recipes folder for easy lookups
    def self.load_recipes
      # stack for directories with recipes (we are doing a recursive traversing)
      dirs = [File.join(IMPLEMENTATION_BRANCH, 'src/main/resources/data/sgjourney/recipe')]
      while dirs.any? # repeat until there is a directory to search
        # @type [String]
        dir = dirs.pop

        if dir.nil?
          throw "Assertation error: recipe directory is nil"
        end

        Dir.each_child(dir) do |entry|
          path = File.join(dir, entry)
          if File.file?(path)
            # removes .json from the end of the file name, this should leave only the item resource name
            RECIPES[entry.gsub(/(.json)$/, "")] = path
          else
            # if we found a directory, add it to the stack for traversal
            dirs.push(path)
          end
        end
      end
      puts "Loaded #{RECIPES.count} recipes for sgjourney"
    end
    load_recipes # loads SGJ recipes on class init

    def render_crafting_table
      <<~HTML
        <span class="mcui mcui-Crafting_Table pixel-image">
          <span class="mcui-input">
            <span class="mcui-row">
              <span class="invslot">#{get_ingredient(0, 0)}</span>
              <span class="invslot">#{get_ingredient(0, 1)}</span>
              <span class="invslot">#{get_ingredient(0, 2)}</span>
            </span>
            <span class="mcui-row">
              <span class="invslot">#{get_ingredient(1, 0)}</span>
              <span class="invslot">#{get_ingredient(1, 1)}</span>
              <span class="invslot">#{get_ingredient(1, 2)}</span>
            </span>
            <span class="mcui-row">
              <span class="invslot">#{get_ingredient(2, 0)}</span>
              <span class="invslot">#{get_ingredient(2, 1)}</span>
              <span class="invslot">#{get_ingredient(2, 2)}</span>
            </span>
          </span>
          <span class="mcui-arrow"><br></span>
          <span class="mcui-output">
            <span class="invslot invslot-large">#{@product}</span>
          </span>
        </span>
      HTML
      # Remove all whitespace between tags and print the output
        .gsub(/>\s+</, '><').strip
    end

    # called by jekyll for each tag usage to init the object
    def initialize(tag_name, markup, parse_context)
      super
      @recipe_items = Array.new(3*3)
      @product = "".freeze
      parse_attributes(markup)
    end

    # parses attributes from markup
    # and fills @attributes map
    def parse_attributes(markup)
      @attributes = {}
      markup.scan(Liquid::TagAttributes) do |key, value|
        @attributes[key] = strip_quotes(value)
      end
    end

    def get_ingredient(row, col)
      @recipe_items[row * 3 + col]
    end

    def set_ingredient(row, col, value)
      @recipe_items[row * 3 + col] = value
    end

    # called by jekyll when the tag should be rendered
    # @param context [Liquid::Context] The template context
    def render(context)
      @context = context
      @lang = Lang.new(context)
      @resource_loader = ResourceLoader.new(context)
      unless @attributes["item"]
        raise "Recipe Crafting Tag usage error: missing item attribute with an item id"
      end
      load_item_recipe(McResource.from(@attributes["item"]))

      render_crafting_table
    end

    # @param str [String] Text to strip single and double quotes from
    # @return [String]
    def strip_quotes(str)
      str.gsub(/(^")|("$)/,'').gsub(/(^')|('$)/, "")
    end

    # @param resource [McResource]
    def load_item_recipe(resource)
      if resource.namespace != "sgjourney"
        raise "Recipe Crafting Tag does not support recipes from other namespaces than sgjourney"
      end

      recipe_file = RECIPES[resource.name]
      unless recipe_file
        puts RECIPES.inspect
        raise "Crafting recipe for #{resource} was not found"
      end

      recipe = JSON.parse(open(recipe_file).read)

      process_recipe(recipe)
    end

    def process_recipe(recipe)
      case recipe["type"]
      when "minecraft:crafting_shaped"
        process_crafting_shaped(recipe)
      else
        raise "Crafting type #{recipe["type"]} is not supported"
      end
    end

    def item_web_link(resource)
      case resource.namespace
      when "minecraft"
        name = resource.name.split('_').map(&:capitalize).join('_')
        return "https://minecraft.wiki/w/#{name}#Crafting"
        # TODO: sgjourney namespace linking
      else
        return ""
      end
    end

    # @param resource [McResource] The resource to render
    def render_item(resource)
      title = @lang.translate(resource)
      description = ""
      if title.nil?
        title = resource.to_s
        description = "missing translation"
      end
      file = resource.asset_url
      link = item_web_link(resource)
      img = "<img src=\"#{absolute_url(file)}\">"
      if not link.nil? and not link.empty? and link != '#'
          img = "<a href=\"#{link}\">#{img}</a>"
      end

      <<~HTML
        <span class="invslot-item invslot-item-image" data-minetip-title="#{title}" data-minetip-text="#{description}">
          #{img}
        </span>
      HTML
        .gsub(/>\s+</, '><').strip
    end

    def process_crafting_shaped(recipe)
      items = {}
      recipe["key"].each do |key, value| # TODO this will need an upgrade for newer versions
        if value.class == Array
            value = value[0]
        end
        if value["item"].nil?
          items[key] = ("#" + value["tag"]).freeze
        else
          items[key] = value["item"]
        end
      end

      row = 0
      recipe["pattern"].each do |line|
        col = 0
        line.chars.each do |key|
          if (not items[key].nil?) and (not items[key].strip().empty?)
            item = McResource.from(items[key])
            @resource_loader.load(item)
            set_ingredient(row, col, render_item(item))
          elsif key != ' '
            LOG.error("Unknown recipe pattern key '#{key}' in #{recipe}")
          end
          col += 1
        end
        row += 1
      end

      product_item = McResource.from(recipe["result"]["id"])
      @resource_loader.load(product_item)
      @product = render_item(product_item)
    end

  end
end