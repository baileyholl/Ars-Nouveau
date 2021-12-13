package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.source.SourcelinkEventQueue;
import com.hollingsworth.arsnouveau.common.datagen.Recipes;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class AgronomicSourcelinkTile extends SourcelinkTile {

    public AgronomicSourcelinkTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.AGRONOMIC_SOURCELINK_TILE, pos, state);
    }

    @Override
    public int getMaxSource() {
        return 1000;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public int getSource() {
        return super.getSource();
    }

    @SubscribeEvent
    public static void cropGrow(BlockEvent.CropGrowEvent.Post event) {
        int mana = 20;

        if(event.getWorld().getBlockState(event.getPos()).is(Recipes.MAGIC_PLANTS)) {
            mana += 25;
        }
        if(event.getWorld() instanceof Level)
            SourcelinkEventQueue.addManaEvent((Level) event.getWorld(), AgronomicSourcelinkTile.class, mana, event, event.getPos());
    }

    @SubscribeEvent
    public static void treeGrow(SaplingGrowTreeEvent event) {
        int mana = 50;
        if(event.getWorld().getBlockState(event.getPos()).is(Recipes.MAGIC_SAPLINGS)) {
            mana += 50;
        }
        if(event.getWorld() instanceof Level)
            SourcelinkEventQueue.addManaEvent((Level) event.getWorld(), AgronomicSourcelinkTile.class, mana, event, event.getPos());
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
