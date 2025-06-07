# https://perseuslynx.dev/blog/jekyll-first-plugin
# https://jekyllrb.com/docs/plugins/tags/

LOG = Jekyll.logger

module Jekyll
  class RecipeCrafting < Liquid::Tag
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
            <span class="invslot invslot-large"></span>
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
      unless @attributes["item"]
        puts this.inspect
        raise "Recipe Crafting Tag usage error: missing item attribute with an item id"
      end
      @site_source = context.registers[:site].source
      @implementation_branch = File.join(@site_source, "/implementation_branch")
      @jekyll_assets = "assets/img/items/crafting"
      load_recipes
      load_item_recipe(@attributes["item"].split(":", 2))

      render_crafting_table
    end

    def strip_quotes(str)
      str.gsub(/(^")|("$)/,'').gsub(/(^')|('$)/, "")
    end

    # flattens sgjourney recipes folder for easy lookups
    def load_recipes
      @recipes = {}
      dirs = [File.join(@implementation_branch, 'src/main/resources/data/sgjourney/recipe')]
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

    def load_sgjourney_item(item_name)
      item_file = item_name + ".png"
      jekyll_assets_dir = File.join(@site_source, @jekyll_assets)
      item_img = File.join(jekyll_assets_dir, item_file)
      unless File.exist?(item_img)
        puts "Missing file #{item_img}"
        impl_assets = File.join(@implementation_branch, 'src/main/resources/assets/sgjourney/textures/item/')
        impl_item_img = File.join(impl_assets, item_file)
        if File.exist?(impl_item_img)
          puts "Copying #{impl_item_img} to #{item_img}"
          FileUtils.cp(impl_item_img, item_img)
        else
          raise "Item missing texture for crafting in implementation: #{item_name} (#{impl_item_img})"
        end
      end
    end

    def load_item(nspace, item_name)
      case nspace
      when 'sgjourney'
        load_sgjourney_item(item_name.freeze)
      when 'minecraft'
      else
        # TODO
      end
    end

    # @param item [Array] [namespace, item_name]
    def load_item_recipe(item)
      if item[0] != "sgjourney"
        puts this.inspect
        raise "Recipe Crafting Tag does not support recipes from other namespaces than sgjourney"
      end

      recipe_file = @recipes[item[1]]
      unless recipe_file
        raise "Crafting recipe for #{item[0]}:#{item[1]} was not found"
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

    def render_item(nspace, item_name)
      case nspace
      when 'sgjourney'
        return <<~HTML
            <span class="invslot-item invslot-item-image" data-minetip-title="#{item_name}">
              <a href="#"><img src="#{File.join(@jekyll_assets, item_name + ".png")}"></a>
            </span>
        HTML
          .gsub(/>\s+</, '><').strip
      end
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
            item = items[key].split(":", 2)
            nspace = item[0]
            item_name = item[1]
            load_item(nspace, item_name)
            set_ingredient(row, col, render_item(nspace, item_name))
          end
          col += 1
        end
        row += 1
      end
    end

  end
end

Liquid::Template.register_tag('minecraft_recipe_crafting', Jekyll::RecipeCrafting)