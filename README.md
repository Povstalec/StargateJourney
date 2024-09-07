The documentation is deployed on https://povstalec.github.io/StargateJourney/

Used template: [Just the docs](https://just-the-docs.com/)

# Local testing
[Testing your GitHub Pages site locally with Jekyll](https://docs.github.com/en/pages/setting-up-a-github-pages-site-with-jekyll/testing-your-github-pages-site-locally-with-jekyll)  

Ubuntu installation
```bash
# install dependencies
sudo apt install ruby ruby-dev ruby-bundler
# change the gem home directory for non-root users (otherwise it defaults to /var)
export GEM_HOME=$HOME/.gem
# make the change "permanent"
echo 'export GEM_HOME=$HOME/.gem' >> $HOME/.bashrc
# build the site
bundle install
```

example `run.sh` for bash
```bash
#!/bin/bash
# you should clear the _site directory on script restart as incremental build is used
# rm -R ./_site
# you may also need to use --force_polling
bundle exec jekyll serve --livereload --incremental
```

# GitHub setup

- Repository settings > Pages
    - Source: GitHub Actions

in the file [_config.yml](/_config.yml) set `url`