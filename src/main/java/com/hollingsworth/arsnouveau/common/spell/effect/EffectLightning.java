package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.LightningEntity;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectLightning extends AbstractEffect {
    public static EffectLightning INSTANCE = new EffectLightning();

    private EffectLightning() {
        super(GlyphLib.EffectLightningID, "Lightning");
    }


    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        Vector3d pos = safelyGetHitPos(rayTraceResult);
        LightningEntity lightningBoltEntity = new LightningEntity(ModEntities.LIGHTNING_ENTITY,world);
        lightningBoltEntity.setPos(pos.x(), pos.y(), pos.z());
        lightningBoltEntity.setCause(shooter instanceof ServerPlayerEntity ? (ServerPlayerEntity) shooter : null);
        lightningBoltEntity.amps = getAmplificationBonus(augments);
        lightningBoltEntity.extendTimes = getDurationModifier(augments);
        lightningBoltEntity.ampScalar = AMP_VALUE.get().floatValue();
        lightningBoltEntity.wetBonus = GENERIC_DOUBLE.get().floatValue();
        (world).addFreshEntity(lightningBoltEntity);
        int multiplier = 1;
        long power = 0;
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            for(ItemStack i : entity.getArmorSlots()){
                IEnergyStorage energyStorage = i.getCapability(CapabilityEnergy.ENERGY).orElse(null);
                System.out.println(energyStorage);
                if(energyStorage != null){
                    multiplier++;
                    power += energyStorage.getEnergyStored();
                }
            }
        }
        lightningBoltEntity.damage = DAMAGE.get().floatValue();
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 5.0);
        addAmpConfig(builder, 3.0);
        addGenericDouble(builder, 2.0, "Bonus damage for wet entities", "wet_bonus");
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.HEART_OF_THE_SEA;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Summons a lightning bolt at the location. Entities struck will be given the Shocked effect. Shocked causes all additional lightning damage to deal bonus damage, and increases the level of Shocked up to III. Lightning also deals bonus damage to entities that are wet or wearing RF powered items. " +
                "Can be augmented with Amplify, Dampen, and Extend Time.";
    }
}
