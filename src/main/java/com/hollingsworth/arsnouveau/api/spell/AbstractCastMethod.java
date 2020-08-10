package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.common.spell.SpellResolver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class AbstractCastMethod extends AbstractSpellPart {
    public SpellResolver resolver;

    public abstract void onCast(ItemStack stack, PlayerEntity playerEntity, World world, ArrayList<AbstractAugment> augments);

    public abstract void onCastOnBlock(ItemUseContext context, ArrayList<AbstractAugment> augments);

    public abstract void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand, ArrayList<AbstractAugment> augments);

    public AbstractCastMethod(String tag, String description){
        super(tag,description);
    }


}
