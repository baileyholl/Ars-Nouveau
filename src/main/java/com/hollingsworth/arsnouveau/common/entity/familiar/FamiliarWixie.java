package com.hollingsworth.arsnouveau.common.entity.familiar;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.PotionEvent;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FamiliarWixie extends FlyingFamiliarEntity implements IAnimationListener {
    public int debuffCooldown;

    public FamiliarWixie(EntityType<? extends PathfinderMob> ent, Level world) {
        super(ent, world);
    }


    public void potionEvent(PotionEvent.PotionAddedEvent event) {
        if(!isAlive())
            return;
        if(event.getEntity() != null && !event.getEntity().level.isClientSide && event.getEntity().equals(getOwner())){
            event.getPotionEffect().duration += event.getPotionEffect().duration * .2;
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
        if(!level.isClientSide && debuffCooldown > 0)
            debuffCooldown--;
    }

    @Override
    public PlayState walkPredicate(AnimationEvent event) {
        if(getNavigation().isInProgress())
            return PlayState.STOP;
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        super.registerControllers(data);
        data.addAnimationController(new AnimationController<>(this, "castController", 1, (a) -> PlayState.CONTINUE));
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.ENTITY_FAMILIAR_WIXIE;
    }

    @Override
    public void startAnimation(int arg) {
        if(arg == EntityWixie.Animations.CAST.ordinal()){
            AnimationController controller = this.factory.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("castController");
            controller.markNeedsReload();
            controller.setAnimation(new AnimationBuilder().addAnimation("cast", false));
        }
    }

    public static class DebuffTargetGoal extends Goal {
        FamiliarWixie wixie;

        public static ArrayList<MobEffect> effectTable = new ArrayList<>(Arrays.asList(
                MobEffects.MOVEMENT_SLOWDOWN, MobEffects.WEAKNESS, MobEffects.LEVITATION, MobEffects.POISON
        ));

        public DebuffTargetGoal(FamiliarWixie wixie){
            this.wixie = wixie;
        }

        @Override
        public void tick() {
            super.tick();
            if(wixie.getTarget() == null)
                return;
            MobEffect effect = effectTable.get(new Random().nextInt(effectTable.size()));
            if(effect == MobEffects.POISON){
                if(wixie.getTarget().isInvertedHealAndHarm())
                    effect = MobEffects.REGENERATION;
            }
            Networking.sendToNearby(wixie.level, wixie, new PacketAnimEntity(wixie.getId(), EntityWixie.Animations.CAST.ordinal()));
            wixie.getTarget().addEffect(new MobEffectInstance(effect, 7 * 20, new Random().nextInt(2)));
            wixie.debuffCooldown = 150;
        }

        @Override
        public boolean canUse() {
            return wixie.debuffCooldown <= 0 && wixie.getTarget() != null;
        }
    }
}
