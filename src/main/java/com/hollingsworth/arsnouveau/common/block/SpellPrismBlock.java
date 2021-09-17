package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.block.*;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.Position;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class SpellPrismBlock extends ModBlock{
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public SpellPrismBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public SpellPrismBlock(String name){
        super(name);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    public static void redirectSpell(ServerWorld world, BlockPos pos, EntityProjectileSpell spell){
        IPosition iposition = getDispensePosition(new ProxyBlockSource(world, pos));
        Direction direction = world.getBlockState(pos).getValue(DispenserBlock.FACING);
        spell.setPos(iposition.x(), iposition.y(), iposition.z());
        spell.shoot(direction.getStepX(), ((float)direction.getStepY()), direction.getStepZ(), 0.5f, 0);
        for(Direction d : Direction.values()){
            if(world.getBlockState(pos.relative(d)).getBlock() instanceof ObserverBlock){
                world.getBlockTicks().scheduleTick(pos.relative(d), world.getBlockState(pos.relative(d)).getBlock(), 2);
            }
        }
    }


    public static IPosition getDispensePosition(IBlockSource coords) {
        Direction direction = coords.getBlockState().getValue(FACING);
        double d0 = coords.x() + 0.3D * (double)direction.getStepX();
        double d1 = coords.y() + 0.3D * (double)direction.getStepY();
        double d2 = coords.z() + 0.3D * (double)direction.getStepZ();
        return new Position(d0, d1, d2);
    }
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }
}
