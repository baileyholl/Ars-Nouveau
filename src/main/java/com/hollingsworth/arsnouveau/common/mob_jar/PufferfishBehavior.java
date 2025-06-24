package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.hollingsworth.arsnouveau.common.mixin.PufferfishAccessor;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class PufferfishBehavior extends JarBehavior<Pufferfish> {

    @Override
    public void tick(MobJarTile tile) {
        super.tick(tile);
        Level level = tile.getLevel();
        Pufferfish pufferfish = (Pufferfish) tile.getEntity();
        if (level.isClientSide || pufferfish == null) {
            return;
        }
        PufferfishAccessor pufferfishAccessor = (PufferfishAccessor) pufferfish;
        boolean playerNearby = mobsNearby(tile, pufferfish);
        BlockPos worldPosition = tile.getBlockPos();
        if (playerNearby) {
            if (pufferfish.getPuffState() == 0) {
                pufferfish.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, 1.0f, pufferfish.getVoicePitch());
                pufferfish.setPuffState(1);
                level.updateNeighborsAt(worldPosition, BlockRegistry.ITEM_DETECTOR.get());
                syncClient(tile);
            } else if (pufferfishAccessor.getInflateCounter() > 40 && pufferfish.getPuffState() == 1) {
                pufferfish.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, 1.0f, pufferfish.getVoicePitch());
                pufferfish.setPuffState(2);
                level.updateNeighborsAt(worldPosition, BlockRegistry.ITEM_DETECTOR.get());
                syncClient(tile);
            }
            pufferfishAccessor.setInflateCounter(pufferfishAccessor.getInflateCounter() + 1);
        } else if (pufferfish.getPuffState() != 0) {
            if (pufferfishAccessor.getDeflateTimer() > 60 && pufferfish.getPuffState() == 2) {
                pufferfish.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, 1.0f, pufferfish.getVoicePitch());
                pufferfish.setPuffState(1);
                level.updateNeighborsAt(worldPosition, BlockRegistry.ITEM_DETECTOR.get());
                syncClient(tile);
            } else if (pufferfishAccessor.getDeflateTimer() > 100 && pufferfish.getPuffState() == 1) {
                pufferfish.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, 1.0f, pufferfish.getVoicePitch());
                pufferfish.setPuffState(0);
                level.updateNeighborsAt(worldPosition, BlockRegistry.ITEM_DETECTOR.get());
                syncClient(tile);
            }
            pufferfishAccessor.setDeflateTimer(pufferfishAccessor.getDeflateTimer() + 1);
        }
        if (!playerNearby) {
            pufferfishAccessor.setInflateCounter(0);
            if (pufferfish.getPuffState() == 0) {
                pufferfishAccessor.setDeflateTimer(0);
            }
        }
    }

    public boolean mobsNearby(MobJarTile tile, Pufferfish pufferfish) {
        List<LivingEntity> list = pufferfish.level.getEntitiesOfClass(LivingEntity.class, new AABB(tile.getBlockPos()).inflate(2.4D), (p_149015_) -> {
            return PufferfishAccessor.targetConditions().test(pufferfish, p_149015_);
        });
        return !list.isEmpty();
    }

    @Override
    public int getSignalPower(MobJarTile tile) {
        Pufferfish pufferfish = (Pufferfish) tile.getEntity();
        if (pufferfish.getPuffState() == 1) {
            return 8;
        } else if (pufferfish.getPuffState() == 2) {
            return 15;
        }
        return 0;
    }
}
