package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.List;


import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class EffectEnderChest extends AbstractEffect {
    private static final ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.enderchest");

    public EffectEnderChest() {
        super(GlyphLib.EffectEnderChestID, "Access Ender Inventory");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(shooter instanceof PlayerEntity && !(shooter instanceof FakePlayer)){
            EnderChestInventory chestInventory = ((PlayerEntity)shooter).getEnderChestInventory();
            ((PlayerEntity) shooter).openMenu(new SimpleNamedContainerProvider((p_226928_1_, p_226928_2_, p_226928_3_) -> {
                return ChestContainer.threeRows(p_226928_1_, p_226928_2_, chestInventory);
            }, CONTAINER_NAME));
        }

    }

    @Override
    public String getBookDescription() {
        return "Opens your personal ender chest inventory from anywhere.";
    }

    @Override
    public int getManaCost() {
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
}
