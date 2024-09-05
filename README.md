The documentation is deployed on https://povstalec.github.io/StargateJourney/

Used template: [Just the docs](https://just-the-docs.com/)

For local testing: [Testing your GitHub Pages site locally with Jekyll](https://docs.github.com/en/pages/setting-up-a-github-pages-site-with-jekyll/testing-your-github-pages-site-locally-with-jekyll)  

example `run.sh` for bash
```bash
#!/bin/bash
# you should clear the _site directory on script restart as incremental build is used
# rm -R ./_site
bundle exec jekyll serve --livereload --incremental
```

