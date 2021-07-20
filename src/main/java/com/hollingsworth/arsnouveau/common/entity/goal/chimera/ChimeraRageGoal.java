package com.hollingsworth.arsnouveau.common.entity.goal.chimera;

import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.EntityChimera;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketAnimEntity;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectDelay;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectGravity;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLaunch;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectPull;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.EnumSet;

public class ChimeraRageGoal extends Goal {

    EntityChimera chimera;
    boolean finished;
    public int ticks;

    public ChimeraRageGoal(EntityChimera chimera){
        this.chimera = chimera;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        ticks++;
        if(ticks >= 40)
            finished = true;
    }

    @Override
    public void start() {
        super.start();
        chimera.rageTimer = 200;
        finished = false;
        ticks = 0;
        Networking.sendToNearby(chimera.level, chimera, new PacketAnimEntity(chimera.getId(), EntityChimera.Animations.HOWL.ordinal()));
        chimera.resetCooldowns();
        chimera.removeAllEffects();
        chimera.gainPhaseBuffs();
        LivingEntity target = chimera.getTarget();
        if(target != null && !target.isOnGround()){
            target.removeEffect(Effects.SLOW_FALLING);
        }
        if(target != null) {
            EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(new Spell.Builder().add(MethodTouch.INSTANCE).add(EffectLaunch.INSTANCE)
                    .add(EffectDelay.INSTANCE)
                    .add(EffectPull.INSTANCE)
                    .add(AugmentAmplify.INSTANCE, 2)
                    .add(EffectGravity.INSTANCE)
                    .add(AugmentExtendTime.INSTANCE)
                    .build(), chimera));
            resolver.onCastOnEntity(target);
            PortUtil.sendMessage(target, new TranslationTextComponent("ars_nouveau.chimera.rage"));
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !finished;
    }

    @Override
    public boolean canUse() {
        return chimera.getTarget() != null && chimera.rageTimer <= 0 && chimera.getPhase() > 0 && chimera.canRage();
    }
}
