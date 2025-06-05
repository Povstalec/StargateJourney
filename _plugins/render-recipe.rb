# https://perseuslynx.dev/blog/jekyll-first-plugin
# https://jekyllrb.com/docs/plugins/tags/


module Jekyll
  class RecipeCrafting < Liquid::Tag

    def render_crafting_table
      html = <<~HTML
        <span class="mcui mcui-Crafting_Table pixel-image">
          <span class="mcui-input">
            <span class="mcui-row">
              <span class="invslot"></span>
              <span class="invslot"></span>
              <span class="invslot"></span>
            </span>
            <span class="mcui-row">
              <span class="invslot"></span>
              <span class="invslot"></span>
              <span class="invslot"></span>
            </span>
            <span class="mcui-row">
              <span class="invslot"></span>
              <span class="invslot"></span>
              <span class="invslot"></span>
            </span>
          </span>
          <span class="mcui-arrow"><br></span>
          <span class="mcui-output">
            <span class="invslot invslot-large"></span>
          </span>
        </span>
      HTML

      # Remove all whitespace between tags and print the output
      html.gsub(/>\s+</, '><').strip
    end

    def initialize(tag_name, text, tokens)
      super
      @text = text
    end

    def render(context)
      render_crafting_table
    end
  end
end

Liquid::Template.register_tag('minecraft_recipe_crafting', Jekyll::RecipeCrafting)