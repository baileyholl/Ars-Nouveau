package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectGlide extends AbstractEffect {

    public static EffectGlide INSTANCE = new EffectGlide();

    private EffectGlide() {
        super(GlyphLib.EffectGlideID, "Glide");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(rayTraceResult.getEntity() instanceof LivingEntity){
            applyConfigPotion(((LivingEntity) rayTraceResult.getEntity()), ModPotions.GLIDE_EFFECT, spellStats);
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 180);
        addExtendTimeConfig(builder, 120);
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public String getBookDescription() {
        return "Grants the Glide effect, allowing the wearer to fly as if they were wearing an Elytra. ";
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.ELYTRA;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        // ModPotions.GLIDE_EFFECT does not respond to amplification
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_AIR);
    }
}
