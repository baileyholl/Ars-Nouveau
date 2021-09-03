package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.BookwyrmLecternTile;
import com.hollingsworth.arsnouveau.common.entity.goal.whelp.PerformTaskGoal;
import com.hollingsworth.arsnouveau.common.items.DominionWand;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.FakePlayerFactory;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EntityBookwyrm extends FlyingEntity implements IPickupResponder, IPlaceBlockResponder, IDispellable, ITooltipProvider, IWandable, IInteractResponder, IAnimatable {

    public static final DataParameter<String> SPELL_STRING = EntityDataManager.defineId(EntityBookwyrm.class, DataSerializers.STRING);
    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.defineId(EntityBookwyrm.class, DataSerializers.ITEM_STACK);
    public static final DataParameter<Boolean> STRICT_MODE = EntityDataManager.defineId(EntityBookwyrm.class, DataSerializers.BOOLEAN);
    public static final DataParameter<String> COLOR = EntityDataManager.defineId(EntityBookwyrm.class, DataSerializers.STRING);

    public BlockPos lecternPos;
    public int ticksSinceLastSpell;
    public Spell spellRecipe;
    private int backoffTicks;

    protected EntityBookwyrm(EntityType<? extends FlyingEntity> p_i48568_1_, World p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
        this.moveControl =  new FlyingMovementController(this, 10, true);
    }


    public EntityBookwyrm setRecipe(Spell spell){
        this.spellRecipe = spell;
        return this;
    }

    public EntityBookwyrm(World p_i50190_2_) {
        super(ModEntities.ENTITY_BOOKWYRM_TYPE, p_i50190_2_);
        this.moveControl = new FlyingMovementController(this, 10, true);
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if(level.isClientSide || hand != Hand.MAIN_HAND)
            return ActionResultType.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        if (player.getMainHandItem().getItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if(color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(COLORS).contains(color.getName()))
                return ActionResultType.SUCCESS;
            this.entityData.set(COLOR, color.getName());
            player.getMainHandItem().shrink(1);
            return ActionResultType.SUCCESS;
        }

        if(stack.getItem() instanceof DominionWand)
            return ActionResultType.FAIL;

        if(stack.getItem() instanceof SpellParchment){
            List<AbstractSpellPart> spellParts = SpellParchment.getSpellRecipe(stack);
            if(new EntitySpellResolver(new SpellContext(spellParts, this)).canCast(this)) {
                this.spellRecipe = new Spell(SpellParchment.getSpellRecipe(stack));
                setRecipeString(spellRecipe.serialize());
                player.sendMessage(new TranslationTextComponent("ars_nouveau.whelp.spell_set"), Util.NIL_UUID);
                return ActionResultType.SUCCESS;
            } else{
                player.sendMessage(new TranslationTextComponent("ars_nouveau.whelp.invalid"), Util.NIL_UUID);
                return ActionResultType.SUCCESS;
            }
        }else if(stack.isEmpty()){
            if(spellRecipe == null || spellRecipe.recipe.size() == 0){
                player.sendMessage(new TranslationTextComponent("ars_nouveau.whelp.desc"), Util.NIL_UUID);
            }else
                player.sendMessage(new TranslationTextComponent("ars_nouveau.whelp.casting", spellRecipe.getDisplayString()), Util.NIL_UUID);
            return ActionResultType.SUCCESS;
        }

        if(!stack.isEmpty()){
            setHeldStack(new ItemStack(stack.getItem()));
            player.sendMessage(new TranslationTextComponent("ars_nouveau.whelp.spell_item", stack.getItem().getName(stack).getString()), Util.NIL_UUID);
        }
        return super.mobInteract(player,  hand);

    }

    @Override
    public void onWanded(PlayerEntity playerEntity) {
        this.entityData.set(STRICT_MODE, !this.entityData.get(STRICT_MODE));
        PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.whelp.strict_mode", this.entityData.get(STRICT_MODE)));
    }

    public EntityBookwyrm(World world, BlockPos lecternPos){
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
                    this.hurt(DamageSource.playerAttack(FakePlayerFactory.getMinecraft((ServerWorld) level)), 99);
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
    protected PathNavigator createNavigation(World world) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(true);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(6, new PerformTaskGoal(this));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
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
            resolver.onCastOnBlock(new BlockRayTraceResult(new Vector3d(target.getX(), target.getY(), target.getZ()), Direction.UP,target, false ), this);
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
    public List<String> getTooltip() {
        List<String> list = new ArrayList<>();
        Spell spellParts = Spell.deserialize(this.getRecipeString());
        String spellString = spellParts.getDisplayString();
        String itemString = this.getHeldStack() == ItemStack.EMPTY ? new TranslationTextComponent("ars_nouveau.whelp.no_item").getString() : this.getHeldStack().getHoverName().getString();
        String itemAction = this.getHeldStack().getItem() instanceof BlockItem ? 
        		new TranslationTextComponent("ars_nouveau.whelp.placing").getString() : 
        		new TranslationTextComponent("ars_nouveau.whelp.using").getString();
        list.add(new TranslationTextComponent("ars_nouveau.whelp.spell").getString() + spellString);
        list.add(itemAction + itemString);
        list.add(new TranslationTextComponent("ars_nouveau.whelp.strict").getString() + new TranslationTextComponent("ars_nouveau." + this.entityData.get(STRICT_MODE)).getString() );
        return list;
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(this.removed)
            return false;

        if(!level.isClientSide){
            ItemStack stack = new ItemStack(ItemsRegistry.BOOKWYRM_CHARM);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerWorld)level, blockPosition());
            this.remove();
        }
        return true;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_BOOKWYRM_TYPE;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
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
            CompoundNBT itemTag = new CompoundNBT();
            getHeldStack().save(itemTag);
            tag.put("held", itemTag);
        }
        tag.putInt("backoff", backoffTicks);
        tag.putBoolean("strict", this.entityData.get(STRICT_MODE));
        tag.putString("color", this.entityData.get(COLOR));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("summoner_x"))
            lecternPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        spellRecipe = Spell.deserialize(tag.getString("spell"));
        ticksSinceLastSpell = tag.getInt("last_spell");
        if(tag.contains("held"))
            setHeldStack(ItemStack.of((CompoundNBT)tag.get("held")));


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

    public static String[] COLORS = {"purple", "green", "blue", "black", "red", "white"};

    @Override
    protected int getExperienceReward(PlayerEntity player) {
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

    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.createMobAttributes().add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
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
}
