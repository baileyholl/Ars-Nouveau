package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class EntityDummy extends CreatureEntity implements ISummon {
    private NetworkPlayerInfo playerInfo;
    public int ticksLeft;
    private static final DataParameter<Optional<UUID>> OWNER_UUID = EntityDataManager.defineId(EntityDummy.class, DataSerializers.OPTIONAL_UUID);

    public EntityDummy(EntityType<? extends CreatureEntity> p_i48577_1_, World p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

    public EntityDummy(World world){
        super(ModEntities.ENTITY_DUMMY, world);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(0, new SwimGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.of(Util.NIL_UUID));
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
        if(!level.isClientSide){
            if(level.getGameTime() % 10 == 0 && level.getPlayerByUUID(getOwnerID()) == null){
                ParticleUtil.spawnPoof((ServerWorld) level, blockPosition());
                this.remove();
                onSummonDeath(level, null, false);
                return;
            }

            ticksLeft--;
            if(ticksLeft <= 0) {
                ParticleUtil.spawnPoof((ServerWorld) level, blockPosition());
                this.remove();
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
    public ItemStack getItemBySlot(EquipmentSlotType p_184582_1_) {
        if(!level.isClientSide)
            return ItemStack.EMPTY;
        return level.getPlayerByUUID(getOwnerID()) != null ? level.getPlayerByUUID(getOwnerID()).getItemBySlot(p_184582_1_) : ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) { }

    public ResourceLocation getSkinTextureLocation() {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(getOwnerID()) : networkplayerinfo.getSkinLocation();
    }

    @Nullable
    protected NetworkPlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(getOwnerID());
        }

        return this.playerInfo;
    }
    public ITextComponent getName() {
        return this.level.getPlayerByUUID(getOwnerID()) == null ? new StringTextComponent("") : this.level.getPlayerByUUID(getOwnerID()).getName();
    }
    public ITextComponent getDisplayName() {
        IFormattableTextComponent iformattabletextcomponent = new StringTextComponent("");
        iformattabletextcomponent = iformattabletextcomponent.append(ScorePlayerTeam.formatNameForTeam(this.getTeam(), this.getName()));
        return iformattabletextcomponent;
    }


    @Override
    public HandSide getMainArm() {
        return HandSide.RIGHT;
    }


    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("left", ticksLeft);
        writeOwner(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        this.ticksLeft = tag.getInt("left");
        if(getOwnerID() != null)
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
    public UUID getOwnerID() {
        return !this.getEntityData().get(OWNER_UUID).isPresent() ? this.getUUID() : this.getEntityData().get(OWNER_UUID).get();
    }

    @Override
    public void setOwnerID(UUID uuid) {
        this.getEntityData().set(OWNER_UUID, Optional.ofNullable(uuid));
    }
}
