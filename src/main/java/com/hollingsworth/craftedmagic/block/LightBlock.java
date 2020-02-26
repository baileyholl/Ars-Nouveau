package com.hollingsworth.craftedmagic.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Random;

public class LightBlock extends Block {

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(6.0D, 6.0D, 6.0D, 12.0D, 12.0D, 12.0D);

    public LightBlock() {
        super(Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(0.0f).lightValue(14).doesNotBlockMovement());
        setRegistryName("light_block");
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return true;
    }

    @Override
    public int tickRate(IWorldReader worldIn) {
        return 1;
    }


    @Override
    public void animateTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
        if(world.getGameTime() % 5 == 0){
            for(int i =0; i < 5; i++) {
                double d0 = pos.getX() + .6; //+ world.rand.nextFloat();
                double d1 = pos.getY() + .6; //+ world.rand.nextFloat();
                double d2 = pos.getZ() + .6; //+ world.rand.nextFloat();
                int mod = world.rand.nextFloat() < 0.5 ? -1 : 1;
                world.addParticle(ParticleTypes.END_ROD, d0, d1, d2, mod*0.05*world.rand.nextFloat(), 0.05*world.rand.nextFloat(), mod*0.05*world.rand.nextFloat());

            }
        }

    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
