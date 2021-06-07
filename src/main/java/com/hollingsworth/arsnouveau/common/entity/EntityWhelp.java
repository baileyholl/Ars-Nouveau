package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrystalTile;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class EntityWhelp extends FlyingEntity implements IPickupResponder, IPlaceBlockResponder, IDispellable, ITooltipProvider, IWandable, IInteractResponder {

    public static final DataParameter<String> SPELL_STRING = EntityDataManager.defineId(EntityWhelp.class, DataSerializers.STRING);
    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.defineId(EntityWhelp.class, DataSerializers.ITEM_STACK);
    public static final DataParameter<Boolean> STRICT_MODE = EntityDataManager.defineId(EntityWhelp.class, DataSerializers.BOOLEAN);


    public BlockPos crystalPos;
    public int ticksSinceLastSpell;
    public List<AbstractSpellPart> spellRecipe;
    private int backoffTicks;
    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    protected EntityWhelp(EntityType<? extends FlyingEntity> p_i48568_1_, World p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
        this.moveControl =  new FlyingMovementController(this, 10, true);
    }
    @Override
    protected int getExperienceReward(PlayerEntity player) {
        return 0;
    }

    public EntityWhelp setRecipe(ArrayList<AbstractSpellPart> recipe){
        this.spellRecipe = recipe;
        return this;
    }

    public EntityWhelp(World p_i50190_2_) {
        super(ModEntities.ENTITY_WHELP_TYPE, p_i50190_2_);
        this.moveControl = new FlyingMovementController(this, 10, true);
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if(level.isClientSide || hand != Hand.MAIN_HAND)
            return ActionResultType.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        if(stack.getItem() instanceof DominionWand)
            return ActionResultType.FAIL;

        if(stack != ItemStack.EMPTY && stack.getItem() instanceof SpellParchment){
            List<AbstractSpellPart> spellParts = SpellParchment.getSpellRecipe(stack);
            if(new EntitySpellResolver(spellParts, new SpellContext(spellParts, this)).canCast(this)) {
                this.spellRecipe = SpellParchment.getSpellRecipe(stack);
                setRecipeString(SpellRecipeUtil.serializeForNBT(spellRecipe));
                player.sendMessage(new TranslationTextComponent("ars_nouveau.whelp.spell_set"), Util.NIL_UUID);
                return ActionResultType.SUCCESS;
            } else{
                player.sendMessage(new TranslationTextComponent("ars_nouveau.whelp.invalid"), Util.NIL_UUID);
                return ActionResultType.SUCCESS;
            }
        }else if(stack == ItemStack.EMPTY){
            if(spellRecipe == null || spellRecipe.size() == 0){
                player.sendMessage(new TranslationTextComponent("ars_nouveau.whelp.desc"), Util.NIL_UUID);
            }else
                player.sendMessage(new TranslationTextComponent("ars_nouveau.whelp.casting", SpellRecipeUtil.getDisplayString(spellRecipe)), Util.NIL_UUID);
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

    public EntityWhelp(World world, BlockPos crystalPos){
        this(world);
        this.crystalPos = crystalPos;
    }

    @Override
    public void tick() {
        super.tick();
        if(level == null || this.dead || crystalPos == null)
            return;
        ticksSinceLastSpell += 1;
        if(!level.isClientSide){
            if(backoffTicks >= 0)
                backoffTicks--;
        }

        if(level.getGameTime() % 20 == 0) {
            if (!(level.getBlockEntity(crystalPos) instanceof SummoningCrystalTile)) {
                if (!level.isClientSide) {
                    this.hurt(DamageSource.playerAttack(FakePlayerFactory.getMinecraft((ServerWorld) level)), 99);
                }
                if (level.isClientSide) {

                    for (int i = 0; i < 2; i++) {
                        double d0 = getX(); //+ world.rand.nextFloat();
                        double d1 = getY();//+ world.rand.nextFloat() ;
                        double d2 = getZ(); //+ world.rand.nextFloat();

                        level.addParticle(ParticleTypes.ENCHANTED_HIT, d0, d1, d2, 0.0, 0.0, 0.0);
                    }
                }
            }
        }
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

    private boolean isBackedOff(){
        if(backoffTicks <= 0) {
            backoffTicks = 60;
            return false;
        }
        return true;
    }

    public boolean canPerformAnotherTask(){
        return  ticksSinceLastSpell > 60 && new EntitySpellResolver(spellRecipe, new SpellContext(spellRecipe, this)).canCast(this) && !isBackedOff();
    }

    public @Nullable BlockPos getTaskLoc(){
        return level.getBlockEntity(crystalPos) instanceof SummoningCrystalTile ? ((SummoningCrystalTile) level.getBlockEntity(crystalPos)).getNextTaskLoc(spellRecipe, this) : null;
    }

    public void castSpell(BlockPos target){
        if(level.isClientSide || !(level.getBlockEntity(crystalPos) instanceof SummoningCrystalTile))
            return;

        if(((SummoningCrystalTile) level.getBlockEntity(crystalPos)).removeManaAround(spellRecipe)){
            EntitySpellResolver resolver = new EntitySpellResolver(this.spellRecipe, new SpellContext(spellRecipe, this));
            resolver.onCastOnBlock(new BlockRayTraceResult(new Vector3d(target.getX(), target.getY(), target.getZ()), Direction.UP,target, false ), this);
        }
        this.ticksSinceLastSpell = 0;
    }

    public boolean enoughManaForTask(){
        if(!(level.getBlockEntity(crystalPos) instanceof SummoningCrystalTile) || spellRecipe == null || spellRecipe.isEmpty())
            return false;
        return ((SummoningCrystalTile) level.getBlockEntity(crystalPos)).enoughMana(spellRecipe);
    }

    @Override
    public void die(DamageSource source) {
        if(!level.isClientSide){
            ItemStack stack = new ItemStack(ItemsRegistry.whelpCharm);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        }

        super.die(source);
    }

    @Override
    public ItemStack onPickup(ItemStack stack) {
        SummoningCrystalTile tile = level.getBlockEntity(crystalPos) instanceof SummoningCrystalTile ? (SummoningCrystalTile) level.getBlockEntity(crystalPos) : null;
        return tile == null ? stack : tile.insertItem(stack);
    }

    @Override
    public ItemStack onPlaceBlock() {
        ItemStack heldStack = getHeldStack();
        if(heldStack == null )
            return  ItemStack.EMPTY;
        SummoningCrystalTile tile = level.getBlockEntity(crystalPos) instanceof SummoningCrystalTile ? (SummoningCrystalTile) level.getBlockEntity(crystalPos) : null;
        return tile == null ? heldStack : tile.getItem(heldStack.getItem());
    }

    @Override
    public List<String> getTooltip() {
        List<String> list = new ArrayList<>();
        List<AbstractSpellPart> spellParts = SpellRecipeUtil.getSpellsFromTagString(this.getRecipeString());
        String spellString = spellParts.size() > 4 ? SpellRecipeUtil.getDisplayString(spellParts.subList(0, 4)) + "..." :SpellRecipeUtil.getDisplayString(spellParts);
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
    public EntityType<?> getType() {
        return ModEntities.ENTITY_WHELP_TYPE;
    }


    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        if(crystalPos != null){
            tag.putInt("summoner_x", crystalPos.getX());
            tag.putInt("summoner_y", crystalPos.getY());
            tag.putInt("summoner_z", crystalPos.getZ());
        }
        tag.putInt("last_spell", ticksSinceLastSpell);
        if(spellRecipe != null){
            tag.putString("spell", SpellRecipeUtil.serializeForNBT(spellRecipe));
        }
        if(getHeldStack() != null) {
            CompoundNBT itemTag = new CompoundNBT();
            getHeldStack().save(itemTag);
            tag.put("held", itemTag);
        }
        tag.putInt("backoff", backoffTicks);
        tag.putBoolean("strict", this.entityData.get(STRICT_MODE));
    }

    public String getRecipeString(){
        return this.entityData.get(SPELL_STRING);
    }

    public void setRecipeString(String recipeString){
        this.entityData.set(SPELL_STRING, recipeString);
    }

    public ItemStack getHeldStack(){
        return this.entityData.get(HELD_ITEM);
    }

    public void setHeldStack(ItemStack stack){
        this.entityData.set(HELD_ITEM,stack);
    }


    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(this.removed)
            return false;

        if(!level.isClientSide){
            ItemStack stack = new ItemStack(ItemsRegistry.whelpCharm);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerWorld)level, blockPosition());
            this.remove();
        }
        return true;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("summoner_x"))
            crystalPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        spellRecipe = SpellRecipeUtil.getSpellsFromTagString(tag.getString("spell"));
        ticksSinceLastSpell = tag.getInt("last_spell");
        if(tag.contains("held"))
            setHeldStack(ItemStack.of((CompoundNBT)tag.get("held")));


        setRecipeString(SpellRecipeUtil.serializeForNBT(spellRecipe));
        this.entityData.set(STRICT_MODE, tag.getBoolean("strict"));
        this.backoffTicks = tag.getInt("backoff");
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void aiStep() {
        super.aiStep();
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
    }

    @Override
    public ItemStack getHeldItem() {
        if(crystalPos != null && level.getBlockEntity(crystalPos) instanceof SummoningCrystalTile){
            SummoningCrystalTile tile = (SummoningCrystalTile) level.getBlockEntity(crystalPos);
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
        return BlockUtil.getAdjacentInventories(level, crystalPos);
    }
}
