package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectCraft extends AbstractEffect {

    public static EffectCraft INSTANCE = new EffectCraft();

    private EffectCraft() {
        super(GlyphLib.EffectCraftID, "Craft");
    }

    private static final Component CONTAINER_NAME = Component.translatable("container.crafting");

    @Override
    public void onResolve(HitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (shooter instanceof Player playerEntity && isNotFakePlayer(shooter)) {
            playerEntity.openMenu(new SimpleMenuProvider((id, inventory, player) -> new CustomWorkbench(id, inventory, ContainerLevelAccess.create(player.getCommandSenderWorld(), player.blockPosition())), CONTAINER_NAME));
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    public static class CustomWorkbench extends CraftingMenu {

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

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Opens the crafting menu.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
