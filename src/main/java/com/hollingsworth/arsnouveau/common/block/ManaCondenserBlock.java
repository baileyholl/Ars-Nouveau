package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ManaCondenserTile;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class ManaCondenserBlock extends ModBlock {

    public ManaCondenserBlock() {
        super(ModBlock.defaultProperties().notSolid(),"mana_condenser");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if(worldIn.isRemote)
            return;
        if(BlockUtil.containsStateInRadius(worldIn, pos, 5, ManaCondenserBlock.class)){
            ((ManaCondenserTile)worldIn.getTileEntity(pos)).isDisabled = true;
            if(placer != null)
                placer.sendMessage(new StringTextComponent("Another condenser is nearby..."), Util.DUMMY_UUID);
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        if(worldIn.isRemote)
            return;
        ManaCondenserTile tile = (ManaCondenserTile) worldIn.getTileEntity(pos);
        if(tile != null)
            MinecraftForge.EVENT_BUS.unregister(tile);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ManaCondenserTile();
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

}
