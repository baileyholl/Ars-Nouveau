package com.hollingsworth.craftedmagic.api.spell;

import com.hollingsworth.craftedmagic.spell.SpellResolver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class CastMethod extends AbstractSpellPart {
    public SpellResolver resolver;

    public abstract void onCast(ItemStack stack, PlayerEntity playerEntity, World world);

    public abstract void onCastOnBlock(ItemUseContext context);

    public abstract void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand);

    public CastMethod(String tag, String description){
        super(tag,description);
    }


}
