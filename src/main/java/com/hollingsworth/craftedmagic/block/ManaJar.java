package com.hollingsworth.craftedmagic.block;

import com.hollingsworth.craftedmagic.api.mana.IMana;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ManaJar extends Block {

    public static final IProperty fill = IntegerProperty.create("fill", 0, 10);

    public ManaJar() {
        super(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(0.0f));
        setRegistryName("mana_jar");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ManaJarTile();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<net.minecraft.block.Block, BlockState> builder) { builder.add(ManaJar.fill); }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote)
            return true;
        System.out.println(((AbstractManaTile)worldIn.getTileEntity(pos)).getCurrentMana());

        return true;
    }
}
