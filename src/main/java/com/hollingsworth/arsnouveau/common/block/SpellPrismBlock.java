package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAccelerate;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDecelerate;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SpellPrismBlock extends ModBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public SpellPrismBlock(Properties properties) {
        super(properties);
    }

    public SpellPrismBlock() {
        super();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    public static void redirectSpell(ServerLevel world, BlockPos pos, EntityProjectileSpell spell) {
        Position iposition = getDispensePosition(new BlockSourceImpl(world, pos));
        Direction direction = world.getBlockState(pos).getValue(DispenserBlock.FACING);
        spell.setPos(iposition.x(), iposition.y(), iposition.z());
        if (spell.spellResolver == null) {
            spell.remove(Entity.RemovalReason.DISCARDED);
            return;
        }
        float acceleration = (spell.spellResolver.spell.getBuffsAtIndex(0, null, AugmentAccelerate.INSTANCE) - spell.spellResolver.spell.getBuffsAtIndex(0, null, AugmentDecelerate.INSTANCE) * 0.5F);
        float velocity = Math.max(0.1f, 0.5f + 0.1f * Math.min(2, acceleration));

        spell.shoot(direction.getStepX(), ((float) direction.getStepY()), direction.getStepZ(), velocity, 0);
        for (Direction d : Direction.values()) {
            BlockPos adjacentPos = pos.relative(d);
            if (world.getBlockState(adjacentPos).getBlock() instanceof ObserverBlock) {
                BlockState observer = world.getBlockState(adjacentPos);
                if (adjacentPos.relative(observer.getValue(FACING)).equals(pos)) { // Make sure the observer is facing us.
                    world.scheduleTick(pos.relative(d), world.getBlockState(pos.relative(d)).getBlock(), 2);
                }
            }
        }
    }


    public static Position getDispensePosition(BlockSource coords) {
        Direction direction = coords.getBlockState().getValue(FACING);
        double d0 = coords.x() + 0.3D * (double) direction.getStepX();
        double d1 = coords.y() + 0.3D * (double) direction.getStepY();
        double d2 = coords.z() + 0.3D * (double) direction.getStepZ();
        return new PositionImpl(d0, d1, d2);
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
}
