package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.DimBoundary;
import com.hollingsworth.arsnouveau.common.entity.ArcanoBoss;
import com.hollingsworth.arsnouveau.common.world.dimension.PlanariumChunkGenerator;
import com.hollingsworth.arsnouveau.common.world.saved_data.ArcanoDimData;
import com.hollingsworth.nuggets.common.util.WorldHelpers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class ArcanoBossEvents {
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level && WorldHelpers.isOfWorldType(level, ArsNouveau.ARCANO_DIMENSION_TYPE_KEY)) {
            boolean insideJar = PlanariumChunkGenerator.innerBox.contains(event.getPos().getBottomCenter());
            if (event.getState().getBlock() instanceof DimBoundary && !insideJar) {
                if (event.getPlayer().canInteractWithBlock(event.getPos(), 4.0f)) {
                    DimBoundary.playerAttemptedBreak(level, event.getPlayer());
                }
                event.setCanceled(true);
            }
        }
    }

    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {

    }

    public static void onLevelTick(LevelTickEvent.Post event) {

    }

    public static void onEntityTravel(EntityTravelToDimensionEvent event) {
        System.out.println("entity entered");
        Level level = event.getEntity().level;
        if (!(level instanceof ServerLevel serverLevel) || !(event.getEntity() instanceof Player player)) {
            return;
        }
        ServerLevel targetLevel = serverLevel.getServer().getLevel(event.getDimension());
        if (targetLevel == null) {
            return;
        }
        if (!WorldHelpers.isOfWorldType(targetLevel, ArsNouveau.ARCANO_DIMENSION_TYPE_KEY)) {
            return;
        }

        ArcanoDimData dimData = ArcanoDimData.from(serverLevel);
        System.out.println("Player " + event.getEntity().getName().getString() + " is traveling to dimension " + event.getDimension());
//        if (!dimData.isBossSpawned()) {
        System.out.println("adding boss");
        ArcanoBoss arcanoBoss = new ArcanoBoss(targetLevel);
        arcanoBoss.setPos(15.5, 2, 15.5);
        arcanoBoss.setTarget(player);
        targetLevel.addFreshEntity(arcanoBoss);
        dimData.setBossSpawned(true);
//        }
    }
}
