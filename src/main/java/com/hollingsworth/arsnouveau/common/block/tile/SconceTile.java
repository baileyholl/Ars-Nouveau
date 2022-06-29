package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class SconceTile extends ModdedTile implements ILightable, ITickable {

    public ParticleColor color = ParticleColor.defaultParticleColor();
    public boolean lit;

    public SconceTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SCONCE_TILE, pos, state);
    }


    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.color = ParticleColor.deserialize(nbt.getCompound("color"));
        lit = nbt.getBoolean("lit");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("color", color.serialize());
        compound.putBoolean("lit", lit);
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext) {
        this.color = spellContext.getColors().clone();
        lit = true;
        if(rayTraceResult instanceof BlockHitResult) {
            BlockState state = world.getBlockState(((BlockHitResult) rayTraceResult).getBlockPos());
            world.setBlock(getBlockPos(), state.setValue(SconceBlock.LIGHT_LEVEL, Math.min(Math.max(0, 15 - stats.getBuffCount(AugmentDampen.INSTANCE)), 15)), 3);
            world.sendBlockUpdated(((BlockHitResult) rayTraceResult).getBlockPos(), state,
                    state.setValue(SconceBlock.LIGHT_LEVEL, 15), 3);
        }
    }

    @Override
    public void tick() {
        if (!level.isClientSide() || !lit)
            return;
        BlockPos pos = getBlockPos();
        RandomSource rand = level.random;
        double xzOffset = 0.15;
        BlockState state = getLevel().getBlockState(getBlockPos());
        if(!(state.getBlock() instanceof SconceBlock))
            return;

        double centerX = 0.0;
        double centerZ = 0.0;
        if(state.getValue(ScribesBlock.FACING) == Direction.NORTH){
            centerX = pos.getX() + 0.5 + ParticleUtil.inRange(-xzOffset/4, xzOffset/4);
            centerZ = pos.getZ() + 0.8+ ParticleUtil.inRange(-xzOffset/4, xzOffset/4);
        }
        if(state.getValue(ScribesBlock.FACING) == Direction.SOUTH){
            centerX = pos.getX() + 0.5 + ParticleUtil.inRange(-xzOffset/4, xzOffset/4);
            centerZ = pos.getZ() + 0.2+ ParticleUtil.inRange(-xzOffset/4, xzOffset/4);
        }
        if(state.getValue(ScribesBlock.FACING) == Direction.EAST){
            centerX = pos.getX() + 0.2 + ParticleUtil.inRange(-xzOffset/4, xzOffset/4);
            centerZ = pos.getZ() + 0.5 + ParticleUtil.inRange(-xzOffset/4, xzOffset/4);
        }
        if(state.getValue(ScribesBlock.FACING) == Direction.WEST){
            centerX = pos.getX() + 0.8 + ParticleUtil.inRange(-xzOffset/4, xzOffset/4);
            centerZ = pos.getZ() + 0.5 + ParticleUtil.inRange(-xzOffset/4, xzOffset/4);
        }

        ParticleColor nextColor = this.color.nextColor(this.level.random);
        int intensity = 10;

        for (int i = 0; i < intensity; i++) {
            level.addParticle(
                    GlowParticleData.createData(nextColor),
                    centerX, pos.getY() + 0.8 + ParticleUtil.inRange(-0.00, 0.1), centerZ,
                    0, ParticleUtil.inRange(0.0, 0.03f), 0);

        }

    }
}