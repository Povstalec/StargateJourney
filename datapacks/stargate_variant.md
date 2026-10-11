---
title: Stargate Variant
nav_order: 11
parent: Datapacks and Resourcepacks
---

# Stargate Variant

Stargate Variant system allows for the visual (and sound) customization of Stargates without changing their functionality.

To apply a Stargate Variant to a Stargate, you will need to right-click on the Stargate with the Stargate Variant Crystal of the variant you want to apply. If the variant is compatible with the Stargate type, it will be applied.

To remove a Stargate Variant from a Stargate, you will need to right-click on the Stargate with a blank Stargate Variant Crystal.

# Datapack Stargate Variant

Stargate Variants need to be defined by both a datapack (so the server knows the variant exists) and a resourcepack (so the client knows what to render/what sounds to play).

This shows how they are defined in a datapack.

Stargate Variant files are located in the `data/<namespace>/sgjourney/stargate_variant` folder.  
`<namespace>` is the identificator of the given Mod/Datapack. For example, for Stargate Journey the `<namespace>` would be `sgjourney`,
making the whole path `data/sgjourney/sgjourney/stargate_variant`.

Datapack Stargate Variants for [Stargate Journey](https://github.com/Povstalec/StargateJourney/tree/main/src/main/resources/data/sgjourney/sgjourney/stargate_variant)

## Example Stargate Variant

```json-doc
{
	// The base Stargate type this Stargate Variant is compatible with
	"base_stargate": "sgjourney:milky_way_stargate",
	
	// A reference to the Stargate Variant resourcepack file the game should use on the client side.
	"client_variant": "sgjourney:movie"
}
```

# Resourcepack Stargate Variant

Stargate Variants need to be defined by both a datapack (so the server knows the variant exists) and a resourcepack (so the client knows what to render/what sounds to play).

This shows how they are defined in a resourcepack.

Stargate Variant files are located in the `assets/<namespace>/sgjourney/stargate_variant` folder.  
`<namespace>` is the identificator of the given Mod/Datapack. For example, for Stargate Journey the `<namespace>` would be `sgjourney`,
making the whole path `assets/sgjourney/sgjourney/stargate_variant`.

Resourcepack Stargate Variants for [Stargate Journey](https://github.com/Povstalec/StargateJourney/tree/main/src/main/resources/assets/sgjourney/sgjourney/stargate_variant)

## Example Stargate Variant

{: .highlight }
`// Blah blah blah` is considered a comment and is not a valid part of the json structure.  
Some of the referenced textures or sounds are only used for the purposes of showcasing what can be used in the definitions, and as such, may not actually exist in the mod files.  
Note that this example shows the combined definitions of every property of every Stargate Variant combined to explain what they all do. If a property is limited to a certain type of Stargate, it will be noted in the comment.  
Some of the listed properties are optional, meaning they don't necessarily need to be defined for the Stargate Variant to work. This example will differentiate between them by prefixing comments with either REQUIRED or OPTIONAL.

```json-doc
{
	//REQUIRED The texture of the Stargate Variant
	"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_stargate.png",
	//OPTIONAL The texture of the Stargate Variant when it's being dialed, which is rendered as an overlay over the base texture (currently it only applies to chevrons)
	"encoded_texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_stargate_encoded.png",
	//REQUIRED The texture of the Stargate Variant when it's connected, which is rendered as an overlay over the base texture (currently it only applies to chevrons)
	"engaged_texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_stargate_engaged.png",
	
	//REQUIRED Definition of the base wormhole (there is also the optional definition of a shiny_wormhole, which is typically what players see by default)
	"wormhole":
	{
		"event_horizon": //REQUIRED Texture of the Event Horizon
		{
			"front": // How the Event Horizon looks like when you're looking at it from the front
			{
				//REQUIRED The texture of the Event Horizon
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_event_horizon.png",
				// If the Event Horizon is meant to be animated, the texture is split into multiple rows and columns, creating sections that are used for a "stop-motion" animation
				//REQUIRED This specifies how many rows the texture should be split into for rendering
				"rows": 32, 
				//REQUIRED This specifies how many rows the texture should be split into for rendering
				"columns": 1,
				//REQUIRED This specifies how many frames the Event Horizon animation goes through, until it restarts at the beginning (typically this number shouldn't be more than rows * columns)
				"frames": 32,
				//OPTIONAL The color of the Event Horizon, defined by 0-255 values (suports alternate definition, as shown in "back")
				"rgba": { "r": 255, "g": 255, "b": 255, "a": 255 }
			},
			"back": // How the Event Horizon looks like when you're looking at it from the back, definition is identical to "front"
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_event_horizon.png",
				"rows": 32,
				"columns": 1,
				"frames": 32,
				//OPTIONAL The color of the Event Horizon, defined by 0.0-1.0 percentages (suports alternate definition, as shown in "front")
				"rgba": { "red": 1.0, "green": 1.0, "blue": 1.0, "alpha": 0.75 }
			}
		},
		"event_horizon_unstable": //OPTIONAL Texture of the Event Horizon when it flickers while it's unstable, definition is identical to "event_horizon"
		{
			// If both sides of the Event Horizon are the same, you can omit the "front" and "back" and just define it as the following
			"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_event_horizon_unstable.png",
			"rows": 32,
			"columns": 1,
			"frames": 32,
			"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 0.75 }
		},
		"kawoosh": //OPTIONAL Texture of the kawoosh that appears when the gate starts connecting, definition is identical to "event_horizon"
		{
			"front":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_vortex.png",
				"rows": 32,
				"columns": 5,
				"frames": 160,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			},
			"back":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_vortex.png",
				"rows": 32,
				"columns": 5,
				"frames": 160,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			}
		},
		"strudel": //OPTIONAL Texture of the vortex (officially called "strudel") that appears behind the gate when its connected, definition is identical to "event_horizon"
		{
			"front":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_vortex.png",
				"rows": 32,
				"columns": 5,
				"frames": 160,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			},
			"back":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_vortex.png",
				"rows": 32,
				"columns": 5,
				"frames": 160,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			}
		},
		// Values defining the specific timings of the animation that's played while the Stargate is disconnecting
		//REQUIRED wait_ticks - How many ticks to wait before starting the animation
		//REQUIRED fade_in_ticks - How many ticks it takes before the first frame of the animation fades in fully (once this number is reached, the original Event Horizon texture disappears)
		//REQUIRED stable_ticks - How many ticks the animated texture should stay fully visible for
		//REQUIRED fade_out_ticks - How many ticks it takes for the animated texture to fade out
		"disconnect_ticks": { "wait_ticks": 16, "fade_in_ticks": 10, "stable_ticks": 0, "fade_out_ticks": 20 },
		
		"disconnect": //OPTIONAL Texture of the animation that will be overlayed over the event horizon while the Stargate is disconnecting, defintion is identical to "event_horizon"
		{
			"front":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_disconnect.png",
				"rows": 20,
				"columns": 1,
				"frames": 20,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			},
			"back":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_disconnect.png",
				"rows": 20,
				"columns": 1,
				"frames": 20,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			}
		}
	},
	
	//OPTIONAL Definition of the shiny wormhole (this one is typically what players see by default), the definition itself is identical to "wormhole"
	"shiny_wormhole":
	{
		"event_horizon":
		{
			"front":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_event_horizon_shiny.png",
				"rows": 32,
				"columns": 1,
				"frames": 32,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			},
			"back":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_event_horizon_shiny.png",
				"rows": 32,
				"columns": 1,
				"frames": 32,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 0.75 }
			}
		},
		"event_horizon_unstable":
		{
			"front":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_event_horizon_shiny_unstable.png",
				"rows": 32,
				"columns": 1,
				"frames": 32,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 0.75 }
			},
			"back":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_event_horizon_shiny_unstable.png",
				"rows": 32,
				"columns": 1,
				"frames": 32,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 0.75 }
			}
		},
		"strudel":
		{
			"front":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_vortex_shiny.png",
				"rows": 32,
				"columns": 5,
				"frames": 160,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			},
			"back":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_vortex_shiny.png",
				"rows": 32,
				"columns": 5,
				"frames": 160,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			}
		},
		"disconnect_ticks": { "wait_ticks": 16, "fade_in_ticks": 10, "stable_ticks": 0, "fade_out_ticks": 20 },
		"disconnect":
		{
			"front":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_disconnect.png",
				"rows": 20,
				"columns": 1,
				"frames": 20,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			},
			"back":
			{
				"texture": "sgjourney:textures/entity/stargate/milky_way/milky_way_disconnect.png",
				"rows": 20,
				"columns": 1,
				"frames": 20,
				"rgba": { "red": 1.0, "blue": 1.0, "green": 1.0, "alpha": 1.0 }
			}
		}
	},
	
	"symbols": //REQUIRED This section specifies how symbols look on this Stargate Variant
	{
		//REQUIRED Symbols when the Stargate isn't doing anything (this and all the following colors support 0.0-1.0 percentage color definitions as shown in "event_horizon")
		// There is an optional field called "glow" (shown below), which, when true, makes the symbols glow in the dark
		"idle_symbols": { "color": {"r": 48, "g": 49, "b": 63} },
		
		//OPTIONAL(same as "engaged_symbols" by default) When the Stargate is dialing, any symbols in the address are rendered like this
		"encoded_symbols": { "color": {"r": 125, "g": 255, "b": 0}, "glow": true },
		//OPTIONAL(same as "encoded_symbols" by default) When the Stargate is dialing, any symbols NOT in the address are rendered like this
		"encoded_symbols_incoming": { "color": {"r": 125, "g": 255, "b": 0}, "glow": true },
		//OPTIONAL(same as "encoded_symbols" by default) When a MILKY WAY Stargate has its chevron open, the symbol that's being encoded is rendered like this
		// When a PEGASUS/ANDROMEDA Stargate is dialing and the symbol is travelling around the ring, it's rendered like this
		"encoding_symbol": { "color": {"r": 255, "g": 255, "b": 0}, "glow": true },
		//OPTIONAL(same as "idle_symbols" by default) When the Stargate is being dialed during an incoming connection, all of its symbols are rendered like this (except for the case of Pegasus Stargate, which has a booting-up animation for them)
		"unencoded_symbols": { "color": {"r": 0, "g": 0, "b": 0, "a": 0} },
		//OPTIONAL(same as "unencoded_symbols" by default) When a PEGASUS Stargate is being dialed during an incoming connection, you know, the booting-up animation the symbols get, then any symbols the animation hasn't reached are rendered like this
		"unencoded_symbols_incoming": { "color": {"r": 0, "g": 0, "b": 0, "a": 0} },
		
		//OPTIONAL(same as "idle_symbols" by default) When a Stargate finishes dialing out, any symbols in the dialed address are rendered like this during an outgoing connection
		"engaged_symbols": { "color": {"r": 0, "g": 255, "b": 200}, "glow": true },
		//OPTIONAL(same as "engaged_symbols" by default) When a Stargate finishes dialing out, any symbols not in the dialed address are rendered like this during an outgoing connection
		"engaged_symbols_incoming": { "color": {"r": 0, "g": 255, "b": 200}, "glow": true },
		//OPTIONAL(same as "idle_symbols" by default) When the Stargate finishes being dialed, all of its symbols are rendered like this during an incoming connection 
		"unengaged_symbols": { "color": {"r": 0, "g": 0, "b": 0, "a": 0} }
	},
	
	"stargate_model": //REQUIRED(Only for MILKY WAY Stargate), OPTIONAL(Only for PEGASUS and ANDROMEDA Stargate) This section specifies visual model adjustments this Stargate Variant makes to the Stargate
	{
		// The following 3 options only apply to the GenericStargateModel (which includes Milky Way, Pegasus and Andromeda Stargates)
		//OPTIONAL If true, the Stargate will open and close each individual chevron being encoded (Milky Way Stargate only)
		"movie_chevron_locking": true,
		//OPTIONAL If true, the Stargate will render with the top chevron (also called registry clamp) from the original Stargate movie
		"movie_primary_chevron": true,
		//OPTIONAL If true, both the front and the back of the chevron will be raised when a chevron is encoded
		"raise_back_chevrons": false
	},
	
	"chevron_engaged_sounds": //REQUIRED The sound played when a chevron is engaged
	{
		// The sound files that each individual chevron can play (all except "default" are OPTIONAL, so if any are left out, the "default" will play instead)
		"default": "sgjourney:milky_way_chevron_engage",
		"primary_chevron": "sgjourney:milky_way_chevron_engage_0",
		"chevron_1": "sgjourney:milky_way_chevron_engage_1",
		"chevron_2": "sgjourney:milky_way_chevron_engage_2",
		"chevron_3": "sgjourney:milky_way_chevron_engage_3",
		"chevron_4": "sgjourney:milky_way_chevron_engage_4",
		"chevron_5": "sgjourney:milky_way_chevron_engage_5",
		"chevron_6": "sgjourney:milky_way_chevron_engage_6",
		"chevron_7": "sgjourney:milky_way_chevron_engage_7",
		"chevron_8": "sgjourney:milky_way_chevron_engage_8"
	},
	
	"chevron_incoming_sounds": //REQUIRED The sound played when a chevron is engaged during an incoming connection, definition is identical to "chevron_engaged_sounds"
	{
		"default": "sgjourney:milky_way_chevron_engage"
	},
	
	"chevron_open_sounds": //REQUIRED The sound played when a MILKY WAY Stargate's chevron is opened, definition is identical to "chevron_engaged_sounds"
	{
		"default": "sgjourney:milky_way_chevron_open"
	},
	
	"chevron_encode_sounds": //REQUIRED The sound played when a MILKY WAY Stargate's chevron is encoded, definition is identical to "chevron_engaged_sounds"
	{
		"default": "sgjourney:milky_way_chevron_encode"
	},
	
	"rotation_sounds": // The rotation sounds of the Stargate (does not apply to TOLLAN Stargate, which does not rotate)
	{
		//OPTIONAL The sound played when the rotation is starting (only applies to computer-dialing)
		"rotation_startup_sound": "sgjourney:milky_way_ring_spin_start",
		//OPTIONAL Sound played while the Stargate is rotating
		"rotation_sound": "sgjourney:milky_way_ring_spin",
		//OPTIONAL Sound played when the Stargate rotation is stopped (only applies to computer-dialing)
		"rotation_stop_sound": "sgjourney:milky_way_ring_spin_stop"
	},
	
	"wormhole_sounds": //REQUIRED The rotation sounds of the Stargate's wormhole
	{
		//REQUIRED The sound played when the wormhole opens
		"open": "sgjourney:milky_way_wormhole_open",
		//REQUIRED The idle sound that plays on a loop while the wormhole is open
		"idle": "sgjourney:milky_way_wormhole_idle",
		//REQUIRED The sound played when the wormhole closes
		"close": "sgjourney:milky_way_wormhole_close"
	},
	
	"fail_sounds": //REQUIRED The sound Stargate makes when it runs into an error
	{
		//REQUIRED The default sound played when there's an error (currently there are no other sound for specific error types)
		"default": "sgjourney:milky_way_dial_fail"
	},
	
	//OPTIONAL The sound played when a UNIVERSE Stargate starts dialing (only applies to DHD-dialing)
	"dial_start_sound": "sgjourney:universe_dial_start",
	//OPTIONAL If true, only the front of the UNIVERSE Stargate will rotate
	"only_front_rotates": true
}

```