package com.hollingsworth.craftedmagic.items;

import com.hollingsworth.craftedmagic.ArsNouveau;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.item.Item;

public class Test extends Item {
    public Test(){
        super(new Item.Properties().maxStackSize(1).group(ArsNouveau.itemGroup));
        setRegistryName("test");        // The unique name (within your mod) that identifies this item

        //setUnlocalizedName(ExampleMod.MODID + ".spell_book");     // Used for localization (en_US.lang)
    }



}
