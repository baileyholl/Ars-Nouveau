package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SuccessfulTreeGrowthEvent;
import com.hollingsworth.arsnouveau.api.source.SourcelinkEventQueue;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class AgronomicSourcelinkTile extends SourcelinkTile {

    public AgronomicSourcelinkTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.AGRONOMIC_SOURCELINK_TILE.get(), pos, state);
    }

    @SubscribeEvent
    public static void cropGrow(CropGrowEvent.Post event) {
        int mana = 20;

        if (event.getLevel().getBlockState(event.getPos()).is(BlockTagProvider.MAGIC_PLANTS)) {
            mana += 25;
        }
        if (event.getLevel() instanceof Level)
            SourcelinkEventQueue.addManaEvent((Level) event.getLevel(), AgronomicSourcelinkTile.class, mana, event, event.getPos());
    }

    @SubscribeEvent
    public static void treeGrow(SuccessfulTreeGrowthEvent event) {
        int mana = 50;
        if (event.sapling.is(BlockTagProvider.MAGIC_SAPLINGS)) {
            mana += 50;
        }
        SourcelinkEventQueue.addManaEvent(event.level, AgronomicSourcelinkTile.class, mana, event, event.pos);
    }

    @Override
    public boolean usesEventQueue() {
        return true;
    }
}
