package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import static com.hollingsworth.arsnouveau.common.block.tile.SummoningTile.CONVERTED;

public class WixieCauldron extends SummonBlock {

    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    public WixieCauldron() {
        super(defaultProperties().noOcclusion());
        registerDefaultState(defaultBlockState().setValue(CONVERTED, false).setValue(FILLED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (worldIn.isClientSide || handIn != InteractionHand.MAIN_HAND || !(worldIn.getBlockEntity(pos) instanceof WixieCauldronTile) || player.getMainHandItem().getItem() == ItemsRegistry.DOMINION_ROD.get())
            return InteractionResult.PASS;

        if (player.getMainHandItem().getItem() != ItemsRegistry.WIXIE_CHARM.get()
                && !player.getMainHandItem().isEmpty()
                && worldIn.getBlockEntity(pos) instanceof WixieCauldronTile cauldronTile) {
            MultiRecipeWrapper wrapper = cauldronTile.getRecipesForStack(player.getMainHandItem());
            if (wrapper.isEmpty()) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.wixie.no_recipe"));
            } else {
                cauldronTile.setSetStack(player.getMainHandItem().copy());
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.wixie.recipe_set"));
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILLED);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof WixieCauldronTile cauldronTile) {
            cauldronTile.isOff = world.hasNeighborSignal(pos);
            cauldronTile.updateBlock();
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WixieCauldronTile(pos, state);
    }
}
