package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;

public class EffectGlide extends AbstractEffect {

    public static EffectGlide INTANCE = new EffectGlide();

    private EffectGlide() {
        super(GlyphLib.EffectGlideID, "Glide");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, augments, spellContext);
        if(rayTraceResult.getEntity() instanceof LivingEntity){
            applyConfigPotion(((LivingEntity) rayTraceResult.getEntity()), ModPotions.GLIDE_EFFECT, augments);
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 60);
        addExtendTimeConfig(builder, 60);
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.ELYTRA;
    }
}
