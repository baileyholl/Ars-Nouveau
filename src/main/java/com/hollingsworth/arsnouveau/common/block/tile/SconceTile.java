package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.particle.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class SconceTile extends ModdedTile implements ILightable, ITickable, IDispellable, IWololoable {

    protected ParticleColor color = ParticleColor.defaultParticleColor();
    public boolean lit;

    public SconceTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SCONCE_TILE, pos, state);
    }

    public SconceTile(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, bpRegistries);
        this.color = ParticleColorRegistry.from(pTag.getCompound("color"));
        lit = pTag.getBoolean("lit");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        compound.put("color", color.serialize());
        compound.putBoolean("lit", lit);
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext) {
        this.color = spellContext.getColors().clone();
        lit = true;
        if (rayTraceResult instanceof BlockHitResult) {
            BlockState state = world.getBlockState(((BlockHitResult) rayTraceResult).getBlockPos());
            world.setBlock(getBlockPos(), state.setValue(SconceBlock.LIGHT_LEVEL, Math.min(Math.max(0, 15 - stats.getBuffCount(AugmentDampen.INSTANCE)), 15)), 3);
            world.sendBlockUpdated(((BlockHitResult) rayTraceResult).getBlockPos(), state,
                    state.setValue(SconceBlock.LIGHT_LEVEL, Math.min(Math.max(0, 15 - stats.getBuffCount(AugmentDampen.INSTANCE)), 15)), 3);
        }
        updateBlock();
    }

    @Override
    public void tick() {
        if (!level.isClientSide() || !lit)
            return;
        BlockPos pos = getBlockPos();
        double xzOffset = 0.15;
        BlockState state = getLevel().getBlockState(getBlockPos());
        if (!(state.hasProperty(ScribesBlock.FACING)))
            return;

        double xOffset = ParticleUtil.inRange(-xzOffset / 4, xzOffset / 4);
        double zOffset = ParticleUtil.inRange(-xzOffset / 4, xzOffset / 4);
        double centerX = pos.getX() + xOffset;
        double centerZ = pos.getZ() + zOffset;
        if (state.getValue(ScribesBlock.FACING) == Direction.NORTH) {
            centerX += 0.5;
            centerZ += 0.8;
        }
        if (state.getValue(ScribesBlock.FACING) == Direction.SOUTH) {
            centerX += 0.5;
            centerZ += 0.2;
        }
        if (state.getValue(ScribesBlock.FACING) == Direction.EAST) {
            centerX += 0.2;
            centerZ += 0.5;
        }
        if (state.getValue(ScribesBlock.FACING) == Direction.WEST) {
            centerX += 0.8;
            centerZ += 0.5;
        }
        ParticleColor nextColor = this.color.transition((int) level.getGameTime() * 10);
        for (int i = 0; i < 10; i++) {
            level.addParticle(
                    GlowParticleData.createData(nextColor),
                    centerX, pos.getY() + 0.9 + ParticleUtil.inRange(-0.00, 0.1), centerZ,
                    0, ParticleUtil.inRange(0.0, 0.03f), 0);

        }

    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        this.lit = false;
        level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()).setValue(SconceBlock.LIGHT_LEVEL, 0), 3);
        updateBlock();
        return true;
    }

    @Override
    public void setColor(ParticleColor color) {
        this.color = color;
        updateBlock();
    }

    @Override
    public ParticleColor getColor() {
        return color;
    }
}