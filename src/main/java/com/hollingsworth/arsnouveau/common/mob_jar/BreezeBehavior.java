package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.phys.Vec3;

public class BreezeBehavior extends JarBehavior<Breeze> {
    @Override
    public void onRedstonePower(MobJarTile tile) {
        if (!(tile.getLevel() instanceof ServerLevel level) || isPowered(tile) || tile.getExtraDataTag().getLong("lastActive") == level.getGameTime()) {
            return;
        }

        Breeze breeze = this.entityFromJar(tile);
        BreezeWindCharge windCharge = new BreezeWindCharge(breeze, level);
        var pos = tile.getBlockPos().getCenter().add(new Vec3(tile.getBlockState().getValue(MobJar.FACING).step()).scale(0.6));
        var dir = tile.getBlockState().getValue(MobJar.FACING).step();
        windCharge.setPos(pos);
        breeze.playSound(SoundEvents.BREEZE_SHOOT, 1.5F, 1.0F);
        windCharge.shoot(dir.x, dir.y, dir.z, 0.7F, (float) (5 - level.getDifficulty().getId() * 4));
        level.addFreshEntity(windCharge);

        var tag = new CompoundTag();
        tag.putLong("lastActive", level.getGameTime());
        tile.setExtraDataTag(tag);
    }
}
