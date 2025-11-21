package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class EntityDummy extends PathfinderMob implements ISummon, IDispellable {
    @OnlyIn(Dist.CLIENT)
    private PlayerInfo playerInfo;

    public int ticksLeft;
    public float oBob;
    public float bob;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(EntityDummy.class, EntityDataSerializers.OPTIONAL_UUID);

    public EntityDummy(EntityType<? extends PathfinderMob> p_i48577_1_, Level p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    public EntityDummy(Level world) {
        super(ModEntities.ENTITY_DUMMY.get(), world);
    }

    @Override
    public float getManaReserve() {
        return 200;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(OWNER_UUID, Optional.of(Util.NIL_UUID));
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }


    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public void tick() {
        this.moveCloak();
        super.tick();
        if (!level.isClientSide) {

            // Aggro mobs to the dummy every 4 seconds
            if (level.getGameTime() % (4 * 20) == 0) {
                level.getEntitiesOfClass(Mob.class, getBoundingBox().inflate(5, 4, 5)).forEach(l -> l.setTarget(this));
            }

            if (level.getGameTime() % 10 == 0 && level.getPlayerByUUID(getOwnerUUID()) == null) {
                ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
                this.remove(RemovalReason.DISCARDED);
                onSummonDeath(level, null, false);
                return;
            }

            ticksLeft--;
            if (ticksLeft <= 0) {
                ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
                this.remove(RemovalReason.DISCARDED);
                onSummonDeath(level, null, true);
            }
        }
    }

    @Override
    public void aiStep() {
        this.oBob = this.bob;

        super.aiStep();
        float f;
        if (this.onGround() && !this.isDeadOrDying()) {
            f = (float) Math.min(0.1, this.getDeltaMovement().horizontalDistance());
        } else {
            f = 0.0F;
        }

        this.bob = this.bob + (f - this.bob) * 0.4F;
    }


    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double d0 = this.getX() - this.xCloak;
        double d1 = this.getY() - this.yCloak;
        double d2 = this.getZ() - this.zCloak;
        double d3 = 10.0;
        if (d0 > 10.0) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 > 10.0) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 > 10.0) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        if (d0 < -10.0) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 < -10.0) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 < -10.0) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        this.xCloak += d0 * 0.25;
        this.zCloak += d2 * 0.25;
        this.yCloak += d1 * 0.25;
    }

    @Override
    public void rideTick() {
        super.rideTick();
        this.oBob = this.bob;
        this.bob = 0.0F;
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        onSummonDeath(level, cause, false);
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot p_184582_1_) {
        if (!level.isClientSide)
            return ItemStack.EMPTY;

        ItemStack heldStack = level.getPlayerByUUID(getOwnerUUID()) != null ? level.getPlayerByUUID(getOwnerUUID()).getItemBySlot(p_184582_1_) : ItemStack.EMPTY;
        if (heldStack.getItem() == BlockRegistry.MOB_JAR.asItem()) {
            return new ItemStack(BlockRegistry.MOB_JAR.asItem());
        }

        return heldStack;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot p_184201_1_, @NotNull ItemStack p_184201_2_) {
    }

    public ResourceLocation getSkinTextureLocation() {
        PlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultTexture() : networkplayerinfo.getSkin().texture();
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public PlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(getOwnerUUID());
        }
        return this.playerInfo;
    }

    public @NotNull Component getName() {
        return this.level.getPlayerByUUID(getOwnerUUID()) == null ? Component.literal("") : this.level.getPlayerByUUID(getOwnerUUID()).getName();
    }

    public @NotNull Component getDisplayName() {
        MutableComponent iformattabletextcomponent = Component.literal("");
        iformattabletextcomponent = iformattabletextcomponent.append(PlayerTeam.formatNameForTeam(this.getTeam(), this.getName()));
        return iformattabletextcomponent;
    }


    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("left", ticksLeft);
        writeOwner(tag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.ticksLeft = tag.getInt("left");
        if (getOwnerUUID() != null)
            setOwnerID(tag.getUUID("owner"));
    }

    @Override
    public int getTicksLeft() {
        return ticksLeft;
    }

    @Override
    public void setTicksLeft(int ticks) {
        this.ticksLeft = ticks;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.getEntityData().get(OWNER_UUID).isEmpty() ? this.getUUID() : this.getEntityData().get(OWNER_UUID).get();
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.getEntityData().set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSlim() {
        if (this.playerInfo != null) {
            return playerInfo.getSkin().model() == PlayerSkin.Model.SLIM;
        } else return false;
    }

    @Override
    public boolean onDispel(@NotNull LivingEntity caster) {
        this.ticksLeft = 0;
        return true;
    }
}
