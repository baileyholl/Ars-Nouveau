package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.HashMap;

public abstract class AbstractCastMethod extends AbstractSpellPart {

    public AbstractCastMethod(String tag, String description) {
        super(tag, description);
    }

    public AbstractCastMethod(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public Integer getTypeIndex() {
        return 1;
    }


    /**
     * Called when the spell is cast on nothing. In context of items, this is called when the player right clicks air.
     */
    public abstract CastResolveType onCast(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, SpellStats spellStats, SpellContext context, SpellResolver resolver);

    /**
     * Called when the spell is cast on a block.
     */
    public abstract CastResolveType onCastOnBlock(UseOnContext context, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver);

    /**
     * Called when the spell is cast on a block.
     */
    public abstract CastResolveType onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver);

    /**
     * Called when the spell is cast on an entity.
     **/
    public abstract CastResolveType onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver);

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        super.buildAugmentLimitsConfig(builder, getDefaultAugmentLimits(new HashMap<>()));
    }
}
