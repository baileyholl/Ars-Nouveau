package com.hollingsworth.craftedmagic.spell;

import com.hollingsworth.craftedmagic.api.spell.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.util.ManaUtil;
import com.hollingsworth.craftedmagic.capability.ManaCapability;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;
import com.hollingsworth.craftedmagic.api.spell.CastMethod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpellResolver {
    private CastMethod castType;
//    private EffectType effectType;

    private ArrayList<AbstractSpellPart> spell_recipe;
    
    public SpellResolver(CastMethod cast, ArrayList<AbstractSpellPart> spell_recipe){
        this.castType = cast;
        this.spell_recipe = spell_recipe;
        this.castType.resolver = this;
    }

    public SpellResolver(ArrayList<AbstractSpellPart> spell_recipe) {
        this((CastMethod) spell_recipe.get(0), spell_recipe);
    }

    public boolean canCast(PlayerEntity entity){
        if(spell_recipe.isEmpty())
            return false;

        int totalCost = ManaUtil.calculateCost(spell_recipe);
        AtomicBoolean canCast = new AtomicBoolean(false);
        ManaCapability.getMana(entity).ifPresent(mana -> {
            canCast.set(totalCost <= mana.getCurrentMana() || entity.isCreative());

        });
        return canCast.get();
    }

    public void onCast(ItemStack stack, PlayerEntity playerEntity, World world){
        if(canCast(playerEntity))
            castType.onCast(stack, playerEntity, world);
    }

    public void onCastOnBlock(ItemUseContext context){
        if(canCast(context.getPlayer()))
            castType.onCastOnBlock(context);
    }

    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand){
        if(canCast(playerIn))
            castType.onCastOnEntity(stack, playerIn, target, hand);
    }

    public void onResolveEffect(World world, LivingEntity shooter, RayTraceResult result){
        for(int i = 0; i < spell_recipe.size(); i++){
            AbstractSpellPart spell = spell_recipe.get(i);
            if(spell instanceof AbstractEffect){
                ArrayList<AugmentType> augments = new ArrayList<>();
                for(int j = i + 1; j < spell_recipe.size(); j++){
                    AbstractSpellPart next_spell = spell_recipe.get(j);
                    if(next_spell instanceof AugmentType){
                        augments.add((AugmentType) next_spell);
                    }else{
                        break;
                    }
                }
                ((AbstractEffect) spell).onResolve(result, world, shooter, augments);
            }
        }
    }

    public void expendMana(LivingEntity entity){
        int totalCost = ManaUtil.calculateCost(spell_recipe);
        ManaCapability.getMana(entity).ifPresent(mana -> {
            mana.removeMana(totalCost);
        });
    }
}
