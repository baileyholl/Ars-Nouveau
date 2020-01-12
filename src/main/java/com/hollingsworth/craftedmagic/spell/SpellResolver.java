package com.hollingsworth.craftedmagic.spell;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.Position;
import com.hollingsworth.craftedmagic.spell.effect.EffectType;
import com.hollingsworth.craftedmagic.spell.enhancement.EnhancementType;
import com.hollingsworth.craftedmagic.spell.method.CastMethod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
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

    public SpellResolver(ArrayList<AbstractSpellPart> spell_recipe){
        this((CastMethod)spell_recipe.get(0), spell_recipe);
    }
//
//    public SpellResolver(AbstractSpellPart cast, AbstractSpellPart spell, Position cast_position, World world, EntityLivingBase shooter){
//        this((CastMethod) cast, (EffectType) spell, cast_position, world, shooter);
//    }
//
//    public SpellResolver(String castTag, String spellTag, Position cast_position, World world, EntityLivingBase shooter){
//        this(CraftedMagicAPI.craftedMagicAPI.spell_map.get(castTag), CraftedMagicAPI.craftedMagicAPI.spell_map.get(spellTag), cast_position, world, shooter);
//    }



    public void onCast(Position pos, World world, LivingEntity shooter){
        castType.onCast(pos, world, shooter);
        //effectType.onCast();
    }


    public void onResolveEffect(World world, LivingEntity shooter, RayTraceResult result){
        for(int i = 0; i < spell_recipe.size(); i++){
            AbstractSpellPart spell = spell_recipe.get(i);
            if(spell instanceof EffectType){

                ArrayList<EnhancementType> enhancements = new ArrayList<>();
                for(int j = i + 1; j < spell_recipe.size(); j++){
                    AbstractSpellPart next_spell = spell_recipe.get(j);
                    if(next_spell instanceof EnhancementType){
                        System.out.println("Applying enhancement ");
                        enhancements.add((EnhancementType) next_spell);
                    }else{
                        break;
                    }
                }
                ((EffectType) spell).onResolve(result, world, shooter, enhancements);
            }
        }
    }

    public CompoundNBT getNBTTag(){
        CompoundNBT tag = new CompoundNBT();
        tag.putString("cast_tag", this.castType.getTag());
        for(int i=0; i<spell_recipe.size(); i++){
            tag.putString("recipe_spell_"+i, spell_recipe.get(i).getTag());
        }
        return tag;
    }


}
