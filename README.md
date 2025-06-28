# Stargate Journey documentation
The documentation is deployed on https://povstalec.github.io/StargateJourney/

Used template: [Just the docs](https://just-the-docs.com/)

## Local development setup
1. Install ruby and jekyll  
   [Testing your GitHub Pages site locally with Jekyll](https://docs.github.com/en/pages/setting-up-a-github-pages-site-with-jekyll/testing-your-github-pages-site-locally-with-jekyll)  
Ubuntu installation
```bash
# install dependencies
sudo apt install ruby ruby-dev ruby-bundler
# change the gem home directory for non-root users (otherwise it defaults to /var)
export GEM_HOME=$HOME/.gem
# make the change "permanent"
echo 'export GEM_HOME=$HOME/.gem' >> $HOME/.bashrc
```
2. Clone the repository  
```bash
git clone -b documentation git@github.com:Povstalec/StargateJourney.git
```
3. Build the site
```bash
cd StargateJourney
# build the bundle
bundle install
```
3. Clone an implementation branch into the `implementation_branch` directory (those files will be used for assets sourcing - recipes, item textures)  
```bash
git clone -b main git@github.com:Povstalec/StargateJourney.git implementation_branch
```
4. Serve the site locally (it might be required to let the site build twice to load dynamically added assets from the implementation branch)  
```bash
# To clear the current build, delete the _site directory
# You may also need to use --force_polling if you have issues with livereload (e.g. on WSL)
bundle exec jekyll serve --livereload
```

## Development notes
Use '-' instead of '_' in the page file (.md) names.

- **Per site styles and scripts:**  
You can use `custom_css: "/assets/css/computercraft.css"` in the [Front Matter](https://jekyllrb.com/docs/front-matter/)
with a path to css file in the `/assets/css/` directory and the styles will be included in the site header.
Note that even if you write the source in SCSS, you need to reference the resulting CSS file in the front matter.
The same applies to scripts, you can use `custom_js: "/assets/js/computercraft.js"`.

- **Custom right sidebar with table of contents:**  
The site uses custom layout (`_layouts/default.html`) that includes the right sidebar HTML (`_includes/components/right_bar.html`).
Additionally, the custom header (`_includes/head_custom.html`) includes the script.
Styles (`_sass/custom/right-bar.scss`) are included from the custom SCSS entry file (`_sass/custom.scss`) used by the JustTheDocs theme.
It uses customized version of `_includes/toc.html` with `jekyll-toc` plugin to generate the table of contents.

- **`<details>` element auto-expansion and linking:**  
`_includes/js/open-details.js` implements processing of `<details>` elements on each page load, automatically generates ids (if not present).
Changes the site hash in the URL when a `<details>` element is expanded, and removes it when collapsed.
When a page is loaded with a hash pointing to a `<details>` element, it expands the element and scrolls to it.

- **Custom jekyll plugins hack**
By default, GitHub Pages does not allow custom jekyll plugins.
However since we are building the site using custom GitHub Action workflow,
we use the [github-pages-unsafe plugin](https://github.com/OpenKneeboard/OpenKneeboard/blob/f44354d5a3021814d8dc056f4f98b61116d1ccd1/docs/_plugins/github-pages-unsafe.rb) 
that bypasses the restrictions by the GitHub Pages gem and allows us to use custom plugins in `_plugins/` directory.

- **Minecraft Recipes plugin**:
A custom jekyll plugin (`_plugins/recipe.rb`) is used to generate the recipes.
See the file for details about the implementation and usage.  
  - **Warning:** The main implementation branch is used, if the game version on it changes, it is required to reflect the change
  - in the `_plugins/recipe/constants.rb` file in the `RECIPE_GAME_VERSION` property.  
  If you want to change the implementation branch, change it in the GitHub workflow file and update this readme file.

- **Block assets (images)**:
For instructions where to get image of in-game blocks, see the `_plugins/recipe.rb` file.

# License

Uses MCUI created by Valgard  
https://github.com/Valgard/mcui  
Licensed under the MIT License  
Related files: 
```
/_sass/custom/mcui.scss
/assets/img/mcui
/assets/fonts/minecraft.eot
/assets/fonts/minecraft.ttf
/assets/fonts/minecraft.woff
```

Uses Jekyll-TOC created by Vladimir "allejo" Jimenez  
https://github.com/allejo/jekyll-toc  
(c) 2017 Vladimir "allejo" Jimenez  
Uses modified version of [toc.html](https://github.com/allejo/jekyll-toc/blob/master/_includes/toc.html) at `_includes/toc.html`
