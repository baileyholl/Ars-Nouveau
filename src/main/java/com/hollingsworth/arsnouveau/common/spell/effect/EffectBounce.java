package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectBounce extends AbstractEffect {

    public static EffectBounce INSTANCE = new EffectBounce();

    private EffectBounce() {
        super(GlyphLib.EffectBounceID, "Bounce");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext);
        if(rayTraceResult.getEntity() instanceof LivingEntity){
            applyConfigPotion((LivingEntity) rayTraceResult.getEntity(), ModPotions.BOUNCE_EFFECT, spellStats);
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 30);
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.SLIME_BALL;
    }


    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ABJURATION);
    }

    @Override
    public String getBookDescription() {
        return "Gives players the Bounce effect, causing them to bounce upwards upon falling. Amplification of Bounce will preserve additional forward facing motion per bounce.";
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return POTION_AUGMENTS;
    }
}
