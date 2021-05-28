package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
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

public class RitualBlock extends ModBlock{
    public RitualBlock(String registryName) {
        super(defaultProperties().noOcclusion().lightLevel((b) -> b.getValue(LIT) ? 15 : 0), registryName);
    }
    public static final Property<Boolean> LIT = BooleanProperty.create("lit");

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!(worldIn.getBlockEntity(pos) instanceof RitualTile) || handIn != Hand.MAIN_HAND || !player.getMainHandItem().isEmpty())
            return super.use(state, worldIn, pos, player, handIn, hit);
        RitualTile tile = (RitualTile) worldIn.getBlockEntity(pos);
        if(tile.ritual != null && !tile.isRitualDone()) {
            tile.startRitual();
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }


    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        if(!world.isClientSide() && world.getBlockEntity(pos) instanceof RitualTile){
            ((RitualTile) world.getBlockEntity(pos)).isOff = world.hasNeighborSignal(pos);
            BlockUtil.safelyUpdateState(world, pos);
        }
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if(worldIn.getBlockEntity(pos) instanceof RitualTile){
            RitualTile tile = (RitualTile) worldIn.getBlockEntity(pos);
            if(tile.ritual != null && !tile.ritual.isRunning() && !tile.ritual.isDone()){
                worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ArsNouveauAPI.getInstance().getRitualItemMap().get(tile.ritual.getID()))));
            }

        }
    }


    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            world.setBlock(pos, state.setValue(LIT, false), 2);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RitualTile();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
