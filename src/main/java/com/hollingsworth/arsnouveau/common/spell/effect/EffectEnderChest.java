package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectEnderChest extends AbstractEffect {
    public static EffectEnderChest INSTANCE = new EffectEnderChest();

    private static final Component CONTAINER_NAME = Component.translatable("container.enderchest");

    private EffectEnderChest() {
        super(GlyphLib.EffectEnderChestID, "Access Ender Inventory");
    }


    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (shooter instanceof Player player && !(shooter instanceof FakePlayer)) {
            PlayerEnderChestContainer chestInventory = player.getEnderChestInventory();
            player.openMenu(new SimpleMenuProvider((p_226928_1_, p_226928_2_, p_226928_3_) -> ChestMenu.threeRows(p_226928_1_, p_226928_2_, chestInventory), CONTAINER_NAME));
        }
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Opens your personal ender chest inventory from anywhere.";
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
