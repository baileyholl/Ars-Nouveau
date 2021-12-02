package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.GlyphPressTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult p_225533_6_) {
        if(!world.isClientSide) {
            GlyphPressTile tile = (GlyphPressTile) world.getBlockEntity(pos);
            if(tile.isCrafting)
                return InteractionResult.PASS;

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
        return InteractionResult.SUCCESS;
    }


    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new GlyphPressTile();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) { builder.add(stage); }

}
