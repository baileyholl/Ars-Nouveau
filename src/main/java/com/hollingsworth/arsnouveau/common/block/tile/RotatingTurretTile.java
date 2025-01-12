package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.RotatingSpellTurret;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.core.Direction.*;

public class RotatingTurretTile extends BasicSpellTurretTile implements IWandable {
    public RotatingTurretTile(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public RotatingTurretTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ROTATING_TURRET_TILE.get(), pos, state);
    }

    public float rotationX;
    public float rotationY;
    public float neededRotationX;
    public float neededRotationY;

    // Step between current and needed rotation on the client each tick, smoothly animate with partials between
    public float clientNeededX;
    public float clientNeededY;
    @Override
    public void tick() {
        super.tick();
        // Animated in the renderer
        if(level.isClientSide){
            if(clientNeededX != neededRotationX){
                float diff = neededRotationX - clientNeededX;
                if(Math.abs(diff) < 0.1){
                    clientNeededX = neededRotationX;
                }else{
                    clientNeededX += diff * 0.1f;
                }
            }
            if(clientNeededY != neededRotationY){
                float diff = neededRotationY - clientNeededY;
                if(Math.abs(diff) < 0.1){
                    clientNeededY = neededRotationY;
                }else{
                    clientNeededY += diff * 0.1f;
                }
            }
            if(rotationX != clientNeededX){
                float diff = clientNeededX - rotationX;
                if(Math.abs(diff) < 0.1){
                    rotationX = clientNeededX;
                }else{
                    rotationX += diff * 0.1f;
                }
            }
            if(rotationY != clientNeededY){
                float diff = clientNeededY - rotationY;
                if(Math.abs(diff) < 0.1){
                    rotationY = clientNeededY;
                }else{
                    rotationY += diff * 0.1f;
                }
            }
            return;
        }
        if(rotationX != neededRotationX){
            float diff = neededRotationX - rotationX;
            if(Math.abs(diff) < 0.1){
                setRotationX(neededRotationX);
            }else{
                setRotationX(rotationX + diff * 0.1f);
            }
            setChanged();
        }
        if(rotationY != neededRotationY){
            float diff = neededRotationY - rotationY;
            if(Math.abs(diff) < 0.1){
                setRotationY(neededRotationY);
            }else{
                setRotationY(rotationY + diff * 0.1f);
            }
            setChanged();
        }
    }

    public void aim(@Nullable BlockPos blockPos, Player playerEntity) {
        if (blockPos == null) return;

        Vec3 thisVec = Vec3.atCenterOf(getBlockPos());
        Vec3 blockVec = Vec3.atCenterOf(blockPos);

        Vec3 diffVec = blockVec.subtract(thisVec);
        Vec3 diffVec2D = new Vec3(diffVec.x, diffVec.z, 0);
        Vec3 rotVec = new Vec3(0, 1, 0);
        float angle = (float) (angleBetween(rotVec, diffVec2D) / Math.PI * 180.0f);

        if (blockVec.x < thisVec.x) {
            angle = -angle;
        }

        neededRotationX = angle + 90f;

        rotVec = new Vec3(diffVec.x, 0, diffVec.z);
        angle = (float) (angleBetween(diffVec, rotVec) * 180F / (float)Math.PI);
        if (blockVec.y < thisVec.y) {
            angle = -angle;
        }
        neededRotationY = angle;

        updateBlock();
        ParticleUtil.beam(blockPos, getBlockPos(), level);
        PortUtil.sendMessageNoSpam(playerEntity, Component.literal("Turret now aims to " + blockPos.toShortString()));
    }

    public static double angleBetween(Vec3 a, Vec3 b) {
        double projection = a.normalize().dot(b.normalize());
        return Math.acos(Mth.clamp(projection, -1, 1));
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null) this.aim(storedPos, playerEntity);
    }

    @Override
    public void shootSpell() {
        BlockPos pos = getBlockPos();
        if (spellCaster.getSpell().isEmpty() || !(level instanceof ServerLevel level))
            return;
        int manaCost = getManaCost();
        if (manaCost > 0 && SourceUtil.takeSourceMultipleWithParticles(pos, level, 10, manaCost) == null)
            return;
        Networking.sendToNearbyClient(level, pos, new PacketOneShotAnimation(pos));
        Position iposition = RotatingSpellTurret.getDispensePosition(pos, this);
        FakePlayer fakePlayer = uuid != null
                ? FakePlayerFactory.get(level, new GameProfile(uuid, ""))
                : ANFakePlayer.getPlayer(level);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(level, spellCaster.getSpell(), fakePlayer, new TileCaster(this, SpellContext.CasterType.TURRET)));
        if (resolver.castType != null && RotatingSpellTurret.ROT_TURRET_BEHAVIOR_MAP.containsKey(resolver.castType)) {
            RotatingSpellTurret.ROT_TURRET_BEHAVIOR_MAP.get(resolver.castType).onCast(resolver, level, pos, fakePlayer, iposition, orderedByNearest()[0].getOpposite());
            spellCaster.playSound(pos, level, null, spellCaster.getCurrentSound(), SoundSource.BLOCKS);
        }
    }

    public Direction[] orderedByNearest() {
        float f = this.getRotationY() * (float) Math.PI / 180F;
        float f1 = (90 + this.getRotationX()) * (float) Math.PI / 180F;
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putFloat("rotationY", rotationY);
        tag.putFloat("rotationX", rotationX);
        tag.putFloat("neededRotationY", neededRotationY);
        tag.putFloat("neededRotationX", neededRotationX);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        rotationX = tag.getFloat("rotationX");
        rotationY = tag.getFloat("rotationY");
        neededRotationX = tag.getFloat("neededRotationX");
        neededRotationY = tag.getFloat("neededRotationY");
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public void setRotationX(float rot) {
        rotationX = rot;
    }

    public void setRotationY(float rot) {
        rotationY = rot;
    }


    /**
     * @return Vector for projectile shooting. Don't ask me why it works, it was pure luck.
     */
    public Vec3 getShootAngle() {
        float f = getRotationY() * ((float) Math.PI / 180F);
        float f1 = (90 + getRotationX()) * ((float) Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4).reverse();
    }
}
