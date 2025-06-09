module Recipe
  class CraftingRecipe < Liquid::Tag
    include Jekyll::Filters::URLFilters
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

    # @param context [Liquid::Context] The template context
    def render(context)
      @context = context
      @lang = Lang.new(context)
      unless @attributes["item"]
        raise "Recipe Crafting Tag usage error: missing item attribute with an item id"
      end
      load_recipes
      load_item_recipe(McResource.from(@attributes["item"]))

      render_crafting_table
    end

    # @param str [String] Text to strip single and double quotes from
    # @return [String]
    def strip_quotes(str)
      str.gsub(/(^")|("$)/,'').gsub(/(^')|('$)/, "")
    end

    # flattens sgjourney recipes folder for easy lookups
    def load_recipes
      @recipes = {}
      dirs = [File.join(IMPLEMENTATION_BRANCH, 'src/main/resources/data/sgjourney/recipe')]
      while dirs.any?
        dir = dirs.shift
        Dir.each_child(dir) do |entry|
          path = File.join(dir, entry)
          if File.file?(path)
            @recipes[entry.gsub(/(.json)$/, "")] = path
          else
            dirs.push(path)
          end
        end
      end
      LOG.debug("Loaded #{@recipes.count} recipes for sgjourney")
    end

    # @param item_name [String]
    def load_sgjourney_item(item_name)
      item_file = "#{item_name}.png"
      jekyll_assets_dir = File.join(PROJECT_DIRECTORY, RELATIVE_JEKYLL_CRAFTING_ASSETS)
      item_img = File.join(jekyll_assets_dir, item_file)
      unless File.exist?(item_img)
        puts "Missing file #{item_img}"
        impl_assets = File.join(IMPLEMENTATION_BRANCH, 'src/main/resources/assets/sgjourney/textures/item/')
        impl_item_img = File.join(impl_assets, item_file)
        if File.exist?(impl_item_img)
          puts "Copying #{impl_item_img} to #{item_img}"
          FileUtils.mkdir_p(jekyll_assets_dir)
          FileUtils.cp(impl_item_img, item_img)
        else
          raise "Item missing texture for crafting in implementation: #{item_name} (#{impl_item_img})"
        end
      end
    end

    # @param resource [McResource]
    def load_item(resource)
      case resource.namespace
      when 'sgjourney'
        load_sgjourney_item(resource.name)
      when 'minecraft'
      else
        # TODO
      end
    end

    # @param resource [McResource]
    def load_item_recipe(resource)
      if resource.namespace != "sgjourney"
        raise "Recipe Crafting Tag does not support recipes from other namespaces than sgjourney"
      end

      recipe_file = @recipes[resource.name]
      unless recipe_file
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

    # @param resource [McResource] The resource to render
    def render_item(resource)
      title = @lang.translate(resource)
      description = ""
      if title.nil?
        title = resource.to_s
        description = "missing translation"
      end
      file = "assets/img/mcui/questionmark.png"
      case resource.namespace
      when 'sgjourney'
        file = File.join(RELATIVE_JEKYLL_CRAFTING_ASSETS, resource.name + ".png")
      when 'minecraft'
        file = "https://minecraft.wiki/images/ItemSprite_" + resource.name + ".png"
      else
        LOG.warn("Missing translation for item/block: #{resource}")
      end

      <<~HTML
        <span class="invslot-item invslot-item-image" data-minetip-title="#{title}" data-minetip-text="#{description}">
          <a href=""><img src="#{absolute_url(file)}"></a>
        </span>
      HTML
        .gsub(/>\s+</, '><').strip
    end

    def process_crafting_shaped(recipe)
      # TODO: handle tags
      items = {}
      recipe["key"].each do |key, value| # TODO this will need an upgrade for newer versions
        items[key] = value["item"].freeze || ("#" + value["tag"]).freeze
      end

      row = 0
      recipe["pattern"].each do |line|
        col = 0
        line.chars.each do |key|
          if (not items[key].nil?) and (not items[key].strip().empty?)
            item = McResource.from(items[key])
            load_item(item)
            set_ingredient(row, col, render_item(item))
          end
          col += 1
        end
        row += 1
      end

      product_item = McResource.from(recipe["result"]["id"])
      load_item(product_item)
      @product = render_item(product_item)
    end

  end
end