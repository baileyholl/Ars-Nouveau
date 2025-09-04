package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.client.gui.SchoolTooltip;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class Glyph extends ModItem {
    public AbstractSpellPart spellPart;

    public Glyph(AbstractSpellPart part) {
        super(new Item.Properties());
        this.spellPart = part;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        if (worldIn.isClientSide)
            return super.use(worldIn, playerIn, handIn);

        if (!Config.isGlyphEnabled(this.spellPart.getRegistryName())) {
            playerIn.sendSystemMessage(Component.translatable("ars_nouveau.spell.disabled"));
            return super.use(worldIn, playerIn, handIn);
        }
        IPlayerCap playerDataCap = CapabilityRegistry.getPlayerDataCap(playerIn);
        if (playerDataCap != null) {
            if (playerDataCap.knowsGlyph(spellPart) || GlyphRegistry.getDefaultStartingSpells().contains(spellPart)) {
                playerIn.sendSystemMessage(Component.translatable("ars_nouveau.already_learned"));
                return super.use(worldIn, playerIn, handIn);
            } else if (playerDataCap.unlockGlyph(spellPart)) {
                CapabilityRegistry.EventHandler.syncPlayerCap(playerIn);
                if (!playerIn.hasInfiniteMaterials()) {
                    playerIn.getItemInHand(handIn).shrink(1);
                }
                playerIn.sendSystemMessage(Component.translatable("ars_nouveau.learn_glyph", Component.translatable(spellPart.getLocalizationKey()).getString()));
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return Component.translatable("ars_nouveau.glyph_of", this.spellPart.getLocaleName());
    }

    public @NotNull Component getName() {
        return getName(ItemStack.EMPTY);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        if (spellPart == null)
            return;

        if (!Config.isGlyphEnabled(this.spellPart.getRegistryName())) {
            tooltip2.add(Component.translatable("tooltip.ars_nouveau.glyph_disabled"));
        } else if (spellPart != null) {
            tooltip2.add(Component.translatable("tooltip.ars_nouveau.glyph_level", spellPart.getConfigTier().value).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
            if (Screen.hasShiftDown() && !spellPart.spellSchools.isEmpty()) {
                tooltip2.add(Component.translatable("ars_nouveau.schools"));
                for (SpellSchool s : spellPart.spellSchools) {
                    tooltip2.add(s.getTextComponent());
                }
            }
        }
        var player = ArsNouveau.proxy.getPlayer();
        if (player == null)
            return;
        IPlayerCap playerDataCap = CapabilityRegistry.getPlayerDataCap(player);
        if (playerDataCap != null) {
            if (playerDataCap.knowsGlyph(spellPart) || GlyphRegistry.getDefaultStartingSpells().contains(spellPart)) {
                tooltip2.add(Component.translatable("tooltip.ars_nouveau.glyph_known").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN)));
            } else {
                tooltip2.add(Component.translatable("tooltip.ars_nouveau.glyph_unknown").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
            }
        }
        if (flagIn.hasShiftDown()) {
            tooltip2.add(spellPart.getBookDescLang());
        } else {
            tooltip2.add(Component.translatable("tooltip.ars_nouveau.hold_shift", Component.keybind("key.sneak")));
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        if (!Screen.hasShiftDown() && spellPart != null && !spellPart.spellSchools.isEmpty()) {
            return Optional.of(new SchoolTooltip(spellPart, true));
        }
        return Optional.empty();
    }
}
