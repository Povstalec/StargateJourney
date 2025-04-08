---
title: Troubleshooting
nav_order: 20
---

# Troubleshooting
{: .no_toc }

If you can't find a solution to your problem on this page or if one of the solutions below doesn’t work, 
you can visit the [Discord server]({{ site.discord_invite_link }}) and ask there.

1. Table of Contents
{:toc}

## Stargate is not dialing / not disconnecting / causing any trouble
_With access to commands:_  
Use the `/sgjourney stargateNetwork forceStellarUpdate`  
As of 0.6.6 this command can no longer be used in Survival

_Without access to commands, but with access to world files:_  
Go to your `saves/<world name here>/data` folder
Delete `sgjourney-stargate_network.dat`

{: .warning }
> **UNDER NO CIRCUMSTANCES EVER DELETE** `sgjourney-block_enties.dat`.
> It holds info with **locations** of all the block entities, 
> like **Stargates** and **Transport Rings**,
> and deleting it means Stargate Network won't be able to find the Stargates anymore.

After all that, log in to your world again to let the Stargate Network regenerate, and everything should be fine.

___


## I added the mod to an existing world, but Stargate isn't generating.
The Stargate structure can only generate in **new chunks**. 
By default, Stargate structures generate in an area **centered around** the `X = 0` `Z = 0` coordinates, 
and the maximum allowed **offset** from that **center** position is `64` chunks.
Suppose you've already explored the world **before** adding Stargate Journey. 
In that case, you must change the **area offset** that the Stargate structure can generate.
To do this:

Go to the **Common Config** (`config/sgjourney_common.toml`), and under the **Stargate Network Config**, change 
`stargate_generation_center_x_chunk_offset` or `stargate_generation_center_z_chunk_offset` to some number of chunks further away, which you haven't generated yet.

___

# Outdated

## Addresses are randomized (Stargate Journey 0.6.6)
> Addresses are randomized after updating my world's sgjourney to 0.6.6 even though they weren't before.

1. Either:
- Change `use_datapack_addresses` int your config to `true`
- Delete your config altogether and let it regenerate
2. Open your `saves/<world name here>/data` folder  
   Delete `sgjourney-stargate_network_settings.dat`, `sgjourney-stargate_network.dat` and `sgjourney-universe.dat`

{: .warning }
> **UNDER NO CIRCUMSTANCES EVER DELETE** `sgjourney-block_enties.dat`.
> It holds info with **locations** of all the block entities,
> like **Stargates** and **Transport Rings**,
> and deleting it means Stargate Network won't be able to find the Stargates anymore.

## Stargate Journey is causing datapack related crashes

<!-- // TODO: There is no image :D not even on discord -->

Normall you would fix it by just deleting the current Common Stargates datapack and dragging a new one in your datapack folder, 
but since some modpacks (like ATM9) handle datapacks through kubejs, which isn't something I'm specificaly familiar with, 
here's a copy-paste version of what tehgreatdoge who knows how to use it came up with:

================================================================

- This tutorial assumes that you have 0 knowledge about how KubeJS works.

- To get started, open your Minecraft instance’s folder. 
Then go to ./kubejs/data. 
If you see a folder labeled sgjourney, this is (probably) the right tutorial for you.

- If you are using ATM9 you can go ahead and skip this next step.

- Now that you have the folder, 
you will need to verify that there aren’t any other important changes made by the modpack. 
To do so, compare your file structure against the attached image. 
While this won’t 100% guarantee that everything will be alright, it should help prevent any issues.
If your file structure doesn’t exactly match, please open a post in #bugs-and-suggestions with the following info: 
Modpack and modpack version, sgjourney version, common stargates version.
Now that we have verified that the folders match, go ahead and delete the sgjourney folder.

- Now, download the latest version of common stargates for your Minecraft version and open it. 
Inside, there should be a data folder. 
Copy the common_stargates and sgjourney folder from it and paste it into the ./kubejs/data folder. 
You did it! 
If this doesn’t work, please create a post in #bugs-and-suggestions with the previously mentioned info 

================================================================

In case this doesn't work, you can join the Discord Server and try discussing it there with tehgreatedoge directly

[//]: # (// TODO: datapack update tutorial also with KubeJS variant)