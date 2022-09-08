package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.client.IVariantColorProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.IInteractResponder;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.BookwyrmLecternTile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EntityBookwyrm extends FlyingMob implements IDispellable, ITooltipProvider, IWandable, IInteractResponder, IAnimatable, IVariantColorProvider<EntityBookwyrm> {

    public static final EntityDataAccessor<String> SPELL_STRING = SynchedEntityData.defineId(EntityBookwyrm.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<ItemStack> HELD_ITEM = SynchedEntityData.defineId(EntityBookwyrm.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<Boolean> STRICT_MODE = SynchedEntityData.defineId(EntityBookwyrm.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> COLOR = SynchedEntityData.defineId(EntityBookwyrm.class, EntityDataSerializers.STRING);

    public BlockPos lecternPos;
    public int ticksSinceLastSpell;
    public Spell spellRecipe;
    private int backoffTicks;

    protected EntityBookwyrm(EntityType<? extends FlyingMob> p_i48568_1_, Level p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }


    public EntityBookwyrm setRecipe(Spell spell) {
        this.spellRecipe = spell;
        return this;
    }

    public EntityBookwyrm(Level p_i50190_2_) {
        super(ModEntities.ENTITY_BOOKWYRM_TYPE.get(), p_i50190_2_);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }


    @Override
    public void onWanded(Player playerEntity) {
        this.entityData.set(STRICT_MODE, !this.entityData.get(STRICT_MODE));
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.bookwyrm.strict_mode", this.entityData.get(STRICT_MODE)));
    }

    public EntityBookwyrm(Level world, BlockPos lecternPos) {
        this(world);
        this.lecternPos = lecternPos;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || this.dead || lecternPos == null)
            return;
        ticksSinceLastSpell += 1;
        if (!level.isClientSide) {
            if (backoffTicks >= 0)
                backoffTicks--;
        }

        if (level.getGameTime() % 20 == 0) {
            if (!(level.getBlockEntity(lecternPos) instanceof BookwyrmLecternTile)) {
                if (!level.isClientSide) {
                    this.hurt(DamageSource.playerAttack(ANFakePlayer.getPlayer((ServerLevel) level)), 99);
                }
            }
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float p_70097_2_) {
        if (source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.SWEET_BERRY_BUSH || source == DamageSource.CACTUS)
            return false;
        return super.hurt(source, p_70097_2_);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }



    @Override
    public void getTooltip(List<Component> tooltip) {
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if (this.isRemoved())
            return false;

        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.BOOKWYRM_CHARM.get());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerLevel) level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return true;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_BOOKWYRM_TYPE.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (lecternPos != null) {
            tag.putInt("summoner_x", lecternPos.getX());
            tag.putInt("summoner_y", lecternPos.getY());
            tag.putInt("summoner_z", lecternPos.getZ());
        }
        tag.putInt("last_spell", ticksSinceLastSpell);

        if (!getHeldStack().isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            getHeldStack().save(itemTag);
            tag.put("held", itemTag);
        }
        tag.putInt("backoff", backoffTicks);
        tag.putBoolean("strict", this.entityData.get(STRICT_MODE));
        tag.putString("color", this.entityData.get(COLOR));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("summoner_x"))
            lecternPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        ticksSinceLastSpell = tag.getInt("last_spell");
        if (tag.contains("held"))
            setHeldStack(ItemStack.of((CompoundTag) tag.get("held")));

        this.entityData.set(STRICT_MODE, tag.getBoolean("strict"));
        this.backoffTicks = tag.getInt("backoff");
        if (tag.contains("color"))
            this.entityData.set(COLOR, tag.getString("color"));

    }


    @Override
    public ItemStack getHeldItem() {
        if (lecternPos != null && level.getBlockEntity(lecternPos) instanceof BookwyrmLecternTile tile) {
            for (IItemHandler inv : BlockUtil.getAdjacentInventories(level, tile.getBlockPos())) {
                for (int i = 0; i < inv.getSlots(); i++) {
                    if (inv.getStackInSlot(i).sameItem(this.entityData.get(HELD_ITEM)))
                        return inv.getStackInSlot(i).split(1);
                }
            }

        }
        return ItemStack.EMPTY;
    }

    public @Nullable BookwyrmLecternTile getTile() {
        return lecternPos == null || !(level.getBlockEntity(lecternPos) instanceof BookwyrmLecternTile) ? null : (BookwyrmLecternTile) level.getBlockEntity(lecternPos);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "walkController", 1, this::idle));
    }

    public PlayState idle(AnimationEvent event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        return PlayState.CONTINUE;
    }

    AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public int getExperienceReward() {
        return 0;
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    public @Nonnull ItemStack getHeldStack() {
        return this.entityData.get(HELD_ITEM);
    }

    public void setHeldStack(ItemStack stack) {
        this.entityData.set(HELD_ITEM, stack);
    }

    @Override
    public void die(DamageSource source) {
        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(ItemsRegistry.BOOKWYRM_CHARM.get());
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        }

        super.die(source);
    }

    public static AttributeSupplier.Builder attributes() {
        return Mob.createMobAttributes().add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HELD_ITEM, ItemStack.EMPTY);
        this.entityData.define(SPELL_STRING, "");
        this.entityData.define(STRICT_MODE, true);
        this.entityData.define(COLOR, "blue");
    }

    public static String[] COLORS = {"purple", "green", "blue", "black", "red", "white"};

    @Override
    public ResourceLocation getTexture(EntityBookwyrm entity) {
        String color = getColor().toLowerCase();
        if (color.isEmpty())
            color = "blue";
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/book_wyrm_" + color + ".png");
    }

    @Override
    public String getColor() {
        return getEntityData().get(EntityBookwyrm.COLOR);
    }

    @Override
    public void setColor(String color) {
        getEntityData().set(EntityBookwyrm.COLOR, color);
    }
}
