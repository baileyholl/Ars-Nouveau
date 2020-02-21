package com.hollingsworth.craftedmagic.spell;

import com.hollingsworth.craftedmagic.api.spell.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.util.ManaUtil;
import com.hollingsworth.craftedmagic.capability.ManaCapability;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.spell.AbstractCastMethod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hollingsworth.craftedmagic.api.util.SpellRecipeUtil.getAugments;

public class SpellResolver {
    private AbstractCastMethod castType;
//    private EffectType effectType;

    private ArrayList<AbstractSpellPart> spell_recipe;
    
    public SpellResolver(AbstractCastMethod cast, ArrayList<AbstractSpellPart> spell_recipe){
        this.castType = cast;
        this.spell_recipe = spell_recipe;
        if(castType != null)
            this.castType.resolver = this;
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

    public boolean canCast(PlayerEntity entity){
        if(spell_recipe.isEmpty() || castType == null) {
            entity.sendMessage(new StringTextComponent("Invalid Spell."));
            return false;
        }

        int totalCost = ManaUtil.calculateCost(spell_recipe);
        AtomicBoolean canCast = new AtomicBoolean(false);
        ManaCapability.getMana(entity).ifPresent(mana -> {
            canCast.set(totalCost <= mana.getCurrentMana() || entity.isCreative());

        });
        return canCast.get();
    }

    public void onCast(ItemStack stack, PlayerEntity playerEntity, World world){
        if(canCast(playerEntity))
            castType.onCast(stack, playerEntity, world, getAugments(spell_recipe, 0));
    }

    public void onCastOnBlock(ItemUseContext context){
        if(canCast(context.getPlayer()))
            castType.onCastOnBlock(context, getAugments(spell_recipe, 0));
    }

    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand){
        if(canCast(playerIn))
            castType.onCastOnEntity(stack, playerIn, target, hand, getAugments(spell_recipe, 0));
    }



    public void onResolveEffect(World world, LivingEntity shooter, RayTraceResult result){
        for(int i = 0; i < spell_recipe.size(); i++){
            AbstractSpellPart spell = spell_recipe.get(i);
            if(spell instanceof AbstractEffect){
                ((AbstractEffect) spell).onResolve(result, world, shooter, getAugments(spell_recipe, i));
            }
        }
    }

    public void expendMana(LivingEntity entity){
        int totalCost = ManaUtil.calculateCost(spell_recipe);
        ManaCapability.getMana(entity).ifPresent(mana -> {
            mana.removeMana(totalCost);
            System.out.println("Expending  Mana");
        });
    }
}
