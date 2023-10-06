package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

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
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("rotationY", rotationY);
        tag.putFloat("rotationX", rotationX);
        tag.putFloat("neededRotationY", neededRotationY);
        tag.putFloat("neededRotationX", neededRotationX);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
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
