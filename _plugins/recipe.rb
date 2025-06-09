# https://perseuslynx.dev/blog/jekyll-first-plugin
# https://jekyllrb.com/docs/plugins/tags/

module Recipe
  LOG = Jekyll.logger
end

Liquid::Template.register_tag('minecraft_recipe_crafting', Recipe::CraftingRecipe)