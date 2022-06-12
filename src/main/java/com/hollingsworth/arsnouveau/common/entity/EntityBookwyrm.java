package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.client.IVariantTextureProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.BookwyrmLecternTile;
import com.hollingsworth.arsnouveau.common.entity.goal.whelp.PerformTaskGoal;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
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
import java.util.Arrays;
import java.util.List;

public class EntityBookwyrm extends FlyingMob implements IPickupResponder, IPlaceBlockResponder, IDispellable, ITooltipProvider, IWandable, IInteractResponder, IAnimatable, IVariantTextureProvider {

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
        this.moveControl =  new FlyingMoveControl(this, 10, true);
    }


    public EntityBookwyrm setRecipe(Spell spell){
        this.spellRecipe = spell;
        return this;
    }

    public EntityBookwyrm(Level p_i50190_2_) {
        super(ModEntities.ENTITY_BOOKWYRM_TYPE, p_i50190_2_);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(level.isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        if (player.getMainHandItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if(color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(COLORS).contains(color.getName()))
                return InteractionResult.SUCCESS;
            this.entityData.set(COLOR, color.getName());
            player.getMainHandItem().shrink(1);
            return InteractionResult.SUCCESS;
        }

        if(stack.getItem() instanceof DominionWand)
            return InteractionResult.FAIL;

        if(stack.getItem() instanceof SpellParchment){
            Spell spell =  CasterUtil.getCaster(stack).getSpell();
            if(new EntitySpellResolver(new SpellContext(spell, this)).canCast(this)) {
                this.spellRecipe = spell;
                setRecipeString(spellRecipe.serialize());
                player.sendMessage(Component.translatable("ars_nouveau.bookwyrm.spell_set"), Util.NIL_UUID);
            } else{
                player.sendMessage(Component.translatable("ars_nouveau.bookwyrm.invalid"), Util.NIL_UUID);
            }
            return InteractionResult.SUCCESS;
        }else if(stack.isEmpty()){
            if(spellRecipe == null || spellRecipe.recipe.size() == 0){
                player.sendMessage(Component.translatable("ars_nouveau.bookwyrm.desc"), Util.NIL_UUID);
            }else
                player.sendMessage(Component.translatable("ars_nouveau.bookwyrm.casting", spellRecipe.getDisplayString()), Util.NIL_UUID);
            return InteractionResult.SUCCESS;
        }

        if(!stack.isEmpty()){
            setHeldStack(new ItemStack(stack.getItem()));
            player.sendMessage(Component.translatable("ars_nouveau.bookwyrm.spell_item", stack.getItem().getName(stack).getString()), Util.NIL_UUID);
        }
        return super.mobInteract(player,  hand);

    }

    @Override
    public void onWanded(Player playerEntity) {
        this.entityData.set(STRICT_MODE, !this.entityData.get(STRICT_MODE));
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.bookwyrm.strict_mode", this.entityData.get(STRICT_MODE)));
    }

    public EntityBookwyrm(Level world, BlockPos lecternPos){
        this(world);
        this.lecternPos = lecternPos;
    }

    @Override
    public void tick() {
        super.tick();
        if(level == null || this.dead || lecternPos == null)
            return;
        ticksSinceLastSpell += 1;
        if(!level.isClientSide){
            if(backoffTicks >= 0)
                backoffTicks--;
        }

        if(level.getGameTime() % 20 == 0) {
            if (!(level.getBlockEntity(lecternPos) instanceof BookwyrmLecternTile)) {
                if (!level.isClientSide) {
                    this.hurt(DamageSource.playerAttack(ANFakePlayer.getPlayer((ServerLevel) level)), 99);
                }
            }
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float p_70097_2_) {
        if(source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.SWEET_BERRY_BUSH || source == DamageSource.CACTUS)
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
        this.goalSelector.addGoal(6, new PerformTaskGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public boolean canPerformAnotherTask(){
        return ticksSinceLastSpell > 60 && new EntitySpellResolver(new SpellContext(spellRecipe, this)).canCast(this);
    }

    public @Nullable BlockPos getTaskLoc(){
        BlockPos task = getTile() != null ? getTile().getNextTaskLoc(spellRecipe, this) : null;
        if(task == null)
            ticksSinceLastSpell = 0;
        return task;
    }

    public void castSpell(BlockPos target){
        if(level.isClientSide || !(level.getBlockEntity(lecternPos) instanceof BookwyrmLecternTile))
            return;

        if(((BookwyrmLecternTile) level.getBlockEntity(lecternPos)).removeManaAround(spellRecipe)){
            EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(spellRecipe, this));
            resolver.onCastOnBlock(new BlockHitResult(new Vec3(target.getX(), target.getY(), target.getZ()), Direction.UP,target, false ), this);
        }
        this.ticksSinceLastSpell = 0;
    }

    public boolean enoughManaForTask(){
        if(!(level.getBlockEntity(lecternPos) instanceof BookwyrmLecternTile) || spellRecipe == null || spellRecipe.isEmpty())
            return false;
        return ((BookwyrmLecternTile) level.getBlockEntity(lecternPos)).enoughMana(spellRecipe);
    }


    @Override
    public @Nonnull ItemStack onPickup(ItemStack stack) {
        BookwyrmLecternTile tile = getTile();
        return tile == null ? stack : tile.insertItem(stack);
    }

    @Override
    public ItemStack onPlaceBlock() {
        ItemStack heldStack = getHeldStack();
        if(heldStack.isEmpty())
            return  ItemStack.EMPTY;
        BookwyrmLecternTile tile = getTile();
        return tile == null ? heldStack : tile.getItem(heldStack.getItem());
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        Spell spellParts = Spell.deserialize(this.getRecipeString());
        String spellString = spellParts.getDisplayString();
        String itemString = this.getHeldStack() == ItemStack.EMPTY ? Component.translatable("ars_nouveau.bookwyrm.no_item").getString() : this.getHeldStack().getHoverName().getString();
        String itemAction = this.getHeldStack().getItem() instanceof BlockItem ? 
        		Component.translatable("ars_nouveau.bookwyrm.placing").getString() :
        		Component.translatable("ars_nouveau.bookwyrm.using").getString();
        tooltip.add(Component.literal(Component.translatable("ars_nouveau.bookwyrm.spell").getString() + spellString));
        tooltip.add(Component.literal(itemAction + itemString));
        tooltip.add(Component.literal(Component.translatable("ars_nouveau.bookwyrm.strict").getString() +
                Component.translatable("ars_nouveau." + this.entityData.get(STRICT_MODE)).getString() ));
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(this.isRemoved())
            return false;

        if(!level.isClientSide){
            ItemStack stack = new ItemStack(ItemsRegistry.BOOKWYRM_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerLevel)level, blockPosition());
            this.remove(RemovalReason.DISCARDED);
        }
        return true;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_BOOKWYRM_TYPE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if(lecternPos != null){
            tag.putInt("summoner_x", lecternPos.getX());
            tag.putInt("summoner_y", lecternPos.getY());
            tag.putInt("summoner_z", lecternPos.getZ());
        }
        tag.putInt("last_spell", ticksSinceLastSpell);
        if(spellRecipe != null){
            tag.putString("spell", spellRecipe.serialize());
        }
        if(!getHeldStack().isEmpty()) {
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
        if(tag.contains("summoner_x"))
            lecternPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        spellRecipe = Spell.deserialize(tag.getString("spell"));
        ticksSinceLastSpell = tag.getInt("last_spell");
        if(tag.contains("held"))
            setHeldStack(ItemStack.of((CompoundTag)tag.get("held")));


        setRecipeString(spellRecipe.serialize());
        this.entityData.set(STRICT_MODE, tag.getBoolean("strict"));
        this.backoffTicks = tag.getInt("backoff");
        if (tag.contains("color"))
            this.entityData.set(COLOR, tag.getString("color"));

    }


    @Override
    public ItemStack getHeldItem() {
        if(lecternPos != null && level.getBlockEntity(lecternPos) instanceof BookwyrmLecternTile){
            BookwyrmLecternTile tile = (BookwyrmLecternTile) level.getBlockEntity(lecternPos);
            for(IItemHandler inv : BlockUtil.getAdjacentInventories(level, tile.getBlockPos())){
                for(int i = 0; i < inv.getSlots(); i++){
                    if(inv.getStackInSlot(i).sameItem(this.entityData.get(HELD_ITEM)))
                        return inv.getStackInSlot(i).split(1);
                }
            }

        }
        return ItemStack.EMPTY;
    }

    @Override
    public List<IItemHandler> getInventory() {
        return BlockUtil.getAdjacentInventories(level, lecternPos);
    }

    public @Nullable BookwyrmLecternTile getTile(){
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
    protected int getExperienceReward(Player player) {
        return 0;
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }


    public String getRecipeString(){
        return this.entityData.get(SPELL_STRING);
    }

    public void setRecipeString(String recipeString){
        this.entityData.set(SPELL_STRING, recipeString);
    }

    public @Nonnull ItemStack getHeldStack(){
        return this.entityData.get(HELD_ITEM);
    }

    public void setHeldStack(ItemStack stack){
        this.entityData.set(HELD_ITEM,stack);
    }


    @Override
    public void die(DamageSource source) {
        if(!level.isClientSide){
            ItemStack stack = new ItemStack(ItemsRegistry.BOOKWYRM_CHARM);
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
    public ResourceLocation getTexture(LivingEntity entity) {
        String color = getEntityData().get(EntityBookwyrm.COLOR).toLowerCase();
        if(color.isEmpty())
            color = "blue";
        return new ResourceLocation(ArsNouveau.MODID, "textures/entity/book_wyrm_" + color +".png");
    }
}
