package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectCraft extends AbstractEffect {

    public static EffectCraft INSTANCE = new EffectCraft();

    private EffectCraft() {
        super(GlyphLib.EffectCraftID, "Craft");
    }

    private static final Component CONTAINER_NAME = new TranslatableComponent("container.crafting");

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(shooter instanceof Player && isRealPlayer(shooter)){
            Player playerEntity = (Player) shooter;
            playerEntity.openMenu(new SimpleMenuProvider((id, inventory, player) -> new CustomWorkbench(id, inventory, ContainerLevelAccess.create(player.getCommandSenderWorld(), player.blockPosition())), CONTAINER_NAME));
        }
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    public static class CustomWorkbench extends CraftingMenu{

        public CustomWorkbench(int id, Inventory playerInventory) {
            super(id, playerInventory);
        }

        public CustomWorkbench(int id, Inventory playerInventory, ContainerLevelAccess p_i50090_3_) {
            super(id, playerInventory, p_i50090_3_);
        }

        @Override
        public boolean stillValid(Player playerIn) {
            return true;
        }
    }

    @Override
    public Item getCraftingReagent() {
        return Items.CRAFTING_TABLE;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Opens the crafting menu.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
