package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IEnchantingRecipe;
import com.hollingsworth.arsnouveau.common.network.HighlightAreaPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchantingApparatusBlock extends TickableModBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public EnchantingApparatusBlock() {
        this(TickableModBlock.defaultProperties().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    public EnchantingApparatusBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantingApparatusTile(pos, state);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {

        if (world.isClientSide || handIn != InteractionHand.MAIN_HAND || !(world.getBlockEntity(pos) instanceof EnchantingApparatusTile tile))
            return ItemInteractionResult.SUCCESS;
        if (tile.isCrafting)
            return ItemInteractionResult.SUCCESS;


        var facing = state.getValue(FACING);
        var maybeCore = world.getBlockState(pos.relative(facing.getOpposite()));
        if (!(maybeCore.getBlock() instanceof ArcaneCore)) {
            PortUtil.sendMessage(player, Component.translatable("alert.core"));
            return ItemInteractionResult.SUCCESS;
        }

        if (!maybeCore.getValue(ArcaneCore.FACING).getAxis().test(facing)) {
            PortUtil.sendMessage(player, Component.translatable("alert.core.wrong_axis"));
            return ItemInteractionResult.SUCCESS;
        }

        if (tile.getStack() == null || tile.getStack().isEmpty()) {
            IEnchantingRecipe recipe = tile.getRecipe(player.getMainHandItem(), player);
            if (recipe == null) {
                List<ColorPos> colorPos = new ArrayList<>();
                for(BlockPos pedPos : tile.pedestalList()){
                    if(world.getBlockEntity(pedPos) instanceof ArcanePedestalTile pedestalTile){
                        colorPos.add(ColorPos.centeredAbove(pedPos));
                    }
                }

                Networking.sendToNearbyClient(world, tile.getBlockPos(), new HighlightAreaPacket(colorPos, 60));

                // This is cursed, but IImbuementRecipe uses ImbuementTile as its Input and
                // ImbuementRecipe calls ImbuementTile#getPedestalItems
                ImbuementTile imbuementTile = new ImbuementTile(pos, BlockRegistry.IMBUEMENT_BLOCK.defaultBlockState());
                imbuementTile.setLevel(world);
                imbuementTile.stack = player.getItemInHand(handIn).copy();
                RecipeHolder<? extends IImbuementRecipe> imbue = imbuementTile.getRecipeNow();

                if (imbue == null) {
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.apparatus.norecipe"));
                } else {
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.apparatus.use_imbuement"));
                }

            } else if (recipe.consumesSource() && !SourceUtil.hasSourceNearby(tile.getBlockPos(), tile.getLevel(), 10, recipe.sourceCost())) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.apparatus.nomana"));
            } else {
                if (tile.attemptCraft(player.getMainHandItem(), player)) {
                    tile.setStack(player.getInventory().removeItem(player.getInventory().selected, 1));
                }
            }
        } else {
            ItemEntity item = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), tile.getStack());
            world.addFreshEntity(item);
            tile.setStack(ItemStack.EMPTY);
            if (tile.attemptCraft(player.getMainHandItem(), player)) {
                tile.setStack(player.getInventory().removeItem(player.getInventory().selected, 1));
            }
        }

        world.sendBlockUpdated(pos, state, state, 2);
        return ItemInteractionResult.SUCCESS;
    }

    public static final VoxelShape DOWN_SHAPE = Block.box(1, 0, 1, 15, 15, 15);
    public static final VoxelShape UP_SHAPE = Block.box(1, 1, 1, 15, 16, 15);
    public static final VoxelShape NORTH_SHAPE = Block.box(1, 1, 0, 15, 15, 15);
    public static final VoxelShape SOUTH_SHAPE = Block.box(1, 1, 1, 15, 15, 16);
    public static final VoxelShape WEST_SHAPE = Block.box(0, 1, 1, 15, 15, 15);
    public static final VoxelShape EAST_SHAPE = Block.box(1, 1, 1, 16, 15, 15);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @Override
    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(worldIn, pos, state, player);
        if (worldIn.getBlockEntity(pos) instanceof EnchantingApparatusTile tile && tile.getStack() != null) {
            worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getStack()));
        }
        return state;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        context.getLevel().scheduleTick(context.getClickedPos(), this, 1);
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }
}
