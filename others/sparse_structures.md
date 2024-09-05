# Sparse structures
Update your configuration file `sparsestructures.json5` according to the `customSpreadFactors` category in the example below:
```
// ### THE MOD REQUIRES A RESTART OF THE GAME TO APPLY CHANGES ###
{
    // this is the main spread factor (default is 2)
    //
    // tips : a spread factor can be a decimal number (such as 1.5)
    //        a spread factor of 1 means all structure's placement are not modified (useful if you want to use only custom spread factors)
    //        a spread factor above 1 means all structures are rarer
    //        a spread factor below 1 means all structure are more common
    "spreadFactor": 2,

    // this is a list of custom spread factors
  "customSpreadFactors": [

    // StargateJourney
    {
        "structure": "sgjourney:buried_stargate",
        "factor": 1
    },
    {
        "structure": "sgjourney:cartouche",
        "factor": 1
    },
    {
        "structure": "sgjourney:city",
        "factor": 1
    },
    {
        "structure": "sgjourney:jaffa_house",
        "factor": 1
    },
    {
        "structure": "sgjourney:stargate_outpost",
        "factor": 1
    },
    {
        "structure": "sgjourney:stargate_pedestal",
        "factor": 1
    },
    {
        "structure": "sgjourney:stargate_temple",
        "factor": 1
    },
    // end of StargateJourney

    // add the structures you want to modify in the format:
    // {
    //     "structure": "namespace:structure_name",
    //     "factor": number
    // },
    // where "structure" is a structure_set or the name of a structure
    // /!\ if you put the name of a structure, all structures in its set will be modified
    // (example : "minecraft:village_plains" will modify all structures in the "villages" set)
    // see https://minecraft.wiki/w/Tutorials/Custom_structures#Structure_Set for more info
    //
    // tip : the same spread factors rules apply here
  ]
}
```