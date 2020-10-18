package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

import static com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil.getAugments;

public class DelayedSpellEvent implements ITimedEvent{
    private int duration;
    private final List<AbstractSpellPart> recipe;
    private final RayTraceResult result;
    private final World world;
    private final LivingEntity shooter;

    public DelayedSpellEvent(int delay, List<AbstractSpellPart> recipe, RayTraceResult result, World world, LivingEntity shooter){
        this.duration = delay;
        this.recipe = recipe;
        this.result = result;
        this.world = world;
        this.shooter = shooter;
    }

    @Override
    public void tick() {
        duration--;
        if(duration <= 0){
            resolveSpell();
        }
    }

    public void resolveSpell(){
        if(world == null)
            return;
        SpellContext context = new SpellContext(recipe, shooter);
        for(int i = 0; i < recipe.size(); i++){
            if(context.isCanceled())
                break;
            AbstractSpellPart spell = context.nextSpell();
            if(spell instanceof AbstractEffect){
                ((AbstractEffect) spell).onResolve(result, world, shooter, getAugments(recipe, i, shooter), context);
            }
        }
    }

    @Override
    public boolean isExpired() {
        return duration <= 0 || world == null;
    }
}
