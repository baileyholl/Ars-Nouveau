package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.SummonHorse;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class EffectSummonSteed extends AbstractEffect {


    public EffectSummonSteed() {
        super(GlyphLib.EffectSummonSteedID, "Summon Steed");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolve(rayTraceResult, world, shooter, augments, spellContext);
        int ticks = 60 * 20 * (5 + 2* getDurationModifier(augments));
        if(!canSummon(shooter))
            return;
        Vector3d hit = rayTraceResult.getLocation();
        SummonHorse horse = new SummonHorse(ModEntities.SUMMON_HORSE, world);
      //  wolf.ticksLeft = 400;
        horse.setPos(hit.x(), hit.y(), hit.z());
        horse.ticksLeft = ticks;
        horse.tameWithName((PlayerEntity) shooter);
        world.addFreshEntity(horse);
        horse.getHorseInventory().setItem(0, new ItemStack(Items.SADDLE));
        //horse.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.DIAMOND_HORSE_ARMOR));
        horse.setDropChance(EquipmentSlotType.CHEST, 0.0F);
        applySummoningSickness(shooter, ticks/2);
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }


    @Override
    public String getBookDescription() {
        return "Summons a saddled horse that will vanish after a few minutes. Extend Time will increase the duration of the summon. Applies Summoning Sickness to the caster, and cannot be cast while afflicted by this Sickness.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.SADDLE;
    }
}
