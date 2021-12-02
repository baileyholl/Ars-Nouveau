package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.CrystallizerTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class CrystallizerBlock extends ModBlock{
    public CrystallizerBlock() {
        super(defaultProperties().noOcclusion(), LibBlockNames.CRYSTALLIZER);
    }

    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new CrystallizerTile();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }


    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(!(worldIn.getBlockEntity(pos) instanceof CrystallizerTile))
            return super.use(state, worldIn, pos, player, handIn, hit);
        ItemStack stack = ((CrystallizerTile) worldIn.getBlockEntity(pos)).stack;
        worldIn.addFreshEntity(new ItemEntity(worldIn, player.getX(), player.getY(), player.getZ(), stack.copy()));
        ((CrystallizerTile) worldIn.getBlockEntity(pos)).stack = ItemStack.EMPTY;
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if(!(worldIn.getBlockEntity(pos) instanceof CrystallizerTile))
            return;
        ItemStack stack = ((CrystallizerTile) worldIn.getBlockEntity(pos)).stack;
        worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack.copy()));
        ((CrystallizerTile) worldIn.getBlockEntity(pos)).stack = ItemStack.EMPTY;
    }
}
