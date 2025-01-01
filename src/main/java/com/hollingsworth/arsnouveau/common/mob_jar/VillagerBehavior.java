package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.world.entity.npc.Villager;

public class VillagerBehavior extends JarBehavior<Villager> {
    @Override
    public void tick(MobJarTile tile) {
        if(tile.getLevel().isClientSide)
            return;
        Villager villager = entityFromJar(tile);
        if (!villager.isTrading()) {
            if (villager.updateMerchantTimer > 0) {
                --villager.updateMerchantTimer;
                if (villager.updateMerchantTimer <= 0) {
                    if (villager.increaseProfessionLevelOnUpdate) {
                        villager.increaseMerchantCareer();
                        villager.increaseProfessionLevelOnUpdate = false;
                    }
                }
            }

            if (villager.shouldRestock()) {
                villager.restock();
            }
        }
    }
}
