package com.hollingsworth.craftedmagic.spell;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.spell.effect.EffectType;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import com.hollingsworth.craftedmagic.spell.method.CastMethod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

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

    public void onCast(ItemStack stack, PlayerEntity playerEntity, World world){
        castType.onCast(stack, playerEntity, world);
    }

    public void onCastOnBlock(ItemUseContext context){
        castType.onCastOnBlock(context);
    }

    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand){
        castType.onCastOnEntity(stack, playerIn, target, hand);
    }

    public void onResolveEffect(World world, LivingEntity shooter, RayTraceResult result){
        for(int i = 0; i < spell_recipe.size(); i++){
            AbstractSpellPart spell = spell_recipe.get(i);
            if(spell instanceof EffectType){

                ArrayList<AugmentType> augments = new ArrayList<>();
                for(int j = i + 1; j < spell_recipe.size(); j++){
                    AbstractSpellPart next_spell = spell_recipe.get(j);
                    if(next_spell instanceof AugmentType){
                        System.out.println("Applying enhancement ");
                        augments.add((AugmentType) next_spell);
                    }else{
                        break;
                    }
                }
                ((EffectType) spell).onResolve(result, world, shooter, augments);
            }
        }
    }
}
