---
title: Commands
nav_order: 110
---

{: .note }
This content was migrated from the old wiki and is awaiting an update.

Every command added by Stargate Journey 
starts with `/sgjourney` For example: `/sgjourney stargateNetwork forceStellarUpdate`

1. Table of Contents
{:toc}

___

# Stargate Network

## `/sgjourney stargateNetwork address <dimension>`
Accessible in Survival: `False`

Returns the Address of the selected Dimension in the Galaxy the Dimension the player located in. 
For example, if End Solar System is registered under both the Milky Way and Pegasus Galaxies, 
the output of the command will look as follows:

Player is in the minecraft:overworld Dimension, which is only in the Milky Way Galaxy

    /sgjourney stargateNetwork address minecraft:the_end
     
        The address of minecraft:the_end in sgjourney:milky_way is:
        -13-24-2-19-3-30-

Player is in the minecraft:overworld Dimension, which is only in the Milky Way Galaxy

    /sgjourney stargateNetwork address sgjourney:lantea
    
        sgjourney:lantea is not located in sgjourney:milky_way

Player is in the sgjourney:lantea Dimension, which is only in the Pegasus Galaxy

    /sgjourney stargateNetwork address minecraft:the_end
    
        The address of minecraft:the_end in sgjourney:pegasus is:
        -19-30-6-13-3-24-

Player is in the minecraft:the_end Dimension, which is in both Milky Way and Pegasus Galaxy

    /sgjourney stargateNetwork address minecraft:the_end

        The address of minecraft:the_end in sgjourney:milky_way is:
        -13-24-2-19-3-30-
        The address of minecraft:the_end in sgjourney:pegasus is:
        -19-30-6-13-3-24-

___

## `/sgjourney stargateNetwork extragalacticAddress <dimension>`
Accessible in Survival: `False`

Returns the 8-Chevron Address of the selected Dimension. 
Player is in the `minecraft:overworld` Dimension, which is only in the Milky Way Galaxy

    /sgjourney stargateNetwork address minecraft:the_end

        The extragalactic address of sgjourney:lantea is:
        -18-20-1-15-14-7-19-

___

## `/sgjourney stargateNetwork forceStellarUpdate`
Accessible in Survival: `False`

Forces the Stargate Network to perform a Stellar Update.

___

## `/sgjourney stargateNetwork getAllStargates <dimension>`
Accessible in Survival: `False`

Returns the 9-Chevron Addresses and positions of all Stargates 
that have been registered to the Stargate Network in the selected Dimension.

___

## `/sgjourney stargateNetwork version`
Accessible in Survival: `False`

Returns the current Version of the Stargate Network.

___

# Transport Rings Network Commands
## `/sgjourney ringsNetwork getAllRings <dimension>`
Accessible in Survival: `False`

Returns the positions of all Transport Rings 
that have been registered to the Transport Rings Network in the selected Dimension.







