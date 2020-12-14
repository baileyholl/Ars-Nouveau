package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WixieCauldron extends ModBlock{

    public WixieCauldron() {
        super(defaultProperties().notSolid(), LibBlockNames.WIXIE_CAULDRON);
        setDefaultState(getDefaultState().with(CONVERTED, false).with(FILLED, false));
    }

    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final BooleanProperty CONVERTED = BooleanProperty.create("converted");


    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote || handIn != Hand.MAIN_HAND)
            return ActionResultType.SUCCESS;

        if(player.getHeldItemMainhand().getItem() == ItemsRegistry.bucketOfMana && !state.get(FILLED)){
            player.setHeldItem(Hand.MAIN_HAND, Items.BUCKET.getDefaultInstance());
            worldIn.setBlockState(pos, state.with(FILLED, true));
        }

        if(worldIn.getTileEntity(pos) instanceof WixieCauldronTile){
            ((WixieCauldronTile) worldIn.getTileEntity(pos)).setRecipes(player.getHeldItemMainhand());
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FILLED, CONVERTED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        CompoundNBT tag = context.getItem().getTag();
        if(tag != null && tag.contains("BlockEntityTag")){
            tag = tag.getCompound("BlockEntityTag");
            if(tag.contains("converted") && tag.getBoolean("converted")){
                state = state.with(CONVERTED, true);
            }
        }
        return state;

    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new WixieCauldronTile();
    }
}
