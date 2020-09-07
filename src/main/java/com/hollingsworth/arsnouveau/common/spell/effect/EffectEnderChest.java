package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.block.EnderChestBlock.CONTAINER_NAME;

public class EffectEnderChest extends AbstractEffect {
    public EffectEnderChest() {
        super(ModConfig.EffectEnderChestID, "Access Ender Inventory");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(shooter instanceof PlayerEntity){
            EnderChestInventory chestInventory = ((PlayerEntity)shooter).getInventoryEnderChest();
//            chestInventory.openInventory((PlayerEntity) shooter);
            ((PlayerEntity) shooter).openContainer(new SimpleNamedContainerProvider((p_226928_1_, p_226928_2_, p_226928_3_) -> {
                return ChestContainer.createGeneric9X3(p_226928_1_, p_226928_2_, chestInventory);
            }, CONTAINER_NAME));
        }

    }

    @Override
    protected String getBookDescription() {
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
