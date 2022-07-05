package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
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
        super(defaultProperties().noOcclusion(), LibBlockNames.WIXIE_CAULDRON);
        registerDefaultState(defaultBlockState().setValue(CONVERTED, false).setValue(FILLED, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(worldIn.isClientSide || handIn != InteractionHand.MAIN_HAND || !(worldIn.getBlockEntity(pos) instanceof WixieCauldronTile))
            return InteractionResult.SUCCESS;

        if(player.getMainHandItem().getItem() != ItemsRegistry.WIXIE_CHARM && !player.getMainHandItem().isEmpty()){
            ((WixieCauldronTile) worldIn.getBlockEntity(pos)).setRecipes(player, player.getMainHandItem());
            worldIn.sendBlockUpdated(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
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
        if(!world.isClientSide() && world.getBlockEntity(pos) instanceof WixieCauldronTile cauldronTile){
            cauldronTile.setOff(world.hasNeighborSignal(pos));
            cauldronTile.updateBlock();
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WixieCauldronTile(pos, state);
    }
}
