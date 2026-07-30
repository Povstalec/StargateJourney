# description from issue: https://github.com/github/pages-gem/issues/926#issuecomment-2377713179
# restores github pages configuration
# source: https://github.com/OpenKneeboard/OpenKneeboard/blob/f44354d5a3021814d8dc056f4f98b61116d1ccd1/docs/_plugins/github-pages-unsafe.rb
require "github-pages"

class << GitHubPages::Configuration
  alias_method :GitHubPages_effective_config, :effective_config
  def effective_config(config)
    orig = config.dup.freeze
    config = GitHubPages_effective_config(config)
    %w[safe whitelist plugins_dir].each do |key|
      config[key] = orig[key]
    end
    config
  end
end

# Pull in the plugins that GitHubPages depends on; we need to do this super-early before any of the hooks start
Jekyll.sites.each do |site|
  GitHubPages::Configuration::set(site)
end