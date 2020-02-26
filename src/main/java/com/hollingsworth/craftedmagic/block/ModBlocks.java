package com.hollingsworth.craftedmagic.block;

import com.hollingsworth.craftedmagic.ArsNouveau;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {

    @ObjectHolder(ArsNouveau.MODID + ":phantom_block")
    public static PhantomBlock PHANTOM_BLOCK;

    @ObjectHolder(ArsNouveau.MODID + ":light_block")
    public static LightBlock lightBlock;

    @ObjectHolder(ArsNouveau.MODID + ":phantom_block")
    public static TileEntityType<PhantomBlockTile> PHANTOM_TILE;


}
