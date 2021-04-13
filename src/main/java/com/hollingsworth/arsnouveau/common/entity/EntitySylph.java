package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrystalTile;
import com.hollingsworth.arsnouveau.common.entity.goal.GoBackHomeGoal;
import com.hollingsworth.arsnouveau.common.entity.goal.sylph.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class EntitySylph extends AbstractFlyingCreature implements IPickupResponder, IAnimatable, ITooltipProvider, IDispellable {
    AnimationFactory manager = new AnimationFactory(this);

    public int timeSinceBonemeal = 0;
    public static final DataParameter<Boolean> TAMED = EntityDataManager.createKey(EntitySylph.class, DataSerializers.BOOLEAN);
    /*Strictly used for after a tame event*/
    public int tamingTime = 0;
    public boolean droppingShards; // Stricly used by non-tamed spawns for giving shards
    public static final DataParameter<Integer> MOOD_SCORE = EntityDataManager.createKey(EntitySylph.class, DataSerializers.VARINT);
    public List<ItemStack> ignoreItems;
    public int timeUntilGather = 0;
    public int timeUntilEvaluation = 0;
    public int diversityScore;
    public Map<BlockState, Integer> genTable;
    public Map<BlockState, Integer> scoreMap;
    public BlockPos crystalPos;
    public List<ItemStack> drops;
    private boolean setBehaviors;
    private <E extends Entity> PlayState idlePredicate(AnimationEvent event) {
        //   manager.setAnimationSpeed(1.0f);
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "idleController", 20, this::idlePredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }
    @Override
    protected int getExperiencePoints(PlayerEntity player) {
        return 0;
    }


    @Override
    protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        if(player.getEntityWorld().isRemote)
            return super.func_230254_b_(player,hand);
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == ItemsRegistry.DENY_ITEM_SCROLL) {
            List<ItemStack> items = ItemsRegistry.DENY_ITEM_SCROLL.getItems(stack);
            if (!items.isEmpty()) {
                this.ignoreItems = ItemsRegistry.DENY_ITEM_SCROLL.getItems(stack);
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.sylph.ignore"));
            }
        }
        return super.func_230254_b_(player,hand);
    }

    @Override
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
        if(hand != Hand.MAIN_HAND || player.getEntityWorld().isRemote || !this.dataManager.get(TAMED))
            return ActionResultType.PASS;
        
        ItemStack stack = player.getHeldItem(hand);
        if(stack.isEmpty()) {
            int moodScore = dataManager.get(MOOD_SCORE);
            if(moodScore < 250){
                PortUtil.sendMessage(player, new TranslationTextComponent("sylph.unhappy"));
            }else if(moodScore <= 500){
                PortUtil.sendMessage(player, new TranslationTextComponent("sylph.content"));
            }else if(moodScore <= 750){
                PortUtil.sendMessage(player, new TranslationTextComponent("sylph.happy"));
            }else if(moodScore < 1000){
                PortUtil.sendMessage(player, new TranslationTextComponent("sylph.very_happy"));
            }else{
                PortUtil.sendMessage(player, new TranslationTextComponent("sylph.extremely_happy"));
            }
            int numDrops = diversityScore / 2;
            if(numDrops <= 5){
                PortUtil.sendMessage(player, new TranslationTextComponent("sylph.okay_diversity"));
            }else if(numDrops <= 10){
                PortUtil.sendMessage(player, new TranslationTextComponent("sylph.diverse_enough"));
            }else if(numDrops <= 20){
                PortUtil.sendMessage(player, new TranslationTextComponent("sylph.very_diverse"));
            }else{
                PortUtil.sendMessage(player, new TranslationTextComponent("sylph.extremely_diverse"));
            }
            if(ignoreItems != null && !ignoreItems.isEmpty()) {
                StringBuilder status = new StringBuilder();
                status.append(new TranslationTextComponent("ars_nouveau.sylph.ignore_list").getString());
                for (ItemStack i : ignoreItems) {
                    status.append(i.getDisplayName().getString()).append(" ");
                }
                PortUtil.sendMessage(player, status.toString());
            }

            return ActionResultType.SUCCESS;
        }
        if(!(stack.getItem() instanceof BlockItem))
            return ActionResultType.PASS;
        BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
        int score = EvaluateGroveGoal.getScore(state);
        if(score > 0 && this.scoreMap != null && this.scoreMap.get(state) != null && this.scoreMap.get(state) >= 50){
            PortUtil.sendMessage(player, new TranslationTextComponent("sylph.toomuch"));
            return ActionResultType.SUCCESS;
        }

        if(score == 0) {
            PortUtil.sendMessage(player, new TranslationTextComponent("sylph.notinterested"));
        }
        if(score == 1){
            PortUtil.sendMessage(player, new TranslationTextComponent("sylph.likes"));
        }

        if(score == 2){
            PortUtil.sendMessage(player, new TranslationTextComponent("sylph.excited"));
        }
        return ActionResultType.SUCCESS;
    }

    protected EntitySylph(EntityType<? extends AbstractFlyingCreature> type, World worldIn) {
        super(type, worldIn);
        MinecraftForge.EVENT_BUS.register(this);
        this.moveController =  new FlyingMovementController(this, 10, true);
        addGoalsAfterConstructor();
    }

    public EntitySylph(World world, boolean isTamed, BlockPos pos) {
        super(ModEntities.ENTITY_SYLPH_TYPE, world);
        MinecraftForge.EVENT_BUS.register(this);
        this.moveController =  new FlyingMovementController(this, 10, true);
        this.dataManager.set(TAMED, isTamed);
        this.crystalPos = pos;
        addGoalsAfterConstructor();
    }



    @Override
    public void tick() {
        super.tick();
        if(!this.world.isRemote){
            if(world.getGameTime() % 20 == 0 && this.getPosition().getY() < 0) {
                this.remove();
                return;
            }

            if(Boolean.TRUE.equals(this.dataManager.get(TAMED))){
                this.timeUntilEvaluation--;
                this.timeUntilGather--;
            }
            this.timeSinceBonemeal++;
        }
        if(this.droppingShards) {
            tamingTime++;
            if(tamingTime % 20 == 0 && !world.isRemote())
                Networking.sendToNearby(world, this, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, getPosition()));

            if(tamingTime > 60 && !world.isRemote) {
                ItemStack stack = new ItemStack(ItemsRegistry.sylphShard, 1 + world.rand.nextInt(1));
                world.addEntity(new ItemEntity(world, getPosX(), getPosY() + 0.5, getPosZ(), stack));
                this.remove(false);
                world.playSound(null, getPosX(), getPosY(), getPosZ(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1f, 1f );
            }
            else if (tamingTime > 55 && world.isRemote){
                for(int i =0; i < 10; i++){
                    double d0 = getPosX();
                    double d1 = getPosY()+0.1;
                    double d2 = getPosZ();
                    world.addParticle(ParticleTypes.END_ROD, d0, d1, d2, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3);
                }
            }
        }
    }

    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void addGoalsAfterConstructor(){
        if(this.world.isRemote())
            return;

        for(PrioritizedGoal goal : getGoals()){
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<PrioritizedGoal> getGoals(){
        return this.dataManager.get(TAMED) ? getTamedGoals() : getUntamedGoals();
    }

    public boolean enoughManaForTask(){
        if(!(world.getTileEntity(crystalPos) instanceof SummoningCrystalTile))
            return false;
        return ((SummoningCrystalTile) world.getTileEntity(crystalPos)).enoughMana(250);
    }

    public boolean removeManaForDrops(){
        if(!(world.getTileEntity(crystalPos) instanceof SummoningCrystalTile))
            return false;
        return ((SummoningCrystalTile) world.getTileEntity(crystalPos)).removeManaAround(250);
    }

    public boolean isTamed(){
        return this.dataManager.get(TAMED);
    }


    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if(source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH)
            return false;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onDeath(DamageSource source) {
        if(!world.isRemote && isTamed()){
            ItemStack stack = new ItemStack(ItemsRegistry.sylphCharm);
            world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), stack));
        }
        super.onDeath(source);
    }

    //MOJANG MAKES THIS SO CURSED WHAT THE HECK

    public List<PrioritizedGoal> getTamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new BonemealGoal(this, () -> this.crystalPos, 10)));
        list.add(new PrioritizedGoal(1, new EvaluateGroveGoal(this, 20 * 120 )));
        list.add(new PrioritizedGoal(2, new InspectPlantGoal(this, () -> this.crystalPos,15)));
        list.add(new PrioritizedGoal(1, new GoBackHomeGoal(this, () -> this.crystalPos,20)));
        list.add(new PrioritizedGoal(1, new GenerateDropsGoal(this)));
        list.add(new PrioritizedGoal(0, new SwimGoal(this)));
        return list;
    }

    public List<PrioritizedGoal> getUntamedGoals(){
        List<PrioritizedGoal> list = new ArrayList<>();
        list.add(new PrioritizedGoal(3, new FollowMobGoalBackoff(this, 1.0D, 3.0F, 7.0F, 0.5f)));
        list.add(new PrioritizedGoal(5, new FollowPlayerGoal(this, 1.0D, 3.0F, 7.0F)));
        list.add(new PrioritizedGoal(2, new LookRandomlyGoal(this)));
        list.add(new PrioritizedGoal(2, new WaterAvoidingRandomFlyingGoal(this, 1.0D)));
        list.add(new PrioritizedGoal(1, new BonemealGoal(this)));
        list.add(new PrioritizedGoal(0, new SwimGoal(this)));
        return list;
    }

    @SubscribeEvent
    public void treeGrow(SaplingGrowTreeEvent event) {
        if(!this.dataManager.get(TAMED) && BlockUtil.distanceFrom(this.getPosition(), event.getPos()) <= 10) {
            this.droppingShards = true;
        }
    }


    @Override
    protected void registerGoals() { /*Do not use. See above*/}

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        if(!this.dataManager.get(TAMED))
            return tooltip;
        int mood = this.dataManager.get(MOOD_SCORE);
        String moodStr = new TranslationTextComponent("ars_nouveau.sylph.tooltip_unhappy").getString();
        if(mood >= 1000)
            moodStr = new TranslationTextComponent("ars_nouveau.sylph.tooltip_extremely_happy").getString();
        else if(mood >= 750)
            moodStr = new TranslationTextComponent("ars_nouveau.sylph.tooltip_very_happy").getString();
        else if(mood >= 500)
            moodStr = new TranslationTextComponent("ars_nouveau.sylph.tooltip_happy").getString();
        else if(mood >= 250)
            moodStr = new TranslationTextComponent("ars_nouveau.sylph.tooltip_content").getString();
        tooltip.add(new TranslationTextComponent("ars_nouveau.sylph.tooltip_mood").getString() + moodStr);
        return tooltip;
    }

    public boolean isValidReward(ItemStack stack){
        if(ignoreItems == null || ignoreItems.isEmpty())
            return true;
        return ignoreItems.stream().noneMatch(i -> i.isItemEqual(stack));
    }


    @Override
    public ItemStack onPickup(ItemStack stack) {
        if(!isValidReward(stack))
            return stack;
        SummoningCrystalTile tile = world.getTileEntity(crystalPos) instanceof SummoningCrystalTile ? (SummoningCrystalTile) world.getTileEntity(crystalPos) : null;
        return tile == null ? stack : tile.insertItem(stack);
    }


    @Override
    public boolean canDespawn(double p_213397_1_) {
        return false;
    }

    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .createMutableAttribute(Attributes.MAX_HEALTH, 6.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D);
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
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        if(tag.contains("summoner_x"))
            crystalPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        timeSinceBonemeal = tag.getInt("bonemeal");
        timeUntilGather = tag.getInt("gather");
        timeUntilEvaluation = tag.getInt("eval");
        this.dataManager.set(TAMED, tag.getBoolean("tamed"));
        this.dataManager.set(EntitySylph.MOOD_SCORE, tag.getInt("score"));
        if(!setBehaviors){
            tryResetGoals();
            setBehaviors = true;
        }
        ignoreItems = NBTUtil.readItems(tag, "ignored_");
    }
    // A workaround for goals not registering correctly for a dynamic variable on reload as read() is called after constructor.
    public void tryResetGoals(){
        this.goalSelector.goals = new LinkedHashSet<>();
        this.addGoalsAfterConstructor();
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        if(crystalPos != null){
            tag.putInt("summoner_x", crystalPos.getX());
            tag.putInt("summoner_y", crystalPos.getY());
            tag.putInt("summoner_z", crystalPos.getZ());
        }
        tag.putInt("eval", timeUntilEvaluation);
        tag.putInt("bonemeal", timeSinceBonemeal);
        tag.putInt("gather", timeUntilGather);
        tag.putBoolean("tamed", this.dataManager.get(TAMED));
        tag.putInt("score", this.dataManager.get(EntitySylph.MOOD_SCORE));
        if (ignoreItems != null && !ignoreItems.isEmpty())
            NBTUtil.writeItems(tag, "ignored_", ignoreItems);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(MOOD_SCORE, 0);
        this.dataManager.register(TAMED, false);
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(this.removed)
            return false;

        if(!world.isRemote && isTamed()){
            ItemStack stack = new ItemStack(ItemsRegistry.sylphCharm);
            world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), stack));
            ParticleUtil.spawnPoof((ServerWorld)world, getPosition());
            this.remove();
        }
        return this.isTamed();
    }
}