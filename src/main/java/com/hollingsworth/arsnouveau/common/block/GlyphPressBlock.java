package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.GlyphPressTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GlyphPressBlock extends ModBlock{
    public static final Property<Integer> stage = IntegerProperty.create("stage", 1, 31);

    public GlyphPressBlock() {
        super(ModBlock.defaultProperties().noOcclusion(),"glyph_press");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        if(!world.isClientSide) {
            GlyphPressTile tile = (GlyphPressTile) world.getBlockEntity(pos);
            if(tile.isCrafting)
                return ActionResultType.PASS;

            if (tile.baseMaterial != null && !tile.baseMaterial.isEmpty() && player.getItemInHand(handIn).isEmpty()) {
                ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.baseMaterial);
                world.addFreshEntity(item);
                tile.baseMaterial = ItemStack.EMPTY;
            }
            else if (!player.inventory.getSelected().isEmpty()) {
                if(player.getItemInHand(handIn).getItem() == Items.CLAY_BALL || player.getItemInHand(handIn).getItem() == ItemsRegistry.magicClay ||
                        player.getItemInHand(handIn).getItem() == ItemsRegistry.marvelousClay || player.getItemInHand(handIn).getItem() == ItemsRegistry.mythicalClay) {
                    if(tile.baseMaterial != null && !tile.baseMaterial.isEmpty()){
                        ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.baseMaterial);
                        world.addFreshEntity(item);
                    }
                    tile.baseMaterial = player.inventory.removeItem(player.inventory.selected, 1);
                }else if(tile.baseMaterial != null && !tile.baseMaterial.isEmpty()){
                    if(tile.reagentItem != null && !tile.reagentItem.isEmpty()){
                        ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.reagentItem);
                        world.addFreshEntity(item);
                    }

                    tile.reagentItem = player.inventory.removeItem(player.inventory.selected, 1);
                    if(!tile.craft(player) && player.inventory.add(tile.reagentItem)) {
                        tile.reagentItem = ItemStack.EMPTY;
                    }
                }
            }

            world.sendBlockUpdated(pos, state, state, 2);
        }
        return ActionResultType.SUCCESS;
    }


    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if(!(worldIn.getBlockEntity(pos) instanceof GlyphPressTile) || worldIn.isClientSide)
            return;
        GlyphPressTile tile = ((GlyphPressTile) worldIn.getBlockEntity(pos));
        if(tile.baseMaterial != null && !tile.baseMaterial.isEmpty()){
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.baseMaterial));
            if(tile.reagentItem != null && !tile.reagentItem.isEmpty()){
                worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.reagentItem));
            }

        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GlyphPressTile();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<net.minecraft.block.Block, BlockState> builder) { builder.add(stage); }

}
