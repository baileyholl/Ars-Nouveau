package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.enchanting_apparatus.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ArmorTile;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ArmorBlock extends TickableModBlock{


    public ArmorBlock() {
        this(defaultProperties().noOcclusion());
    }

    public ArmorBlock(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        if (world.isClientSide || handIn != InteractionHand.MAIN_HAND || !(world.getBlockEntity(pos) instanceof ArmorTile tile))
            return InteractionResult.SUCCESS;
        if (tile.isCrafting)
            return InteractionResult.SUCCESS;


        if (!(world.getBlockState(pos.below()).getBlock() instanceof ArcaneCore)) {
            PortUtil.sendMessage(player, Component.translatable("alert.core"));
            return InteractionResult.SUCCESS;
        }
        if (tile.getArmorStack().isEmpty()) {
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
            ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getArmorStack());
            world.addFreshEntity(item);
            tile.setArmorStack(ItemStack.EMPTY);
            if (tile.attemptCraft(player.getMainHandItem(), player)) {
                tile.setCatalystItem(player.getInventory().removeItem(player.getInventory().selected, 1));
            }
        }

        world.sendBlockUpdated(pos, state, state, 2);
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ArmorTile(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
