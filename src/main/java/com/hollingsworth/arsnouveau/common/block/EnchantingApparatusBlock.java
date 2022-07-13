package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class EnchantingApparatusBlock extends TickableModBlock {

    public EnchantingApparatusBlock() {
        this(TickableModBlock.defaultProperties().noOcclusion());
    }

    public EnchantingApparatusBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantingApparatusTile(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        if (world.isClientSide || handIn != InteractionHand.MAIN_HAND || !(world.getBlockEntity(pos) instanceof EnchantingApparatusTile tile))
            return InteractionResult.SUCCESS;
        if (tile.isCrafting)
            return InteractionResult.SUCCESS;


        if (!(world.getBlockState(pos.below()).getBlock() instanceof ArcaneCore)) {
            PortUtil.sendMessage(player, Component.translatable("alert.core"));
            return InteractionResult.SUCCESS;
        }
        if (tile.getCatalystItem() == null || tile.getCatalystItem().isEmpty()) {
            IEnchantingRecipe recipe = tile.getRecipe(player.getMainHandItem(), player);
            if (recipe == null) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.apparatus.norecipe"));
            } else if (recipe.consumesSource() && !SourceUtil.hasSourceNearby(tile.getBlockPos(), tile.getLevel(), 10, recipe.getSourceCost())) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.apparatus.nomana"));
            } else {
                if (tile.attemptCraft(player.getMainHandItem(), player)) {
                    tile.setCatalystItem(player.getInventory().removeItem(player.getInventory().selected, 1));
                }
            }
        } else {
            ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getCatalystItem());
            world.addFreshEntity(item);
            tile.setCatalystItem(ItemStack.EMPTY);
            if (tile.attemptCraft(player.getMainHandItem(), player)) {
                tile.setCatalystItem(player.getInventory().removeItem(player.getInventory().selected, 1));
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
        if (worldIn.getBlockEntity(pos) instanceof EnchantingApparatusTile tile && tile.getCatalystItem() != null) {
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getCatalystItem()));
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
