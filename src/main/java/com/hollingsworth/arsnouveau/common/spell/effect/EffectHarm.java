package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectHarm extends AbstractEffect {
    public static EffectHarm INSTANCE = new EffectHarm();

    private EffectHarm() {super(GlyphLib.EffectHarmID, "Harm" ); }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        double damage = DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
        Entity entity = rayTraceResult.getEntity();
        int time = (int) spellStats.getDurationMultiplier();
        if(time > 0){
            if(entity instanceof LivingEntity)
                applyConfigPotion((LivingEntity) entity, Effects.POISON, spellStats);
        }else{
            dealDamage(world, shooter, (float) damage, spellStats, entity, DamageSource.playerAttack(getPlayer(shooter, (ServerWorld) world)));
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 5.0);
        addAmpConfig(builder, 2.0);
        addPotionConfig(builder, 5);
        addExtendTimeConfig(builder, 5);
    }

    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof EntityRayTraceResult;
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.IRON_SWORD;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentFortune.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "A spell you start with. Damages a target. May be increased by Amplify, or applies the Poison debuff when using Extend Time. Note, multiple Harms without a delay will not apply due to invincibility on hit.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
