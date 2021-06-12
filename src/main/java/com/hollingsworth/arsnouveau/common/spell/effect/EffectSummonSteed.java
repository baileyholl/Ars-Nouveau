package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.SummonHorse;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectSummonSteed extends AbstractEffect {
    public static EffectSummonSteed INSTANCE = new EffectSummonSteed();


    private EffectSummonSteed() {
        super(GlyphLib.EffectSummonSteedID, "Summon Steed");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolve(rayTraceResult, world, shooter, augments, spellContext);
        int ticks = 20 * (GENERIC_INT.get() +  EXTEND_TIME.get() * getDurationModifier(augments));

        if(!canSummon(shooter))
            return;
        Vector3d hit = rayTraceResult.getLocation();
        for(int i = 0; i < 1 + getBuffCount(augments, AugmentAOE.class); i++){
            SummonHorse horse = new SummonHorse(ModEntities.SUMMON_HORSE, world);
            horse.setPos(hit.x(), hit.y(), hit.z());
            horse.ticksLeft = ticks;
            horse.tameWithName((PlayerEntity) shooter);
            world.addFreshEntity(horse);
            horse.getHorseInventory().setItem(0, new ItemStack(Items.SADDLE));
            horse.setDropChance(EquipmentSlotType.CHEST, 0.0F);
        }
        applySummoningSickness(shooter, 30 * 20);
    }


    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addExtendTimeConfig(builder, 120);
        addGenericInt(builder, 300, "Base duration in seconds", "duration");
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAOE.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Summons a saddled horse that will vanish after a few minutes. AOE will increase the amount summoned, while Extend Time will increase the duration of the summon. Applies Summoning Sickness to the caster, and cannot be cast while afflicted by this Sickness.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.LEATHER;
    }
}
