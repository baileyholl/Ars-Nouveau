package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.ITurretBehavior;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.block.tile.RotatingTurretTile;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class RotatingSpellTurret extends BasicSpellTurret {

    public static HashMap<AbstractCastMethod, ITurretBehavior> ROT_TURRET_BEHAVIOR_MAP = new HashMap<>();

    /**
     * Get the position where the dispenser at the given Coordinates should dispense to.
     */
    public static Position getDispensePosition(BlockPos coords, RotatingTurretTile tile) {
        Vec3 direction = tile.getShootAngle().normalize();
        double d0 = coords.getX() + 0.5 + 0.5D * direction.x();
        double d1 = coords.getY()  + 0.5 + 0.5D * direction.y();
        double d2 = coords.getZ()  + 0.5 + 0.5D * direction.z();
        return new Vec3(d0, d1, d2);
    }



    @Override
    public void setPlacedBy(Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
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
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RotatingTurretTile(pos, state);
    }

    static {
        ROT_TURRET_BEHAVIOR_MAP.put(MethodProjectile.INSTANCE, new ITurretBehavior() {
            @Override
            public void onCast(SpellResolver resolver, ServerLevel world, BlockPos pos, Player fakePlayer, Position iposition, Direction direction) {
                if(world.getBlockEntity(pos) instanceof RotatingTurretTile rotatingTurretTile) {
                    EntityProjectileSpell spell = new EntityProjectileSpell(world, resolver);
                    spell.setOwner(fakePlayer);
                    spell.setPos(iposition.x(), iposition.y(), iposition.z());
                    Vec3 vec3d = rotatingTurretTile.getShootAngle().normalize();
                    SpellStats stats = resolver.getCastStats();
                    float velocity = Math.max(0.1f, 0.75f + stats.getAccMultiplier() / 2);
                    spell.shoot(vec3d.x(), vec3d.y(), vec3d.z(), velocity, 0);
                    world.addFreshEntity(spell);
                }
            }
        });


        ROT_TURRET_BEHAVIOR_MAP.put(MethodTouch.INSTANCE, new ITurretBehavior() {
            @Override
            public void onCast(SpellResolver resolver, ServerLevel serverLevel, BlockPos pos, Player fakePlayer, Position dispensePosition, Direction facingDir) {
                BlockPos touchPos = pos.relative(facingDir);

                if(!(serverLevel.getBlockEntity(pos) instanceof RotatingTurretTile rotatingTurretTile)) {
                    return;
                }
                Vec3 aimVec = rotatingTurretTile.getShootAngle().add(rotatingTurretTile.getX() + 0.5, rotatingTurretTile.getY() + 0.5, rotatingTurretTile.getZ() + 0.5);
                List<LivingEntity> entityList = serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(touchPos));
                if (!entityList.isEmpty()) {
                    LivingEntity entity = entityList.get(serverLevel.random.nextInt(entityList.size()));
                    resolver.onCastOnEntity(ItemStack.EMPTY, entity, InteractionHand.MAIN_HAND);
                } else {
                   resolver.onCastOnBlock(new BlockHitResult(aimVec, facingDir, BlockPos.containing(aimVec.x(), aimVec.y(), aimVec.z()), true));
                }
            }
        });

    }
    
}
