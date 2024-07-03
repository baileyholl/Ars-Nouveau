package com.hollingsworth.arsnouveau.common.entity;

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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class EntityDummy extends PathfinderMob implements ISummon {
    @OnlyIn(Dist.CLIENT)
    private PlayerInfo playerInfo;

    public int ticksLeft;
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(EntityDummy.class, EntityDataSerializers.OPTIONAL_UUID);

    public EntityDummy(EntityType<? extends PathfinderMob> p_i48577_1_, Level p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    public EntityDummy(Level world) {
        super(ModEntities.ENTITY_DUMMY.get(), world);
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
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(OWNER_UUID, Optional.of(Util.NIL_UUID));
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }


    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
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
    public void die(DamageSource cause) {
        super.die(cause);
        onSummonDeath(level, cause, false);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_184582_1_) {
        if (!level.isClientSide)
            return ItemStack.EMPTY;

        ItemStack heldStack = level.getPlayerByUUID(getOwnerUUID()) != null ? level.getPlayerByUUID(getOwnerUUID()).getItemBySlot(p_184582_1_) : ItemStack.EMPTY;
        if(heldStack.getItem() == BlockRegistry.MOB_JAR.asItem()){
            return new ItemStack(BlockRegistry.MOB_JAR.asItem());
        }

        return heldStack;
    }

    @Override
    public void setItemSlot(EquipmentSlot p_184201_1_, ItemStack p_184201_2_) {
    }

    public ResourceLocation getSkinTextureLocation() {
        PlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultTexture() : networkplayerinfo.getSkin().texture();
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    protected PlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(getOwnerUUID());
        }
        return this.playerInfo;
    }

    public Component getName() {
        return this.level.getPlayerByUUID(getOwnerUUID()) == null ? Component.literal("") : this.level.getPlayerByUUID(getOwnerUUID()).getName();
    }

    public Component getDisplayName() {
        MutableComponent iformattabletextcomponent = Component.literal("");
        iformattabletextcomponent = iformattabletextcomponent.append(PlayerTeam.formatNameForTeam(this.getTeam(), this.getName()));
        return iformattabletextcomponent;
    }


    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("left", ticksLeft);
        writeOwner(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
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
        }else return false;
    }

}
