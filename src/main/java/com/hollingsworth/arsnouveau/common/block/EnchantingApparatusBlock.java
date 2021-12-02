package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

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
    public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) {
        super.attack(state, worldIn, pos, player);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(world.isClientSide || handIn != InteractionHand.MAIN_HAND)
            return InteractionResult.SUCCESS;
        EnchantingApparatusTile tile = (EnchantingApparatusTile) world.getBlockEntity(pos);
        if(tile.isCrafting)
            return InteractionResult.SUCCESS;


        if(!(world.getBlockState(pos.below()).getBlock() instanceof ArcaneCore)){
            PortUtil.sendMessage(player, new TranslatableComponent("alert.core"));
            return InteractionResult.SUCCESS;
        }
        if(tile.catalystItem == null || tile.catalystItem.isEmpty()){
            IEnchantingRecipe recipe = tile.getRecipe(player.getMainHandItem(), player);
            if(recipe == null){
                PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.apparatus.norecipe"));
            }else if(recipe.consumesMana() && !ManaUtil.hasManaNearby(tile.getBlockPos(), tile.getLevel(), 10, recipe.manaCost())){
                PortUtil.sendMessage(player, new TranslatableComponent("ars_nouveau.apparatus.nomana"));
            }else{
                if(tile.attemptCraft(player.getMainHandItem(), player)){
                    tile.catalystItem = player.inventory.removeItem(player.inventory.selected, 1);
                }
            }
        }else{
            ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.catalystItem);
            world.addFreshEntity(item);
            tile.catalystItem = ItemStack.EMPTY;
            if(tile.attemptCraft(player.getMainHandItem(), player)){
                tile.catalystItem = player.inventory.removeItem(player.inventory.selected, 1);
            }
        }

        world.sendBlockUpdated(pos, state, state, 2);
        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Block.box(1D, 1.0D, 1.0D, 15, 16, 15);
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if(worldIn.getBlockEntity(pos) instanceof EnchantingApparatusTile && ((EnchantingApparatusTile) worldIn.getBlockEntity(pos)).catalystItem != null){
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), ((EnchantingApparatusTile) worldIn.getBlockEntity(pos)).catalystItem));
        }
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new EnchantingApparatusTile();
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
