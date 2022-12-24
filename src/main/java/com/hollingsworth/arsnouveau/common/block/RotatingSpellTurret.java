package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.core.Direction.*;

public class RotatingSpellTurret extends BasicSpellTurret {
    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.getItemInHand(handIn).getItem() instanceof WarpScroll) {
            BlockPos aimPos = new WarpScroll.WarpScrollData(player.getItemInHand(handIn)).getPos();
            if (player.getLevel().getBlockEntity(pos) instanceof RotatingTurretTile tile) {
                tile.aim(aimPos, player);
            }
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    //Direction Adjustments
    public void shootSpell(ServerLevel world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof RotatingTurretTile tile)) return;
        ISpellCaster caster = tile.getSpellCaster();
        if (caster.getSpell().isEmpty())
            return;
        int manaCost = tile.getManaCost();
        if (manaCost > 0 && SourceUtil.takeSourceWithParticles(pos, world, 10, manaCost) == null)
            return;
        Networking.sendToNearby(world, pos, new PacketOneShotAnimation(pos));
        Position iposition = getDispensePosition(new BlockSourceImpl(world, pos), tile);
        FakePlayer fakePlayer = ANFakePlayer.getPlayer(world);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(world, caster.getSpell(), fakePlayer, new TileCaster(tile, SpellContext.CasterType.TURRET)));
        if (resolver.castType != null && TURRET_BEHAVIOR_MAP.containsKey(resolver.castType)) {
            TURRET_BEHAVIOR_MAP.get(resolver.castType).onCast(resolver, tile, world, pos, fakePlayer, iposition, orderedByNearest(tile)[0].getOpposite());
            caster.playSound(pos, world, null, caster.getCurrentSound(), SoundSource.BLOCKS);
        }
    }

    /**
     * Get the position where the dispenser at the given Coordinates should dispense to.
     */
    public static Position getDispensePosition(BlockSource coords, RotatingTurretTile tile) {
        Vec3 direction = tile.getShootAngle().normalize();
        double d0 = coords.x() + 0.5D * direction.x();
        double d1 = coords.y() + 0.5D * direction.y();
        double d2 = coords.z() + 0.5D * direction.z();
        return new PositionImpl(d0, d1, d2);
    }

    public static Direction[] orderedByNearest(RotatingTurretTile pEntity) {
        float f = pEntity.getRotationY() * (float) Math.PI / 180F;
        float f1 = (90 + pEntity.getRotationX()) * (float) Math.PI / 180F;
        float f2 = Mth.sin(f);
        float f3 = Mth.cos(f);
        float f4 = Mth.sin(f1);
        float f5 = Mth.cos(f1);
        boolean flag = f4 > 0.0F;
        boolean flag1 = f2 < 0.0F;
        boolean flag2 = f5 > 0.0F;
        float f6 = flag ? f4 : -f4;
        float f7 = flag1 ? -f2 : f2;
        float f8 = flag2 ? f5 : -f5;
        float f9 = f6 * f3;
        float f10 = f8 * f3;
        Direction direction = flag ? EAST : WEST;
        Direction direction1 = flag1 ? UP : DOWN;
        Direction direction2 = flag2 ? SOUTH : NORTH;
        if (f6 > f8) {
            if (f7 > f9) {
                return makeDirectionArray(direction1, direction, direction2);
            } else {
                return f10 > f7 ? makeDirectionArray(direction, direction2, direction1) : makeDirectionArray(direction, direction1, direction2);
            }
        } else if (f7 > f10) {
            return makeDirectionArray(direction1, direction2, direction);
        } else {
            return f9 > f7 ? makeDirectionArray(direction2, direction, direction1) : makeDirectionArray(direction2, direction1, direction);
        }
    }

    static Direction[] makeDirectionArray(Direction pFirst, Direction pSecond, Direction pThird) {
        return new Direction[]{pFirst, pSecond, pThird, pThird.getOpposite(), pSecond.getOpposite(), pFirst.getOpposite()};
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Direction orientation = placer == null ? Direction.WEST : Direction.orderedByNearest(placer)[0].getOpposite();

        if (!(world.getBlockEntity(pos) instanceof RotatingTurretTile turretTile)) return;
        switch (orientation) {
            case DOWN:
                turretTile.rotationY = -90F;
                break;
            case UP:
                turretTile.rotationY = 90F;
                break;
            case NORTH:
                turretTile.rotationX = 270F;
                break;
            case SOUTH:
                turretTile.rotationX = 90F;
                break;
            case WEST:
                break;
            case EAST:
                turretTile.rotationX = 180F;
                break;
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RotatingTurretTile(pos, state);
    }

}
