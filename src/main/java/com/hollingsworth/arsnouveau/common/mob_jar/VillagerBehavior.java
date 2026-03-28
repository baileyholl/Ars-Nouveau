package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;

public class VillagerBehavior extends JarBehavior<Villager> {
    @Override
    public void tick(MobJarTile tile) {
        if (tile.getLevel().isClientSide() || isPowered(tile))
            return;
        Villager villager = entityFromJar(tile);
        if (!villager.isTrading() && tile.getLevel() instanceof ServerLevel serverLevel) {
            if (villager.shouldRestock(serverLevel)) {
                villager.restock();
            }
        }
    }
}
