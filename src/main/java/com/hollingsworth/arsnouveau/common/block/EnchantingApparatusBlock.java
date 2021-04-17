package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EnchantingApparatusBlock extends ModBlock{

    public EnchantingApparatusBlock() {
        super(ModBlock.defaultProperties().noOcclusion(),"enchanting_apparatus");
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        super.attack(state, worldIn, pos, player);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(world.isClientSide || handIn != Hand.MAIN_HAND)
            return ActionResultType.SUCCESS;
        EnchantingApparatusTile tile = (EnchantingApparatusTile) world.getBlockEntity(pos);
        if(tile.isCrafting)
            return ActionResultType.SUCCESS;


        if(!(world.getBlockState(pos.below()).getBlock() instanceof ArcaneCore)){
            PortUtil.sendMessage(player, new TranslationTextComponent("alert.core"));
            return ActionResultType.SUCCESS;
        }
        if(tile.catalystItem == null || tile.catalystItem.isEmpty()){
            IEnchantingRecipe recipe = tile.getRecipe(player.getMainHandItem());
            if(recipe == null){
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.apparatus.norecipe"));
            }else if(recipe.consumesMana() && !ManaUtil.hasManaNearby(tile.getBlockPos(), tile.getLevel(), 10, recipe.manaCost())){
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.apparatus.nomana"));
            }else{
                if(tile.attemptCraft(player.getMainHandItem())){
                    tile.catalystItem = player.inventory.removeItem(player.inventory.selected, 1);
                }
            }
        }else{
            ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.catalystItem);
            world.addFreshEntity(item);
            tile.catalystItem = ItemStack.EMPTY;
            if(tile.attemptCraft(player.getMainHandItem())){
                tile.catalystItem = player.inventory.removeItem(player.inventory.selected, 1);
            }
        }

        world.sendBlockUpdated(pos, state, state, 2);
        return ActionResultType.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return Block.box(1D, 1.0D, 1.0D, 15, 16, 15);
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if(worldIn.getBlockEntity(pos) instanceof EnchantingApparatusTile && ((EnchantingApparatusTile) worldIn.getBlockEntity(pos)).catalystItem != null){
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), ((EnchantingApparatusTile) worldIn.getBlockEntity(pos)).catalystItem));
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EnchantingApparatusTile();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
