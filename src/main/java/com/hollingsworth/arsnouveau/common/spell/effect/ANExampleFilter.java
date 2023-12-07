package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.AbstractFilter;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.validation.ContextSpellValidator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ANExampleFilter extends AbstractFilter {

    public static ANExampleFilter INSTANCE = new ANExampleFilter("example_filter","Example Filter");

    public ANExampleFilter(String registryName, String name) {
        super(new ResourceLocation(ArsNouveau.MODID, registryName), name);
        ContextSpellValidator.RegisterContextCreator(this);
    }

    @Override
    public boolean shouldResolveOnBlock(BlockHitResult target, Level world) {
        return false;
    }

    @Override
    public boolean shouldResolveOnEntity(EntityHitResult target, Level world) {
        return target.getEntity() instanceof Mob mob && mob.getMobType() == MobType.UNDEAD;
    }
}