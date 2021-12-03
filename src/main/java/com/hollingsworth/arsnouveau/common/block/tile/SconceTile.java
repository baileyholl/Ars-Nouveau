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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Random;

public class SconceTile extends ModdedTile implements ILightable, ITickable {

    public int red = 255;
    public int green = 125;
    public int blue = 255;
    public boolean lit;

    public SconceTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SCONCE_TILE, pos, state);
    }


    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.red = nbt.getInt("red");
        this.red = red > 0 ? red : 255;
        this.green = nbt.getInt("green");
        green = this.green > 0 ? green : 125;
        this.blue = nbt.getInt("blue");
        blue = this.blue > 0 ? blue : 255;
        lit = nbt.getBoolean("lit");
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.putInt("red", red);
        compound.putInt("green", green);
        compound.putInt("blue", blue);
        compound.putBoolean("lit", lit);
        return super.save(compound);
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext) {
        this.red = spellContext.colors.r;
        this.green = spellContext.colors.g;
        this.blue = spellContext.colors.b;
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
        if(!level.isClientSide || !lit)
            return;
        Level world = getLevel();
        BlockPos pos = getBlockPos();
        Random rand = world.random;
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
        ParticleColor color = new ParticleColor(rand.nextInt(red), rand.nextInt(green), rand.nextInt(blue));
        int intensity = 10;
        for(int i =0; i < intensity; i++) {
            world.addParticle(
                    GlowParticleData.createData(color),
                    centerX, pos.getY() + 0.8 + ParticleUtil.inRange(-0.00, 0.1), centerZ,
                    0, ParticleUtil.inRange(0.0, 0.03f), 0);

        }
    }
}