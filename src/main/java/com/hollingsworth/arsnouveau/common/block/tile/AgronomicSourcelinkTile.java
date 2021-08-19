package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.SourcelinkEventQueue;
import com.hollingsworth.arsnouveau.common.datagen.Recipes;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class AgronomicSourcelinkTile extends SourcelinkTile {

    public AgronomicSourcelinkTile() {
        super(BlockRegistry.AGRONOMIC_SOURCELINK_TILE);
    }

    @Override
    public int getMaxMana() {
        return 1000;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public int getCurrentMana() {
        return super.getCurrentMana();
    }

    @SubscribeEvent
    public static void cropGrow(BlockEvent.CropGrowEvent.Post event) {
        int mana = 15;
        if(event.getWorld().getBlockState(event.getPos()).getBlock().is(Recipes.MAGIC_PLANTS)) {
            mana += 20;
        }
        if(event.getWorld() instanceof World)
            SourcelinkEventQueue.addManaEvent((World) event.getWorld(), AgronomicSourcelinkTile.class, mana, event, event.getPos());
    }

    @SubscribeEvent
    public static void treeGrow(SaplingGrowTreeEvent event) {
        int mana = 30;
        if(event.getWorld().getBlockState(event.getPos()).getBlock().is(Recipes.MAGIC_SAPLINGS)) {
            mana += 25;
        }
        if(event.getWorld() instanceof World)
            SourcelinkEventQueue.addManaEvent((World) event.getWorld(), AgronomicSourcelinkTile.class, mana, event, event.getPos());
    }

    @Override
    public boolean usesEventQueue() {
        return true;
    }

    @Override
    public int getTransferRate() {
        return 1000;
    }
}
