package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketMountScryBot;
import com.hollingsworth.arsnouveau.common.network.PacketMoveScryBot;
import com.hollingsworth.arsnouveau.common.network.PacketSetCameraView;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class ScryBot extends Mob implements ICameraCallback {

    public Player mountedPlayer;
    private boolean loadedChunks;
    boolean hasSentChunks;

    private ChunkTrackingView cameraChunks = null;

    public ScryBot(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if(player.level.isClientSide){
            this.mountedPlayer = player;
            Networking.sendToServer(new PacketMountScryBot(this.getId()));
        }
        return super.interactAt(player, vec, hand);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public void tick() {
        super.tick();

    }

    public void playerMounting(ServerPlayer serverPlayer){
        int viewDistance = Mth.clamp(serverPlayer.requestedViewDistance(), 2, serverPlayer.server.getPlayerList().getViewDistance());
        this.setChunkLoadingDistance(viewDistance);
        serverPlayer.camera = this;
        Networking.sendToPlayerClient(new PacketSetCameraView(this), serverPlayer);
        this.mountedPlayer = serverPlayer;
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.LEFT;
    }

    @Override
    public boolean isAlwaysTicking() {
        return mountedPlayer != null;
    }


    public void onMove(PacketMoveScryBot packet) {
//        System.out.println(packet);
//        double d0 = clampHorizontal(packet.leftImpulse);
//        double d1 = clampVertical(packet.getY(this.player.getY()));
//        double d2 = clampHorizontal(packet.getZ(this.player.getZ()));
//        float f = Mth.wrapDegrees(packet.getYRot(this.player.getYRot()));
//        float f1 = Mth.wrapDegrees(packet.getXRot(this.player.getXRot()));
//
//        double d3 = this.getX();
//        double d4 = this.getY();
//        double d5 = this.getZ();
//        double d6 = d0 - this.getX();
//        double d7 = d1 - this.getY();
//        double d8 = d2 - this.getZ();
//        double d9 = this.getDeltaMovement().lengthSqr();
//        double d10 = d6 * d6 + d7 * d7 + d8 * d8;
//
//        boolean flag = false;// this.player.isFallFlying();
//
//
//        AABB aabb = this.getBoundingBox();
//        d6 = d0 - this.getX();
//        d7 = d1 - this.getY();
//        d8 = d2 - this.getZ();
//        boolean flag4 = d7 > 0.0;
//        if (this.onGround() && !packet.isOnGround() && flag4) {
//            this.jumpFromGround();
//        }
//
//        boolean flag1 = this.verticalCollisionBelow;

        this.xRot = packet.xRot;
        this.yRot = packet.yRot;
        float strafe = packet.leftImpulse;
        float forward = packet.forwardImpulse;
        if (strafe >= -1.0F && strafe <= 1.0F) {
            this.xxa = strafe;
        }

        if (forward >= -1.0F && forward <= 1.0F) {
            this.zza = forward;
        }

//        this.jumping = packet.jumping;
//        this.setShiftKeyDown(packet.shiftKeyDown);
        this.hasImpulse = true;
        System.out.println(this.getDeltaMovement());
//        System.out.println(packet.leftImpulse);
//        System.out.println(this.position);
//        this.move(MoverType.PLAYER, new Vec3(packet.leftImpulse, 0, packet.forwardImpulse));
//        this.move(MoverType.PLAYER, new Vec3(d6, d7, d8));


    }

    @Override
    public void travel(Vec3 travelVector) {
//        if(travelVector.x > 0) {
            super.travel(travelVector);
//        }
    }

    private static double clampHorizontal(double value) {
        return Mth.clamp(value, -3.0E7, 3.0E7);
    }

    private static double clampVertical(double value) {
        return Mth.clamp(value, -2.0E7, 2.0E7);
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return this.mountedPlayer != null;
    }

    @Override
    public void onLeftPressed() {

    }

    @Override
    public void onRightPressed() {

    }

    @Override
    public void onUpPressed() {

    }

    @Override
    public void onDownPressed() {

    }

    @Override
    public void stopViewing(ServerPlayer player) {

    }

    public ChunkTrackingView getCameraChunks() {
        return cameraChunks;
    }

    @Override
    public boolean shouldUpdateChunkTracking(ServerPlayer player) {
        return false;
    }

    public void setChunkLoadingDistance(int chunkLoadingDistance) {
        cameraChunks = ChunkTrackingView.of(chunkPosition(), chunkLoadingDistance);
    }

    public void discardCamera() {
        if (!this.level.isClientSide) {

            if (level.getBlockEntity(this.blockPosition()) instanceof ICameraMountable camMount) {
                camMount.stopViewing();
            }

            SectionPos chunkPos = SectionPos.of(blockPosition());
            int chunkLoadingDistance = cameraChunks instanceof ChunkTrackingView.Positioned positionedChunks ? positionedChunks.viewDistance() : level().getServer().getPlayerList().getViewDistance();

            for (int x = chunkPos.getX() - chunkLoadingDistance; x <= chunkPos.getX() + chunkLoadingDistance; x++) {
                for (int z = chunkPos.getZ() - chunkLoadingDistance; z <= chunkPos.getZ() + chunkLoadingDistance; z++) {
                    ArsNouveau.ticketController.forceChunk((ServerLevel) level(), this, x, z, false, false);
                }
            }
        }
    }


    public boolean hasSentChunks() {
        return hasSentChunks;
    }

    public void setHasSentChunks(boolean hasSentChunks) {
        this.hasSentChunks = hasSentChunks;
    }
}
