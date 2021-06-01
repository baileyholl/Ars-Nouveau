package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.block.tile.SpellTurretTile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.block.*;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.Position;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SpellTurret extends ModBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public SpellTurret() {
        super(defaultProperties().noOcclusion(), LibBlockNames.SPELL_TURRET);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.FALSE));
    }

    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        this.shootSpell(worldIn, pos);
    }

    public void shootSpell(ServerWorld world, BlockPos pos ) {
        SpellTurretTile tile = (SpellTurretTile) world.getBlockEntity(pos);

        if(tile == null || tile.recipe == null || tile.recipe.isEmpty())
            return;
        int manaCost = new Spell(tile.recipe).getCastingCost()/2;
        if(ManaUtil.takeManaNearbyWithParticles(pos, world, 10, manaCost) == null)
            return;

        IPosition iposition = getDispensePosition(new ProxyBlockSource(world, pos));
        Direction direction = world.getBlockState(pos).getValue(FACING);
        FakePlayer fakePlayer = new ANFakePlayer(world);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        EntitySpellResolver resolver = new EntitySpellResolver(tile.recipe, new SpellContext(tile.recipe, fakePlayer).withCastingTile(world.getBlockEntity(pos)));
        if(resolver.castType instanceof MethodProjectile){
            shootProjectile(world,pos,tile, resolver);
            return;
        }
        if(resolver.castType instanceof MethodTouch){
            BlockPos touchPos = new BlockPos(iposition.x(), iposition.y(), iposition.z());
            if(direction == Direction.WEST || direction == Direction.NORTH){
                touchPos = touchPos.relative(direction);
            }
            if(direction == Direction.DOWN) // Why do I need to do this? Why does the vanilla dispenser code not offset correctly for DOWN?
                touchPos = touchPos.below();
            resolver.onCastOnBlock(new BlockRayTraceResult(new Vector3d(touchPos.getX(), touchPos.getY(), touchPos.getZ()),
                    direction.getOpposite(), new BlockPos(touchPos.getX(), touchPos.getY(), touchPos.getZ()), false),
                   fakePlayer);
        }
    }

    public void shootProjectile(ServerWorld world,BlockPos pos, SpellTurretTile tile, SpellResolver resolver){
        IPosition iposition = getDispensePosition(new ProxyBlockSource(world, pos));
        Direction direction = world.getBlockState(pos).getValue(DispenserBlock.FACING);
        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft(world);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        EntityProjectileSpell spell = new EntityProjectileSpell(world, fakePlayer,resolver,
                AbstractSpellPart.getBuffCount(new Spell(tile.recipe).getAugments(0, null), AugmentPierce.class));
        spell.setOwner(fakePlayer);
        spell.setPos(iposition.x(), iposition.y(), iposition.z());
        spell.shoot(direction.getStepX(), ((float)direction.getStepY()), direction.getStepZ(), 0.5f, 0);
        world.addFreshEntity(spell);
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean flag = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(pos.above());
        boolean flag1 = state.getValue(TRIGGERED);
        if (flag && !flag1) {
            worldIn.getBlockTicks().scheduleTick(pos, this,4);
            worldIn.setBlock(pos, state.setValue(TRIGGERED, Boolean.TRUE), 4);

        } else if (!flag && flag1) {
            worldIn.setBlock(pos, state.setValue(TRIGGERED, Boolean.FALSE), 4);
        }
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }


    public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
        return Container.getRedstoneSignalFromBlockEntity(worldIn.getBlockEntity(pos));
    }


    /**
     * Get the position where the dispenser at the given Coordinates should dispense to.
     */
    public static IPosition getDispensePosition(IBlockSource coords) {
        Direction direction = coords.getBlockState().getValue(FACING);
        double d0 = coords.x() + 0.5D * (double)direction.getStepX();
        double d1 = coords.y() + 0.5D * (double)direction.getStepY();
        double d2 = coords.z() + 0.5D * (double)direction.getStepZ();
        return new Position(d0, d1, d2);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(handIn == Hand.MAIN_HAND){
            ItemStack stack = player.getItemInHand(handIn);
            if(!(stack.getItem() instanceof SpellParchment) || worldIn.isClientSide)
                return ActionResultType.SUCCESS;
            List<AbstractSpellPart> recipe = SpellParchment.getSpellRecipe(stack);
            if(recipe == null || recipe.isEmpty())
                return ActionResultType.SUCCESS;
            if(!(recipe.get(0) instanceof MethodTouch || recipe.get(0) instanceof MethodProjectile)){
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.alert.turret_type"));
                return ActionResultType.SUCCESS;
            }

            ((SpellTurretTile)worldIn.getBlockEntity(pos)).recipe = recipe;
            PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.alert.spell_set"));
            worldIn.sendBlockUpdated(pos, state, state, 2);
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SpellTurretTile();
    }

}
