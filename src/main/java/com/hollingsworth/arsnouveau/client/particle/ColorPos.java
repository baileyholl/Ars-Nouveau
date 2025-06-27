package com.hollingsworth.arsnouveau.client.particle;

import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class ColorPos {
    public final ParticleColor color;
    public final Vec3 pos;

    public ColorPos(Vec3 pos, ParticleColor color) {
        this.pos = pos;
        this.color = color;
    }

    public ColorPos(Vec3 pos) {
        this(pos, ParticleColor.defaultParticleColor());
    }

    public static ColorPos centered(BlockPos pos, ParticleColor color) {
        return new ColorPos(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), color);
    }

    public static ColorPos centered(BlockPos pos) {
        return new ColorPos(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    public static ColorPos centeredAbove(BlockPos pos, ParticleColor color) {
        return new ColorPos(new Vec3(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5), color);
    }

    public static ColorPos centeredAbove(BlockPos pos) {
        return new ColorPos(new Vec3(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5));
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", pos.x);
        tag.putDouble("y", pos.y);
        tag.putDouble("z", pos.z);
        tag.put("color", color.serialize());
        return tag;
    }

    public static ColorPos fromTag(CompoundTag tag) {
        return new ColorPos(new Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z")), ParticleColorRegistry.from(tag.getCompound("color")));
    }
}
