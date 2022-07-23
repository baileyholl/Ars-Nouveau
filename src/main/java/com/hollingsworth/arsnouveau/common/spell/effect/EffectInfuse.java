package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectInfuse extends AbstractEffect {
    public static EffectInfuse INSTANCE = new EffectInfuse(GlyphLib.EffectInfuseID, "Infuse");

    public EffectInfuse(String tag, String description) {
        super(tag, description);
    }

    public EffectInfuse(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        if (rayTraceResult.getEntity() instanceof LivingEntity livingEntity) {
            ItemStack flask = getItemFromCaster(shooter, spellContext, (i) -> {
                PotionFlask.FlaskData data = new PotionFlask.FlaskData(i);
                return !data.getPotion().isEmpty() && data.getCount() > 0;
            });
            if (!flask.isEmpty()) {
                PotionFlask.FlaskData data = new PotionFlask.FlaskData(flask);
                data.apply(shooter, shooter, livingEntity);
                data.setCount(data.getCount() - 1);
            } else {
                ItemStack potion = getItemFromCaster(shooter, spellContext, (i) -> {
                    return i.getItem() instanceof PotionItem;
                });
                if (!potion.isEmpty()) {
                    PotionData potionData = new PotionData(potion);
                    potionData.applyEffects(shooter, shooter, livingEntity);
                    potion.shrink(1);
                    insertStackToCaster(shooter, spellContext, new ItemStack(Items.GLASS_BOTTLE));
                }
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @NotNull
    @Override
    protected Set<AbstractAugment> getCompatibleAugments() {
        return setOf();
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Infuses a target with a potion from your flask or held potions. Consumes the potion.";
    }
}
