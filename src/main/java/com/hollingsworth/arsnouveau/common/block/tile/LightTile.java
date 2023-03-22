package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LightTile extends ModdedTile implements ITickable {

    public ParticleColor color = ParticleColor.defaultParticleColor();
    public static RandomSource random = RandomSource.createNewThreadLocalInstance();

    public LightTile(BlockPos pos, BlockState state) {
        this(BlockRegistry.LIGHT_TILE, pos, state);
    }

    public LightTile(BlockEntityType<?> lightTile, BlockPos pos, BlockState state) {
        super(lightTile, pos, state);
    }

    @Override
    public void tick(Level level, BlockState state, BlockPos pos) {
        if (level.isClientSide){
            level.addParticle(
                    GlowParticleData.createData(this.color.nextColor(random), 0.25f, 0.9f, 36),
                    pos.getX() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), pos.getY() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), pos.getZ() + 0.5 + ParticleUtil.inRange(-0.1, 0.1),
                    0, 0, 0);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.color = ParticleColor.deserialize(nbt.getCompound("color"));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("color", color.serialize());
    }
}
