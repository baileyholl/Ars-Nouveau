package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class EnchantedMageblock extends EnchantedFallingBlock {
    public EnchantedMageblock(EntityType<? extends ColoredProjectile> p_31950_, Level p_31951_) {
        super(p_31950_, p_31951_);
    }

    public EnchantedMageblock(Level level, double v, int y, double v1, BlockState blockState) {
        super(level, v, y, v1, blockState);
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntities.ENCHANTED_MAGE_BLOCK.get();
    }
}
