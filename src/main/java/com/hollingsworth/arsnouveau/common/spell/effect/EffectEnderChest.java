package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectEnderChest extends AbstractEffect {
    public static EffectEnderChest INSTANCE = new EffectEnderChest();

    private static final Component CONTAINER_NAME = new TranslatableComponent("container.enderchest");

    private EffectEnderChest() {
        super(GlyphLib.EffectEnderChestID, "Access Ender Inventory");
    }


    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(shooter instanceof Player && !(shooter instanceof FakePlayer)){
            PlayerEnderChestContainer chestInventory = ((Player)shooter).getEnderChestInventory();
            ((Player) shooter).openMenu(new SimpleMenuProvider((p_226928_1_, p_226928_2_, p_226928_3_) -> ChestMenu.threeRows(p_226928_1_, p_226928_2_, chestInventory), CONTAINER_NAME));
        }
    }
    @Nonnull
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

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.ENDER_CHEST;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
