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
import net.minecraft.item.Item;
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
import net.minecraftforge.common.Tags;
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
    public static final DataParameter<Boolean> TAMED = EntityDataManager.defineId(EntitySylph.class, DataSerializers.BOOLEAN);
    /*Strictly used for after a tame event*/
    public int tamingTime = 0;
    public boolean droppingShards; // Stricly used by non-tamed spawns for giving shards
    public static final DataParameter<Integer> MOOD_SCORE = EntityDataManager.defineId(EntitySylph.class, DataSerializers.INT);
    public static final DataParameter<String> COLOR = EntityDataManager.defineId(EntitySylph.class, DataSerializers.STRING);
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
    protected int getExperienceReward(PlayerEntity player) {
        return 0;
    }


    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if(player.getCommandSenderWorld().isClientSide)
            return super.mobInteract(player,hand);
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == ItemsRegistry.DENY_ITEM_SCROLL) {
            List<ItemStack> items = ItemsRegistry.DENY_ITEM_SCROLL.getItems(stack);
            if (!items.isEmpty()) {
                this.ignoreItems = ItemsRegistry.DENY_ITEM_SCROLL.getItems(stack);
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.sylph.ignore"));
            }
        }
        return super.mobInteract(player,hand);
    }

    public String getColor(){
        return this.entityData.get(COLOR);
    }

    @Override
    public ActionResultType interactAt(PlayerEntity player, Vector3d vec, Hand hand) {
        if(hand != Hand.MAIN_HAND || player.getCommandSenderWorld().isClientSide || !this.entityData.get(TAMED))
            return ActionResultType.PASS;
        
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        if(Tags.Items.DYES.contains(item)){
            System.out.println("contains");
            if(Tags.Items.DYES_GREEN.contains(item) && !getColor().equals("summer")){
                this.entityData.set(COLOR, "summer");
                stack.shrink(1);
                return ActionResultType.SUCCESS;
            }
            if(Tags.Items.DYES_ORANGE.contains(item) && !getColor().equals("autumn")){
                this.entityData.set(COLOR, "autumn");
                stack.shrink(1);
                return ActionResultType.SUCCESS;
            }
            if(Tags.Items.DYES_YELLOW.contains(item) && !getColor().equals("spring")){
                this.entityData.set(COLOR, "spring");
                stack.shrink(1);
                return ActionResultType.SUCCESS;
            }
            if(Tags.Items.DYES_WHITE.contains(item) && !getColor().equals("winter")){
                this.entityData.set(COLOR, "winter");
                stack.shrink(1);
                return ActionResultType.SUCCESS;
            }


        }


        if(stack.isEmpty()) {
            int moodScore = entityData.get(MOOD_SCORE);
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
                    status.append(i.getHoverName().getString()).append(" ");
                }
                PortUtil.sendMessage(player, status.toString());
            }

            return ActionResultType.SUCCESS;
        }
        if(!(stack.getItem() instanceof BlockItem))
            return ActionResultType.PASS;
        BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
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
        this.moveControl =  new FlyingMovementController(this, 10, true);
        addGoalsAfterConstructor();
    }

    public EntitySylph(World world, boolean isTamed, BlockPos pos) {
        super(ModEntities.ENTITY_SYLPH_TYPE, world);
        MinecraftForge.EVENT_BUS.register(this);
        this.moveControl =  new FlyingMovementController(this, 10, true);
        this.entityData.set(TAMED, isTamed);
        this.crystalPos = pos;
        addGoalsAfterConstructor();
    }



    @Override
    public void tick() {
        super.tick();
        if(!this.level.isClientSide){
            if(level.getGameTime() % 20 == 0 && this.blockPosition().getY() < 0) {
                this.remove();
                return;
            }

            if(Boolean.TRUE.equals(this.entityData.get(TAMED))){
                this.timeUntilEvaluation--;
                this.timeUntilGather--;
            }
            this.timeSinceBonemeal++;
        }
        if(this.droppingShards) {
            tamingTime++;
            if(tamingTime % 20 == 0 && !level.isClientSide())
                Networking.sendToNearby(level, this, new PacketANEffect(PacketANEffect.EffectType.TIMED_HELIX, blockPosition()));

            if(tamingTime > 60 && !level.isClientSide) {
                ItemStack stack = new ItemStack(ItemsRegistry.sylphShard, 1 + level.random.nextInt(1));
                level.addFreshEntity(new ItemEntity(level, getX(), getY() + 0.5, getZ(), stack));
                this.remove(false);
                level.playSound(null, getX(), getY(), getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1f, 1f );
            }
            else if (tamingTime > 55 && level.isClientSide){
                for(int i =0; i < 10; i++){
                    double d0 = getX();
                    double d1 = getY()+0.1;
                    double d2 = getZ();
                    level.addParticle(ParticleTypes.END_ROD, d0, d1, d2, (level.random.nextFloat() * 1 - 0.5)/3, (level.random.nextFloat() * 1 - 0.5)/3, (level.random.nextFloat() * 1 - 0.5)/3);
                }
            }
        }
    }

    // Cannot add conditional goals in RegisterGoals as it is final and called during the MobEntity super.
    protected void addGoalsAfterConstructor(){
        if(this.level.isClientSide())
            return;

        for(PrioritizedGoal goal : getGoals()){
            this.goalSelector.addGoal(goal.getPriority(), goal.getGoal());
        }
    }

    public List<PrioritizedGoal> getGoals(){
        return this.entityData.get(TAMED) ? getTamedGoals() : getUntamedGoals();
    }

    public boolean enoughManaForTask(){
        if(!(level.getBlockEntity(crystalPos) instanceof SummoningCrystalTile))
            return false;
        return ((SummoningCrystalTile) level.getBlockEntity(crystalPos)).enoughMana(250);
    }

    public boolean removeManaForDrops(){
        if(!(level.getBlockEntity(crystalPos) instanceof SummoningCrystalTile))
            return false;
        return ((SummoningCrystalTile) level.getBlockEntity(crystalPos)).removeManaAround(250);
    }

    public boolean isTamed(){
        return this.entityData.get(TAMED);
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH)
            return false;
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        if(!level.isClientSide && isTamed()){
            ItemStack stack = new ItemStack(ItemsRegistry.sylphCharm);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        }
        super.die(source);
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
        if(!this.entityData.get(TAMED) && BlockUtil.distanceFrom(this.blockPosition(), event.getPos()) <= 10) {
            this.droppingShards = true;
        }
    }


    @Override
    protected void registerGoals() { /*Do not use. See above*/}

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        if(!this.entityData.get(TAMED))
            return tooltip;
        int mood = this.entityData.get(MOOD_SCORE);
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
        return ignoreItems.stream().noneMatch(i -> i.sameItem(stack));
    }


    @Override
    public ItemStack onPickup(ItemStack stack) {
        if(!isValidReward(stack))
            return stack;
        SummoningCrystalTile tile = level.getBlockEntity(crystalPos) instanceof SummoningCrystalTile ? (SummoningCrystalTile) level.getBlockEntity(crystalPos) : null;
        return tile == null ? stack : tile.insertItem(stack);
    }


    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return false;
    }

    public static AttributeModifierMap.MutableAttribute attributes() {
        return MobEntity.createMobAttributes().add(Attributes.FLYING_SPEED, Attributes.FLYING_SPEED.getDefaultValue())
                .add(Attributes.MAX_HEALTH, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
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
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("summoner_x"))
            crystalPos = new BlockPos(tag.getInt("summoner_x"), tag.getInt("summoner_y"), tag.getInt("summoner_z"));
        timeSinceBonemeal = tag.getInt("bonemeal");
        timeUntilGather = tag.getInt("gather");
        timeUntilEvaluation = tag.getInt("eval");
        this.entityData.set(TAMED, tag.getBoolean("tamed"));
        this.entityData.set(EntitySylph.MOOD_SCORE, tag.getInt("score"));
        if(!setBehaviors){
            tryResetGoals();
            setBehaviors = true;
        }
        ignoreItems = NBTUtil.readItems(tag, "ignored_");
        this.entityData.set(COLOR, tag.getString("color"));
    }
    // A workaround for goals not registering correctly for a dynamic variable on reload as read() is called after constructor.
    public void tryResetGoals(){
        this.goalSelector.availableGoals = new LinkedHashSet<>();
        this.addGoalsAfterConstructor();
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        if(crystalPos != null){
            tag.putInt("summoner_x", crystalPos.getX());
            tag.putInt("summoner_y", crystalPos.getY());
            tag.putInt("summoner_z", crystalPos.getZ());
        }
        tag.putInt("eval", timeUntilEvaluation);
        tag.putInt("bonemeal", timeSinceBonemeal);
        tag.putInt("gather", timeUntilGather);
        tag.putBoolean("tamed", this.entityData.get(TAMED));
        tag.putInt("score", this.entityData.get(EntitySylph.MOOD_SCORE));
        tag.putString("color", this.entityData.get(COLOR));
        if (ignoreItems != null && !ignoreItems.isEmpty())
            NBTUtil.writeItems(tag, "ignored_", ignoreItems);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MOOD_SCORE, 0);
        this.entityData.define(TAMED, false);
        this.entityData.define(COLOR, "summer");
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(this.removed)
            return false;

        if(!level.isClientSide && isTamed()){
            ItemStack stack = new ItemStack(ItemsRegistry.sylphCharm);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
            ParticleUtil.spawnPoof((ServerWorld)level, blockPosition());
            this.remove();
        }
        return this.isTamed();
    }
}