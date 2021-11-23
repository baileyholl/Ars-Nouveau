package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
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
        float velocity = 0.5f + 0.1f * Math.min(2, spell.spellResolver.spell.getBuffsAtIndex(0, null, AugmentAccelerate.INSTANCE));

        spell.shoot(direction.getStepX(), ((float)direction.getStepY()), direction.getStepZ(), velocity, 0);
        for(Direction d : Direction.values()){
            BlockPos adjacentPos = pos.relative(d);
            if(world.getBlockState(adjacentPos).getBlock() instanceof ObserverBlock){
                BlockState observer = world.getBlockState(adjacentPos);
                if(adjacentPos.relative(observer.getValue(FACING)).equals(pos)) { // Make sure the observer is facing us.
                    world.getBlockTicks().scheduleTick(pos.relative(d), world.getBlockState(pos.relative(d)).getBlock(), 2);
                }
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
