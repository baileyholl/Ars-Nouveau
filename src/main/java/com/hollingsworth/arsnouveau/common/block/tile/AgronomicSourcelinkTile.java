package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.source.SourcelinkEventQueue;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.SaplingGrowTreeEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class AgronomicSourcelinkTile extends SourcelinkTile {

    public AgronomicSourcelinkTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.AGRONOMIC_SOURCELINK_TILE, pos, state);
    }

    @Override
    public int getMaxSource() {
        return 1000;
    }

    @SubscribeEvent
    public static void cropGrow(BlockEvent.CropGrowEvent.Post event) {
        int mana = 20;

        if (event.getLevel().getBlockState(event.getPos()).is(BlockTagProvider.MAGIC_PLANTS)) {
            mana += 25;
        }
        if (event.getLevel() instanceof Level)
            SourcelinkEventQueue.addManaEvent((Level) event.getLevel(), AgronomicSourcelinkTile.class, mana, event, event.getPos());
    }

    @SubscribeEvent
    public static void treeGrow(SaplingGrowTreeEvent event) {
        int mana = 50;
        if (event.getLevel().getBlockState(event.getPos()).is(BlockTagProvider.MAGIC_SAPLINGS)) {
            mana += 50;
        }
        if (event.getLevel() instanceof Level)
            SourcelinkEventQueue.addManaEvent((Level) event.getLevel(), AgronomicSourcelinkTile.class, mana, event, event.getPos());
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
