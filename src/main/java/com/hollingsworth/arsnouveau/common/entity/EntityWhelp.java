package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.spell.IPlaceBlockResponder;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrytalTile;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.w3c.dom.Attr;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;

public class EntityWhelp extends FlyingEntity implements IPickupResponder, IPlaceBlockResponder {
    public static final DataParameter<String> SPELL_STRING = EntityDataManager.createKey(EntityWhelp.class, DataSerializers.STRING);
    public static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.createKey(EntityWhelp.class, DataSerializers.ITEMSTACK);


    BlockPos crystalPos;
    int ticksSinceLastSpell;
    public ArrayList<AbstractSpellPart> spellRecipe;

    @Override
    public boolean canDespawn(double p_213397_1_) {
        return false;
    }

    protected EntityWhelp(EntityType<? extends FlyingEntity> p_i48568_1_, World p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
        this.moveController =  new FlyingMovementController(this, 10, true);

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
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
        if(world.isRemote)
            return ActionResultType.SUCCESS;
        ItemStack stack = player.getHeldItem(hand);


        if(stack != ItemStack.EMPTY && stack.getItem() instanceof SpellParchment){
            ArrayList<AbstractSpellPart> spellParts = SpellParchment.getSpellRecipe(stack);
            if(new EntitySpellResolver(spellParts).canCast(this)) {
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
        if(stack != ItemStack.EMPTY){
            setHeldStack(new ItemStack(stack.getItem()));
            player.sendMessage(new StringTextComponent("This whelp will use " + stack.getItem().getDisplayName(stack).getString() +  " in spells if this item is in a Summoning Crystal chest."), Util.DUMMY_UUID);
        }
        return super.applyPlayerInteraction(player, vec, hand);
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
        if(world.getGameTime() % 20 == 0){
            if(!(world.getTileEntity(crystalPos) instanceof SummoningCrytalTile)){
                if(!world.isRemote){
                    this.attackEntityFrom(DamageSource.causePlayerDamage(FakePlayerFactory.getMinecraft((ServerWorld)world)), 99);
                }
                if(world.isRemote){

                    for(int i =0; i < 2; i++){
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

    protected void registerGoals() {

//        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
//        this.goalSelector.addGoal(1, new LevitateGoal());
        this.goalSelector.addGoal(6, new PerformTaskGoal(this));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    public boolean canPerformAnotherTask(){
        return  ticksSinceLastSpell > 60 && new EntitySpellResolver(spellRecipe).canCast(this);
    }

    public @Nullable BlockPos getTaskLoc(){
        if(world.getTileEntity(crystalPos) instanceof SummoningCrytalTile){
            return ((SummoningCrytalTile) world.getTileEntity(crystalPos)).getNextTaskLoc();
        }
        return null;
    }

    public void castSpell(BlockPos target){
        if(world.isRemote)
            return;
        if(world instanceof ServerWorld){
            double d0 = target.getX() +0.5; //+ world.rand.nextFloat();
            double d1 = target.getY() + 1;//+ world.rand.nextFloat() ;
            double d2 = target.getZ() +0.5; //+ world.rand.nextFloat();
            ((ServerWorld)world).spawnParticle(ParticleTypes.ENCHANTED_HIT, d0, d1, d2,rand.nextInt(4), 0,0.3,0, 0.1);
        }
        if(!(world.getTileEntity(crystalPos) instanceof SummoningCrytalTile))
            return;
        if(((SummoningCrytalTile) world.getTileEntity(crystalPos)).removeMana(spellRecipe)){
            EntitySpellResolver resolver = new EntitySpellResolver(this.spellRecipe);
            resolver.onCastOnBlock(new BlockRayTraceResult(new Vector3d(target.getX(), target.getY(), target.getZ()), Direction.UP,target, false ), this);
        }
        this.ticksSinceLastSpell = 0;
    }

    public boolean enoughManaForTask(){
        if(!(world.getTileEntity(crystalPos) instanceof SummoningCrytalTile || spellRecipe == null || spellRecipe.size() == 0))
            return false;
        return ((SummoningCrytalTile) world.getTileEntity(crystalPos)).enoughMana(spellRecipe);
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
        SummoningCrytalTile tile = world.getTileEntity(crystalPos) instanceof SummoningCrytalTile ? (SummoningCrytalTile) world.getTileEntity(crystalPos) : null;
        return tile == null ? stack : tile.insertItem(stack);
    }

    @Override
    public ItemStack onPlaceBlock() {
        ItemStack heldStack = getHeldStack();
        if(heldStack == null )
            return  ItemStack.EMPTY;
        SummoningCrytalTile tile = world.getTileEntity(crystalPos) instanceof SummoningCrytalTile ? (SummoningCrytalTile) world.getTileEntity(crystalPos) : null;
        return tile == null ? heldStack : tile.getItem(heldStack.getItem());
    }

    public static class PerformTaskGoal extends Goal {
        EntityWhelp kobold;
        BlockPos taskLoc;
        int timePerformingTask;
        public PerformTaskGoal(EntityWhelp kobold){
            this.kobold = kobold;
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public void startExecuting() {
            super.startExecuting();
//            System.out.println("Executing");
            taskLoc = this.kobold.getTaskLoc();
            timePerformingTask = 0;
            if(this.kobold != null && this.kobold.navigator != null && taskLoc != null)
                 this.kobold.navigator.setPath(this.kobold.navigator.getPathToPos(taskLoc, 1), 1.0f);
        }

        @Override
        public void tick() {
            super.tick();
            timePerformingTask++;
            if(kobold == null  || taskLoc == null)
                return;

            if(BlockUtil.distanceFrom(kobold.getPosition(), taskLoc) <= 2){
                kobold.castSpell(taskLoc);
                kobold.navigator.clearPath();
                timePerformingTask = 0;
            }else if(this.kobold != null && kobold.navigator != null && taskLoc != null){
                this.kobold.navigator.setPath(this.kobold.navigator.getPathToPos(taskLoc.up(2), 0), 1f);
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
//            System.out.println("Executing shoul");
            return kobold.ticksSinceLastSpell > 60 && this.taskLoc != null && timePerformingTask < 300;
        }

        @Override
        public boolean shouldExecute() {
            return kobold.canPerformAnotherTask() && kobold.enoughManaForTask();
        }
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
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        if(tag.contains("summoner_x"))
            crystalPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        spellRecipe = SpellRecipeUtil.getSpellsFromTagString(tag.getString("spell"));
        ticksSinceLastSpell = tag.getInt("last_spell");
        if(tag.contains("held"))
            setHeldStack(ItemStack.read((CompoundNBT)tag.get("held")));
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
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
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(HELD_ITEM, ItemStack.EMPTY);
        this.dataManager.register(SPELL_STRING, "");
    }


    class LevitateGoal extends Goal {
        LevitateGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            EntityWhelp kobold = EntityWhelp.this;
            kobold.world.getBlockState(kobold.getPosition());
            int yCorrection = 0;
            int blocksBelow = 0;
            while(!kobold.world.getBlockState(kobold.getPosition().down(blocksBelow)).isSolid() && blocksBelow < 4){
                blocksBelow++;
            }
            System.out.println(blocksBelow);
            return EntityWhelp.this.navigator.noPath() && blocksBelow != 3;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            EntityWhelp kobold = EntityWhelp.this;
            return kobold.navigator.hasPath();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {

            BlockPos loc = getFloatingLoc();
            EntityWhelp.this.navigator.setPath(EntityWhelp.this.navigator.getPathToPos(loc, 1), 1.0D);
            EntityWhelp.this.moveController.setMoveTo(loc.getX(), loc.getY(), loc.getZ(), 0.1);
            System.out.println(EntityWhelp.this.navigator.getPathToPos(loc, 1).getTarget());

        }
        @Nullable
        private BlockPos getFloatingLoc() {
            Vector3d vec3d = EntityWhelp.this.getLook(0.0F);
            boolean flyUp = false;
            for(int i =0; i < 3; i++){
                if(world.getBlockState(EntityWhelp.this.getPosition().down(i)).isSolid()){ // Too close to the ground
                    flyUp = true;
                    continue;
                }
            }
            return flyUp ? EntityWhelp.this.getPosition().up() : EntityWhelp.this.getPosition().down();
        }

        @Override
        public void tick() {
            super.tick();

//            if (--this.timeToRecalcPath <= 0) {
//                this.timeToRecalcPath = 10;
//
//                if (!this.navigator.tryMoveToEntityLiving(this.summon.getSummoner(), this.followSpeed)) {
//
//                    if (!(this.summon.getSelfEntity().getDistanceSq(this.summon.getSummoner()) < 144.0D)) {
//                        int i = MathHelper.floor(this.summon.getSummoner().getPosX()) - 2;
//                        int j = MathHelper.floor(this.summon.getSummoner().getPosZ()) - 2;
//                        int k = MathHelper.floor(this.summon.getSummoner().getBoundingBox().minY);
//
//                        for(int l = 0; l <= 4; ++l) {
//                            for(int i1 = 0; i1 <= 4; ++i1) {
//                                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.canTeleportToBlock(new BlockPos(i + l, k - 1, j + i1))) {
//                                    this.summon.getSelfEntity().setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.summon.getSelfEntity().rotationYaw, this.summon.getSelfEntity().rotationPitch);
//                                    this.navigator.clearPath();
//                                    return;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}
