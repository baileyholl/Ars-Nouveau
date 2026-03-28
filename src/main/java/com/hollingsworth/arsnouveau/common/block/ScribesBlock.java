package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOpenGlyphCraft;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ScribesBlock extends TableBlock {

    public ScribesBlock() {
        super();
        NeoForge.EVENT_BUS.register(this);
    }


    @Override
    public InteractionResult useItemOn(ItemStack heldStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (world.isClientSide() || handIn != InteractionHand.MAIN_HAND || !(world.getBlockEntity(pos) instanceof ScribesTile tile)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (player.getItemInHand(handIn).getItem() instanceof SpellBook && !player.isShiftKeyDown()) {
            Networking.sendToPlayerClient(new PacketOpenGlyphCraft(pos), (ServerPlayer) player);
            return InteractionResult.SUCCESS;
        }

        if (state.getValue(ScribesBlock.PART) != ThreePartBlock.HEAD) {
            BlockPos headPos = pos.relative(ScribesBlock.getConnectedDirection(state));
            if (world.getBlockEntity(headPos) instanceof ScribesTile head) {
                BlockState headState = head.getBlockState();
                return headState.useItemOn(heldStack, world, player, handIn, hit.withPosition(headPos));
            }

            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!player.isShiftKeyDown()) {

            if (tile.consumeStack(player.getItemInHand(handIn))) {
                return InteractionResult.SUCCESS;
            }

            if (!tile.getStack().isEmpty() && player.getItemInHand(handIn).isEmpty()) {
                ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getStack());
                world.addFreshEntity(item);
                tile.setStack(ItemStack.EMPTY);
            } else if (!player.getInventory().getItem(player.getInventory().getSelectedSlot()).isEmpty()) {
                if (!tile.getStack().isEmpty()) {
                    ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getStack());
                    world.addFreshEntity(item);
                }

                tile.setStack(player.getInventory().removeItem(player.getInventory().getSelectedSlot(), 1));

            }
            BlockState updateState = tile.getBlockState();
            world.sendBlockUpdated(tile.getBlockPos(), updateState, updateState, 2);
        }
        if (player.isShiftKeyDown()) {
            ItemStack stack = tile.getStack();
            if (player.getItemInHand(handIn).getItem() instanceof DominionWand) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            if (stack == null || stack.isEmpty())
                return InteractionResult.TRY_WITH_EMPTY_HAND;

            if (stack.getItem() instanceof IScribeable scribeable) {
                scribeable.onScribe(world, pos, player, handIn, stack);
                BlockState updateState = tile.getBlockState();
                world.sendBlockUpdated(tile.getBlockPos(), updateState, updateState, 2);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (worldIn.getBlockEntity(pos) instanceof ScribesTile tile && tile.getStack() != null) {
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getStack()));
            tile.refundConsumed();
        }
        return state;
    }


    // If the user breaks the other side of the table, this side needs to drop its item
    @Override
    public BlockState tearDown(BlockState state, Direction direction, BlockState state2, LevelReader world, BlockPos pos, BlockPos pos2) {
        if (!world.isClientSide()) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ScribesTile tile && tile.getStack() != null) {
                Level level = (Level) world;
                level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), tile.getStack()));
                tile.setStack(ItemStack.EMPTY);
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
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof ScribesBlock) {
            ItemStack stack = event.getEntity().getItemInHand(event.getHand());
            if (stack.getItem() instanceof DominionWand) {
                return;
            }
            BlockRegistry.SCRIBES_BLOCK.get().useItemOn(stack, state, world, pos, event.getEntity(), event.getHand(), event.getHitVec());
            event.setCanceled(true);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ScribesTile(pos, state);
    }

    @Override
    public boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }
}
