package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.AbstractFilter;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ANExampleFilter extends AbstractFilter {

    public static ANExampleFilter INSTANCE = new ANExampleFilter(GlyphLib.EffectExampleFilter,"Example Filter");

    public ANExampleFilter(String registryName, String name) {
        super(new ResourceLocation(ArsNouveau.MODID, registryName), name);
    }

    @Override
    public boolean shouldResolveOnBlock(BlockHitResult target) {
        return false;
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target) {
        return target.getEntity() instanceof Mob mob && mob.getMobType() == MobType.UNDEAD;
    }
}
