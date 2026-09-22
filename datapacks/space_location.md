---
title: Space Location
nav_order: 1
parent: Datapacks and Resourcepacks
---

# space_location
Space Location is an object that represents a single Dimension, providing custom attributes that Stargate Journey can use.

Space Location files are located in the `data/<namespace>/sgjourney/space_location` folder.  
`<namespace>` is the identificator of the given Mod/Datapack. 


{: .warning }
> Note that the Space Location's namespace and name MUST be the same as the namespace and name of the Dimension it's meant to be tied to!


For example, the Space location representing the `minecraft:overworld` Dimension would be located in  
`data/minecraft/sgjourney/space_location/overworld`.


## Example Space Location

Space Locations for [Stargate Journey Dimensions](https://github.com/Povstalec/StargateJourney/tree/main/src/main/resources/data/sgjourney/sgjourney/space_location)
and [Vanilla Minecraft Dimensions](https://github.com/Povstalec/StargateJourney/tree/main/src/main/resources/data/minecraft/sgjourney/space_location)

```json
{
	"in_stargate_network": true,
	"parent_gravity": 0.0,
	"unity_crystals_grow": false,
	
	"point_of_origin": "sgjourney:pontem",
	"symbols": "sgjourney:end",
	"point_of_origin_table": "sgjourney:galaxy_milky_way",
	
	"generate_in_address_tables": false,
	"address_region": "sgjourney:end",
	"preload_stargate": false
}
```
## Fields
- `in_stargate_network`: If false, Stargates will be unable to dial in this Space Location's Dimension and it won't be possible to dial them from outside this Dimension either.
- `parent_gravity`: The strength of the gravitational pull from this planet's parent experienced by Entitie (could be a star or a black hole), primarily intended for Cavum Tenebrae where gravity changes depending on the position of the black hole in the sky.
- `unity_crystals_grow`: If true, Budding Unity Blocks will be able to grow Unity Crystals in this Space Location's Dimension.


- `point_of_origin`: Local [Point of Origin]({{ site.baseurl }}datapacks/point_of_origin) of this Address Region, any placed Stargates with the "Local Point of Origin" tooltip will default to it (Overrides the Point of Origin defined by [Address Region]({{ site.baseurl }}datapacks/address_region)).
- `symbols`: Local [Symbols]({{ site.baseurl }}datapacks/symbols) of this Address Region, any placed Stargates will default to it (Overrides the Symbols defined by [Address Region]({{ site.baseurl }}datapacks/address_region)).
- `point_of_origin_table`: [Point of Origin Table]({{ site.baseurl }}datapacks/point_of_origin_table) specifying the Points of Origin Stargates and Symbol Blocks can receive at random when placed.

- `address_region`: [Address Region]({{ site.baseurl }}datapacks/address_region) this Space Location will be a part of.
- `preload_stargate`: If true, the Stargate Network will load the Preferred Stargate of this Space Location's Dimension when the world is first loaded. This mainly exists for Dimensions like `sgjourney:destiny`, which can only be dialed through a 9-Chevron Address of a Stargate, as 9-Chevron Addresses are only generated once the Stargate is loaded.
- `generate_in_address_tables`: If true (and this Space Location has an [Address Region]({{ site.baseurl }}datapacks/address_region) tied to it, its Address will generate in all [Address Tables]({{ site.baseurl }}datapacks/address_table) that have the `include_generated_addresses` option set to true.


# Space Location Templates

TODO Space Location Templates