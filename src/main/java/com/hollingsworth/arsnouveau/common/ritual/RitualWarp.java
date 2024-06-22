package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RitualWarp extends AbstractRitual {
    @Override
    protected void tick() {
        Level world = getWorld();
        if (world.isClientSide) {
            BlockPos pos = getPos();

            for (int i = 0; i < 10; i++) {
                Vec3 particlePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5, 5, 5));
                world.addParticle(ParticleLineData.createData(getCenterColor()),
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            }
        }
        if (!world.isClientSide && world.getGameTime() % 20 == 0) {
            incrementProgress();
            if (getProgress() >= 3) {
                List<Entity> entities = getWorld().getEntitiesOfClass(Entity.class, new AABB(getPos()).inflate(5));

                ItemStack i = getConsumedItems().get(0);
                WarpScroll.WarpScrollData data = WarpScroll.WarpScrollData.get(i);
                BlockPos b = data.getPos();
                for (Entity a : entities) {
                    if (b != null)
                        a.teleportTo(b.getX(), b.getY(), b.getZ());
                }
                if (b != null)
                    world.playSound(null, b, SoundEvents.PORTAL_TRAVEL, SoundSource.NEUTRAL, 1.0f, 1.0f);
                setFinished();
            }
        }
    }

    @Override
    public String getLangName() {
        return "Warping";
    }

    @Override
    public String getLangDescription() {
        return "Warps all nearby entities to the location on a warp scroll. Before starting the ritual, you must first augment the ritual with an inscribed Warp Scroll.";
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        if (!(stack.getItem() instanceof WarpScroll) || !getConsumedItems().isEmpty())
            return false;
        WarpScroll.WarpScrollData data = WarpScroll.WarpScrollData.get(stack);
        return data.isValid() && data.canTeleportWithDim(tile.getLevel());
    }

    @Override
    public boolean canStart(@Nullable Player player) {
        return !getConsumedItems().isEmpty();
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.WARP);
    }
}
