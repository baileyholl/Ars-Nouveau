package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FamiliarWixie extends FlyingFamiliarEntity implements IAnimationListener {
    public int debuffCooldown;

    public FamiliarWixie(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }


    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);

        if (player.getMainHandItem().is(Tags.Items.DYES)) {
            DyeColor color = DyeColor.getColor(stack);
            if (color == null || this.entityData.get(COLOR).equals(color.getName()) || !Arrays.asList(EntityWixie.COLORS).contains(color.getName()))
                return InteractionResult.SUCCESS;
            setColor(color);
            return InteractionResult.SUCCESS;
        }else{
            for (BrewingRecipe r : ArsNouveauAPI.getInstance().getAllPotionRecipes(level())) {
                ItemStack water = PotionUtil.getPotion(Potions.WATER);
                ItemStack awkward = PotionUtil.getPotion(Potions.AWKWARD);

                if(r.isIngredient(stack) && (r.getInput().test(awkward) || r.getInput().test(water))){
                    PotionData data = new PotionData(r.getOutput().copy());

                    if(!data.isEmpty()){
                        data.applyEffects(player, player, player);
                        PortUtil.sendMessage(player, Component.translatable("ars_nouveau.wixie_familiar.applied",data.asPotionStack().getHoverName().getString()));
                        Networking.sendToNearbyClient(level(), this, new PacketAnimEntity(this.getId(), EntityWixie.Animations.CAST.ordinal()));
                        ParticleUtil.spawnPoof((ServerLevel) level(), player.blockPosition().above());
                        stack.shrink(1);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    public void potionEvent(MobEffectEvent.Added event) {
        if (!isAlive())
            return;
        Entity target = event.getEntity();
        Entity applier = event.getEffectSource();

        boolean isBeneficialOwner = target.equals(getOwner()) && event.getEffectInstance().getEffect().value().isBeneficial();
        boolean isApplierOwner = applier != null && applier.equals(this.getOwner());
        if(isBeneficialOwner || isApplierOwner){
            event.getEffectInstance().getCures();
            event.getEffectInstance().mapDuration(duration -> duration + duration * 2);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new DebuffTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide && debuffCooldown > 0)
            debuffCooldown--;
    }

    @Override
    public PlayState walkPredicate(AnimationState event) {
        return PlayState.CONTINUE;
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_WIXIE.get();
    }

    @Override
    public void startAnimation(int arg) {
        if(controller == null)
            return;
        if (arg == EntityWixie.Animations.CAST.ordinal()) {
            controller.forceAnimationReset();
            controller.setAnimation(RawAnimation.begin().thenPlay("cast"));
        }
    }

    @Override
    public ResourceLocation getTexture(FamiliarEntity entity) {
        String color = getColor().toLowerCase();
        if (color.isEmpty())
            color = "blue";
        return ArsNouveau.prefix( "textures/entity/wixie_" + color + ".png");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    public static class DebuffTargetGoal extends Goal {
        FamiliarWixie wixie;

        public static ArrayList<Holder<MobEffect>> effectTable = new ArrayList<>(Arrays.asList(
                MobEffects.MOVEMENT_SLOWDOWN, MobEffects.WEAKNESS, MobEffects.LEVITATION, MobEffects.POISON
        ));

        public DebuffTargetGoal(FamiliarWixie wixie) {
            this.wixie = wixie;
        }

        @Override
        public void tick() {
            super.tick();
            if (wixie.getTarget() == null)
                return;
            Holder<MobEffect> effect = effectTable.get(new Random().nextInt(effectTable.size()));
            if (effect == MobEffects.POISON) {
                if (wixie.getTarget().isInvertedHealAndHarm())
                    effect = MobEffects.REGENERATION;
            }
            Networking.sendToNearbyClient(wixie.level, wixie, new PacketAnimEntity(wixie.getId(), EntityWixie.Animations.CAST.ordinal()));
            wixie.getTarget().addEffect(new MobEffectInstance(effect, 7 * 20, new Random().nextInt(2)));
            wixie.debuffCooldown = 150;
        }

        @Override
        public boolean canUse() {
            return wixie.debuffCooldown <= 0 && wixie.getTarget() != null;
        }
    }
}
