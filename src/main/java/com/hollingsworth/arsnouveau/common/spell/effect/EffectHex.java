package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectHex extends AbstractEffect {

    public EffectHex() {
        super(GlyphLib.EffectHexID, "Hex");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, augments, spellContext);
        Entity entity = rayTraceResult.getEntity();
        if(!(entity instanceof LivingEntity))
            return;
        applyPotionWithCap((LivingEntity) entity, ModPotions.HEX_EFFECT, augments, 30, 8, 5);
    }

    @Override
    public String getBookDescription() {
        return "Applies the Hex effect up to level 5. Hex increases any damage taken by a small amount while the user is afflicted by poison, wither, or fire. Additionally, Hex cuts the rate of Mana Regeneration and healing in half.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(new EffectWither());
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Override
    public int getManaCost() {
        return 100;
    }
}
