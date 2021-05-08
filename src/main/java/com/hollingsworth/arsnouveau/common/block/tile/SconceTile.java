package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SconceTile extends TileEntity implements ILightable, ITickableTileEntity {

    public int red = 255;
    public int green = 125;
    public int blue = 255;
    public boolean lit;

    public SconceTile() {
        super(BlockRegistry.SCONCE_TILE);
    }


    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(level.getBlockState(worldPosition), pkt.getTag());
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.red = nbt.getInt("red");
        this.red = red > 0 ? red : 255;
        this.green = nbt.getInt("green");
        green = this.green > 0 ? green : 125;
        this.blue = nbt.getInt("blue");
        blue = this.blue > 0 ? blue : 255;
        lit = nbt.getBoolean("lit");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("red", red);
        compound.putInt("green", green);
        compound.putInt("blue", blue);
        compound.putBoolean("lit", lit);
        return super.save(compound);
    }

    @Override
    public void onLight(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        this.red = spellContext.colors.r;
        this.green = spellContext.colors.g;
        this.blue = spellContext.colors.b;
        lit = true;
        System.out.println("lit");
        if(rayTraceResult instanceof BlockRayTraceResult) {
            BlockState state = world.getBlockState(((BlockRayTraceResult) rayTraceResult).getBlockPos());
            world.setBlock(getBlockPos(), state.setValue(SconceBlock.LIGHT_LEVEL, Math.min(Math.max(0, 15 - AbstractEffect.getBuffCount(augments, AugmentDampen.class)), 15)), 3);
            world.sendBlockUpdated(((BlockRayTraceResult) rayTraceResult).getBlockPos(), state,
                    state.setValue(SconceBlock.LIGHT_LEVEL, 15), 3);
        }
    }

    @Override
    public void tick() {
        if(!level.isClientSide || !lit)
            return;
        World world = getLevel();
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