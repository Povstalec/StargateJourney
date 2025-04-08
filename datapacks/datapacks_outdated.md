---
title: Datapacks - outdated
nav_order: 0
parent: Datapacks and Resourcepacks
---

{: .note }
This content was migrated from the old wiki and is awaiting an update.

1. Table of Contents
{:toc}

One of the core features of Stargate Journey is its reliance on [Datapacks](https://minecraft.wiki/w/Data_pack). When used well, Datapacks can serve as powerful tools to customize your gameplay experience. Along with features from Vanilla Minecraft that can be customized through the use of Datapacks, Stargate Journey allows the following to be added:

* Address Tables
* [Symbols / Symbol Sets / Points of Origin]({{ site.baseurl }}/stargate_network/stargate/#symbols)
* [Solar Systems]({{ site.baseurl }}/stargate_network/solar_system/)
* [Galaxies]({{ site.baseurl }}/stargate_network/galaxy/)
* Stargate Variants (v0.6.9+)

# Installing an existing Datapack
There is a well explained guide on the Minecraft Wiki: https://minecraft.wiki/w/Tutorials/Installing_a_data_pack

# Creating a new Datapack
It is very useful to at least understand the basics of Datapacks when trying to create one, Minecraft Wiki has a tutorial for that as well: https://minecraft.wiki/w/Tutorials/Creating_a_data_pack

## Example Datapacks
Some example Datapacks can be found here: https://github.com/Povstalec/StargateJourney-Datapacks
> Please note that some of the Datapacks may not be up to date

A fully functional example Datapack that adds new Point of Origin, Symbols, Symbol Set, Solar System, Galaxy and Dimension can be found [here](https://github.com/Povstalec/StargateJourney-Datapacks/tree/main/Stargate%20Journey%20Example%20Dimension/Example%20Datapack) and the Resourcepack to go along with the Datapack can be found [here](https://github.com/Povstalec/StargateJourney-Datapacks/tree/main/Stargate%20Journey%20Example%20Dimension/Example%20Resource%20Pack).
> Note that the Datapack was made for Minecraft version 1.20.1, sgjourney version 0.6.8 and it is not 100% guaranteed it will work with other (especially earlier) versions

## Textures
*!!!IMPORTANT!!!*  
Please note that Datapacks alone CAN'T load textures. If at any point you are adding textures to your Datapack (for example, when adding new Symbols), the textures cannot be added to the Datapack folder itself, but rather must be added to a separate Resourcepack, which you can then use on your client, or upload it to Server resources which will distribute it to other clients automatically.

## Guides

* [Adding a custom Point of Origin](#creating-custom-points-of-origin-poo)
* [Adding custom Symbols](#creating-custom-symbols)
* [Adding custom Symbol Sets](#creating-custom-symbol-sets)
* [Adding a Dimension to the Stargate Network](#adding-a-dimension-to-stargate-network)
    * [Making Stargate Pedestals generate inside a Dimension](#making-stargate-pedestals-generate-inside-a-dimension)
    * [Creating a custom Solar System for your Dimension(s)](#creating-a-custom-solar-system-for-your-dimensions)
    * [Creating a custom Galaxy](#creating-a-custom-galaxy)

## Creating custom Points of Origin (PoO)
1. Create a new folder called **point_of_origin** in `/data/<namespace>/sgjourney`
2. Create a new .json file with the name of your PoO (e.g. **giza.json**)
3. The contents of your .json file should look like this:
```json
{
	"name": "point_of_origin.sgjourney.giza",
	"texture": "sgjourney:milky_way/points_of_origin/giza.png",
	"generated_galaxies":
	[
		"sgjourney:milky_way"
	]
}
```
* `name` is the name of the Point of Origin. The name is translatable, so add it to whichever languages you want your Datapack to support file inside the assets folder.
* `texture` refers to the texture path. By default, the texture path starts in `assets/<namespace>/textures/symbols`. The <namespace> will be replaced by the replaced with whatever namespace (the part before : ) your datapack is using, in this case it's *sgjourney* and the second part (after : ) will be appended to the end of the default texture path.
* `generated_galaxies` is a list of galaxies this Point of Origin can generate in randomly. For example, if a new Stargate is placed in the Overworld, which is in the Milky Way, the above mentioned Giza Point of Origin may generate on the Stargate.

## Creating custom Symbol Sets
1. Create a new folder called **symbol_sets** in `/data/<namespace>/sgjourney`
2. Create a new .json file with the name of your Symbols (e.g. **galaxy_milky_way.json**)
3. The contents of your .json file should look like this:
```json
{
	"name": "symbol_set.sgjourney.galaxy_milky_way",
	"texture": "sgjourney:milky_way/galaxy_milky_way.png",
	"size": 38
}
```
* `name` is the common name for the Symbol Set. The name is translatable, so add it to whichever languages you want your Datapack to support file inside the assets folder.
* `texture` refers to the texture path of the file with all of your symbols. By default, the texture path starts in `assets/<namespace>/textures/symbols`. The <namespace> will be replaced by the replaced with whatever namespace (the part before : ) your datapack is using, in this case it's *sgjourney* and the second part (after : ) will be appended to the end of the default texture path.
* `size` refers to the total number of symbols in this Symbol Set

## Creating custom Symbols
1. Create a new folder called **symbols** in `/data/<namespace>/sgjourney`
2. Create a new .json file with the name of your Symbols (e.g. **tauri.json**)
3. The contents of your .json file should look like this:
```json
{
	"name": "symbols.sgjourney.tauri",
	"symbol_set": "sgjourney:galaxy_milky_way",
	"texture": "sgjourney:milky_way/tauri.png",
	"size": 38
}
```
* `name` is the common name for the Symbols. The name is translatable, so add it to whichever languages you want your Datapack to support file inside the assets folder.
* `symbol_set` refers to the set of symbols these symbols belong to. The symbol set is usually the same for all symbols within a galaxy.
* `texture` refers to the texture path of the file with all of your symbols. By default, the texture path starts in `assets/<namespace>/textures/symbols`. The <namespace> will be replaced by the replaced with whatever namespace (the part before : ) your datapack is using, in this case it's *sgjourney* and the second part (after : ) will be appended to the end of the default texture path.
* `size` refers to the total number of symbols in this Symbol Set

## Adding a Dimension to Stargate Network
Stargate Network will automatically add any dimension to a randomly generated Solar System, so there is no need to go out of your way to create a new Solar System for each dimension you wish to play with. However, these dimensions won't have Stargates generated inside them and thus, you will need to bring your own Stargate with you.

### Making Stargate Pedestals generate inside a Dimension
To make a Stargate Pedestal generate inside your chosen Dimension, you must add ALL of the Biomes that generate in said Dimension to a list of Biomes in which the Stargate Pedestals generate. There are multiple Stargate Pedestal variants and to have them generate, you will need to add one or more of these files inside `/data/sgjourney/tags/worldgen/biome/has_structure/stargate_pedestal`:

* `stargate_pedestal_biomes.json` - The default look of the Stargate Pedestal, made out of stone
* `stargate_pedestal_badlands_biomes.json` - Stargate Pedestal made out of red sandstone
* `stargate_pedestal_deep_dark_biomes.json` - Stargate Pedestal made out of deepslate
* `stargate_pedestal_desert_biomes.json` - Stargate Pedestal made out of sandstone
* `stargate_pedestal_jungle_biomes.json` - Stargate Pedestal made out of stone with vines hanging on it
* `stargate_pedestal_mushroom_biomes.json` - Stargate Pedestal made out of mycelium
* `stargate_pedestal_snow_biomes.json` - Stargate Pedestal made out of stone, snow and ice
* `stargate_pedestal_chulak_biomes.json` - Stargate Pedestal variation seen on Chulak

The structure inside each of the files should look like this:
```json
{
	"replace": false,
	"values":
	[
		"<namespace_1>:name_of_your_biome_1",
		"<namespace_2>:name_of_your_biome_2"
	]
}
```

### Creating a custom Solar System for your Dimension(s)
1. Create a new folder called **solar_system** in `/data/<namespace>/sgjourney`
2. Create a new .json file with the name of your Solar System (e.g. **terra.json**)
3. The contents of your .json file should look like this:
```json
{
	"name": "solar_system.sgjourney.terra",
	"symbols": "sgjourney:terra",
	"symbol_prefix": 1,
	"extragalactic_address": {"address": [1, 35, 4, 31, 15, 30, 32], "randomizable": true},
	"addresses":
	[
		{"galaxy": "sgjourney:milky_way", "address": {"address": [27, 25, 4, 35, 10, 28], "randomizable": true}}
	],

	"point_of_origin": "sgjourney:terra",

	"dimensions":
	[
		"minecraft:overworld",
		"ad_astra:earth_orbit",

		"ad_astra:moon",
		"ad_astra:moon_orbit",

		"ad_astra:mars",
		"ad_astra:mars_orbit",

		"ad_astra:mercury",
		"ad_astra:mercury_orbit",

		"ad_astra:venus",
		"ad_astra:venus_orbit"
	]
}
```

* `name` is the name of the Solar System. The name is translatable, so add it to whichever languages you want your Datapack to support file inside the assets folder.
* `symbols` refers to the Symbols used by this Solar System.
* `symbol_prefix` is the first Symbol used when generating an Extragalactic Address in the case that the config is set to generating randomized Addresses. A Prefix Symbol is usually same for the entire Galaxy. (Milky Way Prefix = 1, Pegasus Prefix = 18)
* `randomizable` controls whether or not the Extragalactic Address of this Solar System can be randomized in the case that the config is set to generating randomized Addresses.
* `extragalactic_address` is an array consisting of 7 numbers with the lowest allowed value being 1. It represents the Solar System's 8-chevron Address.
* `addresses` is a list of Addresses of the Solar System in each listed Galaxy. Each Address is created with a pair consisting of `galaxy`, which holds the registry name of the Galaxy and `address`, which is itself a pair consisting of `address` (an array consisting of 6 numbers, with the lowest allowed value being 1) and * `randomizable`, which controls whether or not the Address of this Solar System can be randomized in the case that the config is set to generating randomized Addresses..
* `point_of_origin` refers to the Point of Origin of the Solar System.
* `dimensions` is a list of Dimensions in the Solar System. These Dimensions will all share one address.

## Creating a custom Galaxy
1. Create a new folder called **galaxy** in `/data/<namespace>/sgjourney`
2. Create a new .json file with the name of your Galaxy (e.g. **milky_way.json**)
3. The contents of your .json file should look like this:
```json
{
	"name": "galaxy.sgjourney.milky_way",
	"type": "sgjourney:medium_galaxy",
	"default_symbols": "sgjourney:galaxy_milky_way"
}
```
* `name` is the name of the Galaxy. The name is translatable, so add it to whichever languages you want your Datapack to support file inside the assets folder.
* `type` currently specifies the [size of the Galaxy]({{ site.baseurl }}/stargate_network/galaxy/#galaxy-size).
* `default_symbols` refers to the Symbols which are used by randomly generated Solar Systems inside this galaxy.