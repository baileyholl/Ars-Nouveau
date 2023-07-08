package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOpenGlyphCraft;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class ScribesBlock extends TableBlock {

    public ScribesBlock() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }


    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (world.isClientSide || handIn != InteractionHand.MAIN_HAND || !(world.getBlockEntity(pos) instanceof ScribesTile tile)) {
            return InteractionResult.PASS;
        }
        if (player.getItemInHand(handIn).getItem() instanceof SpellBook && !player.isShiftKeyDown()) {
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PacketOpenGlyphCraft(pos));
            return InteractionResult.SUCCESS;
        }

        if (state.getValue(ScribesBlock.PART) != ThreePartBlock.HEAD) {
            BlockEntity tileEntity = world.getBlockEntity(pos.relative(ScribesBlock.getConnectedDirection(state)));
            tile = tileEntity instanceof ScribesTile ? (ScribesTile) tileEntity : null;
            if (tile == null)
                return InteractionResult.PASS;
        }

        if (!player.isShiftKeyDown()) {

            if (tile.consumeStack(player.getItemInHand(handIn))) {
                return InteractionResult.SUCCESS;
            }

            if (!tile.getStack().isEmpty() && player.getItemInHand(handIn).isEmpty()) {
                ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getStack());
                world.addFreshEntity(item);
                tile.setStack(ItemStack.EMPTY);
            } else if (!player.getInventory().getSelected().isEmpty()) {
                if (!tile.getStack().isEmpty()) {
                    ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getStack());
                    world.addFreshEntity(item);
                }

                tile.setStack(player.getInventory().removeItem(player.getInventory().selected, 1));

            }
            BlockState updateState = world.getBlockState(tile.getBlockPos());
            world.sendBlockUpdated(tile.getBlockPos(), updateState, updateState, 2);
        }
        if (player.isShiftKeyDown()) {
            ItemStack stack = tile.getStack();
            if(player.getItemInHand(handIn).getItem() instanceof DominionWand){
                return InteractionResult.PASS;
            }
            if (stack == null || stack.isEmpty())
                return InteractionResult.SUCCESS;

            if (stack.getItem() instanceof IScribeable scribeable) {
                scribeable.onScribe(world, pos, player, handIn, stack);
                BlockState updateState = world.getBlockState(tile.getBlockPos());
                world.sendBlockUpdated(tile.getBlockPos(), updateState, updateState, 2);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (worldIn.getBlockEntity(pos) instanceof ScribesTile tile && tile.getStack() != null) {
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getStack()));
            tile.refundConsumed();
        }
    }

    // If the user breaks the other side of the table, this side needs to drop its item
    public BlockState tearDown(BlockState state, Direction direction, BlockState state2, LevelAccessor world, BlockPos pos, BlockPos pos2) {
        if (!world.isClientSide()) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ScribesTile tile && ((ScribesTile) entity).getStack() != null) {
                world.addFreshEntity(new ItemEntity((Level) world, pos.getX(), pos.getY(), pos.getZ(), ((ScribesTile) entity).getStack()));
                tile.refundConsumed();
            }
        }
        return Blocks.AIR.defaultBlockState();
    }


    @SubscribeEvent
    public void rightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel().getBlockEntity(event.getPos()) instanceof ScribesTile))
            return;
        Level world = event.getLevel();
        BlockPos pos = event.getPos();

        if (world.getBlockState(pos).getBlock() instanceof ScribesBlock ) {
            if(event.getEntity().getItemInHand(event.getHand()).getItem() instanceof DominionWand){
                return;
            }
            BlockRegistry.SCRIBES_BLOCK.use(world.getBlockState(pos), world, pos, event.getEntity(), event.getHand(), null);
            event.setCanceled(true);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ScribesTile(pos, state);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }
}
