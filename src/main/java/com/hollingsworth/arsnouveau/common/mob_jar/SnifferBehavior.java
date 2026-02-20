package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class SnifferBehavior extends JarBehavior<Sniffer> {
    @Override
    public Vec3 scaleOffset(MobJarTile pBlockEntity) {
        float scale = -0.45F;
        return new Vec3(scale, scale, scale);
    }

    @Override
    public void tick(MobJarTile tile) {
        if (!(tile.getLevel() instanceof ServerLevel level) || isPowered(tile)) {
            return;
        }

        Sniffer sniffer = this.entityFromJar(tile);

        int dropTime = Math.max(8 * 60 * 20, sniffer.getEntityData().get(Sniffer.DATA_DROP_SEED_AT_TICK));

        if (!isEntityBaby(sniffer) && dropTime <= sniffer.tickCount) {
            sniffer.getEntityData().set(Sniffer.DATA_DROP_SEED_AT_TICK, sniffer.tickCount + 8 * 60 * 20);
            level.broadcastEntityEvent(sniffer, (byte) 63);

            LootTable loottable = level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.SNIFFER_DIGGING);
            LootParams lootparams = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, sniffer.position())
                    .withParameter(LootContextParams.THIS_ENTITY, sniffer)
                    .create(LootContextParamSets.GIFT);

            for (ItemStack stack : loottable.getRandomItems(lootparams)) {
                JarBehavior.insertOrCreateItem(tile, stack);
            }

            sniffer.playSound(SoundEvents.SNIFFER_DROP_SEED, 1.0F, 1.0F);
        }

        sniffer.tickCount++;
    }
}
