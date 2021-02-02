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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class EntityWhelp extends FlyingEntity implements IPickupResponder, IPlaceBlockResponder, IDispellable, ITooltipProvider, IWandable, IInteractResponder {

    public static final DataParameter<String> SPELL_STRING = EntityDataManager.createKey(EntityWhelp.class, DataSerializers.STRING);
    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.createKey(EntityWhelp.class, DataSerializers.ITEMSTACK);
    public static final DataParameter<Boolean> STRICT_MODE = EntityDataManager.createKey(EntityWhelp.class, DataSerializers.BOOLEAN);


    public BlockPos crystalPos;
    public int ticksSinceLastSpell;
    public List<AbstractSpellPart> spellRecipe;

    @Override
    public boolean canDespawn(double p_213397_1_) {
        return false;
    }

    protected EntityWhelp(EntityType<? extends FlyingEntity> p_i48568_1_, World p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
        this.moveController =  new FlyingMovementController(this, 10, true);
    }
    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 0;
    }

    public EntityWhelp setRecipe(ArrayList<AbstractSpellPart> recipe){
        this.spellRecipe = recipe;
        return this;
    }

    public EntityWhelp(World p_i50190_2_) {
        super(ModEntities.ENTITY_WHELP_TYPE, p_i50190_2_);
        this.moveController = new FlyingMovementController(this, 10, true);
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        if(world.isRemote || hand != Hand.MAIN_HAND)
            return ActionResultType.SUCCESS;

        ItemStack stack = player.getHeldItem(hand);

        if(stack.getItem() instanceof DominionWand)
            return ActionResultType.FAIL;

        if(stack != ItemStack.EMPTY && stack.getItem() instanceof SpellParchment){
            List<AbstractSpellPart> spellParts = SpellParchment.getSpellRecipe(stack);
            if(new EntitySpellResolver(spellParts, new SpellContext(spellParts, this)).canCast(this)) {
                this.spellRecipe = SpellParchment.getSpellRecipe(stack);
                setRecipeString(SpellRecipeUtil.serializeForNBT(spellRecipe));
                player.sendMessage(new StringTextComponent("Spell set."), Util.DUMMY_UUID);
                return ActionResultType.SUCCESS;
            } else{
                player.sendMessage(new StringTextComponent("A whelp cannot cast an invalid spell."), Util.DUMMY_UUID);
                return ActionResultType.SUCCESS;
            }
        }else if(stack == ItemStack.EMPTY){
            if(spellRecipe == null || spellRecipe.size() == 0){
                player.sendMessage(new StringTextComponent("Give this whelp a spell by giving it some inscribed Spell Parchment. "), Util.DUMMY_UUID);
            }else
                player.sendMessage(new StringTextComponent("This whelp is casting " + SpellRecipeUtil.getDisplayString(spellRecipe)), Util.DUMMY_UUID);
            return ActionResultType.SUCCESS;
        }

        if(!stack.isEmpty()){
            setHeldStack(new ItemStack(stack.getItem()));
            player.sendMessage(new StringTextComponent("This whelp will use " + stack.getItem().getDisplayName(stack).getString() +  " in spells if this item is in a Summoning Crystal chest."), Util.DUMMY_UUID);
        }
        return super.func_230254_b_(player,  hand);

    }

    @Override
    public void onWanded(PlayerEntity playerEntity) {
        this.dataManager.set(STRICT_MODE, !this.dataManager.get(STRICT_MODE));
        PortUtil.sendMessage(playerEntity, new TranslationTextComponent("ars_nouveau.whelp.strict_mode", this.dataManager.get(STRICT_MODE)));
    }

    public EntityWhelp(World world, BlockPos crystalPos){
        this(world);
        this.crystalPos = crystalPos;
    }

    @Override
    public void tick() {
        super.tick();
        if(world == null || this.dead || crystalPos == null)
            return;
        ticksSinceLastSpell += 1;

        if(world.getGameTime() % 20 == 0) {
            if (!(world.getTileEntity(crystalPos) instanceof SummoningCrystalTile)) {
                if (!world.isRemote) {
                    this.attackEntityFrom(DamageSource.causePlayerDamage(FakePlayerFactory.getMinecraft((ServerWorld) world)), 99);
                }
                if (world.isRemote) {

                    for (int i = 0; i < 2; i++) {
                        double d0 = getPosX(); //+ world.rand.nextFloat();
                        double d1 = getPosY();//+ world.rand.nextFloat() ;
                        double d2 = getPosZ(); //+ world.rand.nextFloat();

                        world.addParticle(ParticleTypes.ENCHANTED_HIT, d0, d1, d2, 0.0, 0.0, 0.0);
                    }
                }
            }
        }
    }

    @Override
    protected PathNavigator createNavigator(World world) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanSwim(true);
        flyingpathnavigator.setCanEnterDoors(true);
        return flyingpathnavigator;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(6, new PerformTaskGoal(this));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    public boolean canPerformAnotherTask(){
        return ticksSinceLastSpell > 60 && new EntitySpellResolver(spellRecipe, new SpellContext(spellRecipe, this)).canCast(this);
    }

    public @Nullable BlockPos getTaskLoc(){
        return world.getTileEntity(crystalPos) instanceof SummoningCrystalTile ? ((SummoningCrystalTile) world.getTileEntity(crystalPos)).getNextTaskLoc(spellRecipe, this) : null;
    }

    public void castSpell(BlockPos target){
        if(world.isRemote || !(world.getTileEntity(crystalPos) instanceof SummoningCrystalTile))
            return;

        if(((SummoningCrystalTile) world.getTileEntity(crystalPos)).removeManaAround(spellRecipe)){
            EntitySpellResolver resolver = new EntitySpellResolver(this.spellRecipe, new SpellContext(spellRecipe, this));
            resolver.onCastOnBlock(new BlockRayTraceResult(new Vector3d(target.getX(), target.getY(), target.getZ()), Direction.UP,target, false ), this);
        }
        this.ticksSinceLastSpell = 0;
    }

    public boolean enoughManaForTask(){
        if(!(world.getTileEntity(crystalPos) instanceof SummoningCrystalTile) || spellRecipe == null || spellRecipe.isEmpty())
            return false;
        return ((SummoningCrystalTile) world.getTileEntity(crystalPos)).enoughMana(spellRecipe);
    }

    protected void updateAITasks() {
        super.updateAITasks();
    }

    @Override
    public void onDeath(DamageSource source) {
        if(!world.isRemote){
            ItemStack stack = new ItemStack(ItemsRegistry.whelpCharm);
            world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), stack));
        }

        super.onDeath(source);
    }

    @Override
    public ItemStack onPickup(ItemStack stack) {
        SummoningCrystalTile tile = world.getTileEntity(crystalPos) instanceof SummoningCrystalTile ? (SummoningCrystalTile) world.getTileEntity(crystalPos) : null;
        return tile == null ? stack : tile.insertItem(stack);
    }

    @Override
    public ItemStack onPlaceBlock() {
        ItemStack heldStack = getHeldStack();
        if(heldStack == null )
            return  ItemStack.EMPTY;
        SummoningCrystalTile tile = world.getTileEntity(crystalPos) instanceof SummoningCrystalTile ? (SummoningCrystalTile) world.getTileEntity(crystalPos) : null;
        return tile == null ? heldStack : tile.getItem(heldStack.getItem());
    }

    @Override
    public List<String> getTooltip() {
        List<String> list = new ArrayList<>();
        List<AbstractSpellPart> spellParts = SpellRecipeUtil.getSpellsFromTagString(this.getRecipeString());
        String spellString = spellParts.size() > 4 ? SpellRecipeUtil.getDisplayString(spellParts.subList(0, 4)) + "..." :SpellRecipeUtil.getDisplayString(spellParts);
        String itemString = this.getHeldStack() == ItemStack.EMPTY ? "Nothing." : this.getHeldStack().getDisplayName().getString();
        String itemAction = this.getHeldStack().getItem() instanceof BlockItem ? "Placing: " : "Using: ";
        list.add("Casting: " + spellString);
        list.add(itemAction + itemString);
        list.add("Strict mode: " + this.dataManager.get(STRICT_MODE));
        return list;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_WHELP_TYPE;
    }


    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
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
            getHeldStack().write(itemTag);
            tag.put("held", itemTag);
        }

        tag.putBoolean("strict", this.dataManager.get(STRICT_MODE));
    }

    public String getRecipeString(){
        return this.dataManager.get(SPELL_STRING);
    }

    public void setRecipeString(String recipeString){
        this.dataManager.set(SPELL_STRING, recipeString);
    }

    public ItemStack getHeldStack(){
        return this.dataManager.get(HELD_ITEM);
    }

    public void setHeldStack(ItemStack stack){
        this.dataManager.set(HELD_ITEM,stack);
    }


    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(this.removed)
            return false;

        if(!world.isRemote){
            ItemStack stack = new ItemStack(ItemsRegistry.whelpCharm);
            world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), stack));
            ParticleUtil.spawnPoof((ServerWorld)world, getPosition());
            this.remove();
        }
        return true;
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        if(tag.contains("summoner_x"))
            crystalPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        spellRecipe = SpellRecipeUtil.getSpellsFromTagString(tag.getString("spell"));
        ticksSinceLastSpell = tag.getInt("last_spell");
        if(tag.contains("held"))
            setHeldStack(ItemStack.read((CompoundNBT)tag.get("held")));


        setRecipeString(SpellRecipeUtil.serializeForNBT(spellRecipe));
        this.dataManager.set(STRICT_MODE, tag.getBoolean("strict"));
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick() {
        super.livingTick();
    }

    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .createMutableAttribute(Attributes.MAX_HEALTH, 6.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(HELD_ITEM, ItemStack.EMPTY);
        this.dataManager.register(SPELL_STRING, "");
        this.dataManager.register(STRICT_MODE, true);
    }

    @Override
    public ItemStack getHeldItem() {
        if(crystalPos != null && world.getTileEntity(crystalPos) instanceof SummoningCrystalTile){
            SummoningCrystalTile tile = (SummoningCrystalTile) world.getTileEntity(crystalPos);
            for(IItemHandler inv : BlockUtil.getAdjacentInventories(world, tile.getPos())){
                for(int i = 0; i < inv.getSlots(); i++){
                    if(inv.getStackInSlot(i).isItemEqual(this.dataManager.get(HELD_ITEM)))
                        return inv.getStackInSlot(i).split(1);
                }
            }

        }
        return ItemStack.EMPTY;
    }

    @Override
    public List<IItemHandler> getInventory() {
        return BlockUtil.getAdjacentInventories(world, crystalPos);
    }
}
