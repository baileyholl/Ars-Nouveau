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
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectHex extends AbstractEffect {
    public static EffectHex INSTANCE = new EffectHex();

    private EffectHex() {
        super(GlyphLib.EffectHexID, "Hex");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, augments, spellContext);
        Entity entity = rayTraceResult.getEntity();
        if(!(entity instanceof LivingEntity))
            return;
        applyPotionWithCap((LivingEntity) entity, ModPotions.HEX_EFFECT, augments, POTION_TIME.get(), EXTEND_TIME.get(), 5);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 30);
        addExtendTimeConfig(builder, 8);
    }

    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return POTION_AUGMENTS;
    }

    @Override
    public String getBookDescription() {
        return "Applies the Hex effect up to level 5. Hex increases any damage taken by a small amount while the user is afflicted by poison, wither, or fire. Additionally, Hex cuts the rate of Mana Regeneration and healing in half.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(EffectWither.INSTANCE);
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
