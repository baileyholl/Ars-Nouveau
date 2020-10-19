package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil.getAugments;

public class SpellResolver {
    protected AbstractCastMethod castType;
    public List<AbstractSpellPart> spell_recipe;
    public SpellContext spellContext;
    public boolean silent;
    public SpellResolver(AbstractCastMethod cast, List<AbstractSpellPart> spell_recipe){
        this.castType = cast;
        this.spell_recipe = spell_recipe;
        if(castType != null)
            this.castType.resolver = this;
    }

    public SpellResolver(List<AbstractSpellPart> spell_recipe, boolean silent){
        this(spell_recipe);
        this.silent = silent;
    }


    public SpellResolver(AbstractSpellPart[] spellParts){
        this(new ArrayList<>(Arrays.asList(spellParts)));
    }

    public SpellResolver(List<AbstractSpellPart> spell_recipe) {
        this(null, spell_recipe);
        AbstractCastMethod method = null;
        if(spell_recipe != null && !spell_recipe.isEmpty() && spell_recipe.get(0) instanceof AbstractCastMethod)
            method = (AbstractCastMethod) spell_recipe.get(0);

        this.castType = method;
        if(this.castType != null)
            this.castType.resolver = this;

    }


    public boolean canCast(LivingEntity entity){
        if(spell_recipe == null || spell_recipe.isEmpty() || castType == null) {
            if(!silent)
                entity.sendMessage(new StringTextComponent("Invalid Spell."), Util.DUMMY_UUID);
            return false;
        }
        return enoughMana(entity);
    }

    boolean enoughMana(LivingEntity entity){
        int totalCost = ManaUtil.getCastingCost(spell_recipe, entity);
        AtomicBoolean canCast = new AtomicBoolean(false);
        ManaCapability.getMana(entity).ifPresent(mana -> {
            canCast.set(totalCost <= mana.getCurrentMana() || (entity instanceof PlayerEntity &&  ((PlayerEntity) entity).isCreative()));
            if(!canCast.get() && !entity.getEntityWorld().isRemote && !silent)
                entity.sendMessage(new StringTextComponent("Not enough mana."), null);
        });
        return canCast.get();
    }

    public boolean postEvent(LivingEntity entity){
        return SpellUtil.postEvent(new SpellCastEvent(entity, spell_recipe));
    }

    public void onCast(ItemStack stack, LivingEntity livingEntity, World world){
        if(canCast(livingEntity) && !postEvent(livingEntity))
            castType.onCast(stack, livingEntity, world, getAugments(spell_recipe, 0, livingEntity));
    }

    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster){
        if(canCast(caster) && !postEvent(caster))
            castType.onCastOnBlock(blockRayTraceResult, caster, getAugments(spell_recipe, 0, caster));
    }

    public void onCastOnBlock(ItemUseContext context){
        if(canCast(context.getPlayer()) && !postEvent(context.getPlayer()))
            castType.onCastOnBlock(context, getAugments(spell_recipe, 0, context.getPlayer()));
    }

    public void onCastOnEntity(ItemStack stack, LivingEntity playerIn, LivingEntity target, Hand hand){
        if(canCast(playerIn) && !postEvent(playerIn))
            castType.onCastOnEntity(stack, playerIn, target, hand, getAugments(spell_recipe, 0, playerIn));
    }


    public void onResolveEffect(World world, LivingEntity shooter, RayTraceResult result){
        spellContext = new SpellContext(spell_recipe, shooter);
        for(int i = 0; i < spell_recipe.size(); i++){
            if(spellContext.isCanceled())
                break;
            AbstractSpellPart spell = spellContext.nextSpell();
            if(spell instanceof AbstractEffect){
                ((AbstractEffect) spell).onResolve(result, world, shooter, getAugments(spell_recipe, i, shooter), spellContext);
            }
        }
    }

    public boolean wouldAllEffectsDoWork(RayTraceResult result, World world, LivingEntity entity, List<AbstractAugment> augments){
        for(AbstractSpellPart spellPart : spell_recipe){
            if(spellPart instanceof AbstractEffect){
                if(!((AbstractEffect) spellPart).wouldSucceed(result, world, entity, augments)){
                    System.out.println("no work");
                    return false;
                }
            }
        }
        System.out.println("all do work");
        return true;
    }

    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity caster, World world, List<AbstractAugment> augments){
        return castType.wouldCastSuccessfully(stack, caster, world, augments);
    }

    public boolean wouldCastOnBlockSuccessfully(ItemUseContext context, List<AbstractAugment> augments){
        return castType.wouldCastOnBlockSuccessfully(context, augments);
    }

    public boolean wouldCastOnBlockSuccessfully(BlockRayTraceResult blockRayTraceResult, LivingEntity caster){
        return castType.wouldCastOnBlockSuccessfully(blockRayTraceResult, caster,  getAugments(spell_recipe, 0, caster));
    }

    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments){
        return castType.wouldCastOnEntitySuccessfully(stack, caster, target, hand, augments);
    }


    public void expendMana(LivingEntity entity){
        int totalCost = ManaUtil.getCastingCost(spell_recipe, entity);
        ManaCapability.getMana(entity).ifPresent(mana -> mana.removeMana(totalCost));
    }
}
