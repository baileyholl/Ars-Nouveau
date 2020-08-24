package com.hollingsworth.arsnouveau.common.spell;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil.getAugments;

public class SpellResolver {
    protected AbstractCastMethod castType;
//    private EffectType effectType;

    protected ArrayList<AbstractSpellPart> spell_recipe;
    
    public SpellResolver(AbstractCastMethod cast, ArrayList<AbstractSpellPart> spell_recipe){
        this.castType = cast;
        this.spell_recipe = spell_recipe;
        if(castType != null)
            this.castType.resolver = this;
    }


    public SpellResolver(AbstractSpellPart[] spellParts){
        this(new ArrayList<>(Arrays.asList(spellParts)));
    }

    public SpellResolver(ArrayList<AbstractSpellPart> spell_recipe) {
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
            entity.sendMessage(new StringTextComponent("Invalid Spell."));
            return false;
        }
        Set<AbstractSpellPart> testSet = new HashSet<>(spell_recipe.size());
        for(AbstractSpellPart part : spell_recipe){
            if(part instanceof AbstractEffect && !testSet.add(part)) {
                entity.sendMessage(new StringTextComponent("No duplicate effects are allowed. Use Augments!"));
                return false;
            }
        }

        return enoughMana(entity);
    }

    boolean enoughMana(LivingEntity entity){
        int totalCost = ManaUtil.getCastingCost(spell_recipe, entity);
        AtomicBoolean canCast = new AtomicBoolean(false);
        ManaCapability.getMana(entity).ifPresent(mana -> {
            canCast.set(totalCost <= mana.getCurrentMana() || (entity instanceof PlayerEntity &&  ((PlayerEntity) entity).isCreative()));
            if(!canCast.get())
                entity.sendMessage(new StringTextComponent("Not enough mana."));
        });
        return canCast.get();
    }

    public void onCast(ItemStack stack, LivingEntity livingEntity, World world){
        if(canCast(livingEntity))
            castType.onCast(stack, livingEntity, world, getAugments(spell_recipe, 0, livingEntity));
    }

    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster){
        if(canCast(caster))
            castType.onCastOnBlock(blockRayTraceResult, caster, getAugments(spell_recipe, 0, caster));
    }

    public void onCastOnBlock(ItemUseContext context){
        if(canCast(context.getPlayer()))
            castType.onCastOnBlock(context, getAugments(spell_recipe, 0, context.getPlayer()));
    }

    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand){
        if(canCast(playerIn))
            castType.onCastOnEntity(stack, playerIn, target, hand, getAugments(spell_recipe, 0, playerIn));
    }



    public void onResolveEffect(World world, LivingEntity shooter, RayTraceResult result){
        for(int i = 0; i < spell_recipe.size(); i++){
            AbstractSpellPart spell = spell_recipe.get(i);
            if(spell instanceof AbstractEffect){
                ((AbstractEffect) spell).onResolve(result, world, shooter, getAugments(spell_recipe, i, shooter));
            }
        }
    }

    public void expendMana(LivingEntity entity){
        int totalCost = ManaUtil.getCastingCost(spell_recipe, entity);
        ManaCapability.getMana(entity).ifPresent(mana -> {
            mana.removeMana(totalCost);
        });
    }
}
