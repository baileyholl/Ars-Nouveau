package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.ISpellCasterProvider;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An interface for caster items that provides default behavior for scribing, displaying mana, and tooltips
 */
// TODO 1.20: Split ISpellHotkeyListener out
public interface ICasterTool extends IScribeable, IDisplayMana, ISpellHotkeyListener, ISpellCasterProvider {
    @Override
    default boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack tableStack) {
        ItemStack heldStack = player.getItemInHand(handIn);
        ISpellCaster thisCaster = CasterUtil.getCaster(tableStack);
        if (!((heldStack.getItem() instanceof SpellBook) || (heldStack.getItem() instanceof SpellParchment) || (heldStack.getItem() == ItemsRegistry.MANIPULATION_ESSENCE.asItem())))
            return false;
        boolean success;

        Spell spell = new Spell();
        if (heldStack.getItem() instanceof ICasterTool) {
            ISpellCaster heldCaster = CasterUtil.getCaster(heldStack);
            spell = heldCaster.getSpell();
            thisCaster.setColor(heldCaster.getColor());
            thisCaster.setFlavorText(heldCaster.getFlavorText());
            thisCaster.setSpellName(heldCaster.getSpellName());
            thisCaster.setSound(heldCaster.getCurrentSound());
        } else if (heldStack.getItem() == ItemsRegistry.MANIPULATION_ESSENCE.asItem()) {
            // Thanks mojang
            String[] words = new String[]{"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale", "phnglui", "mglwnafh", "cthulhu", "rlyeh", "wgahnagl", "fhtagn", "baguette"};
            // Pick between 3 and 5 words
            int numWords = world.random.nextInt(3) + 3;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numWords; i++) {
                sb.append(words[world.random.nextInt(words.length)]).append(" ");
            }
            thisCaster.setSpellHidden(true);
            thisCaster.setHiddenRecipe(sb.toString());
            PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.spell_hidden"));
            return true;
        }
        if (isScribedSpellValid(thisCaster, player, handIn, tableStack, spell)) {
            success = setSpell(thisCaster, player, handIn, tableStack, spell);
            if (success) {
                sendSetMessage(player);
                return true;
            }
        } else {
            sendInvalidMessage(player);
        }
        return false;
    }

    default void sendSetMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.set_spell"));
    }

    default void sendInvalidMessage(Player player) {
        PortUtil.sendMessageNoSpam(player, Component.translatable("ars_nouveau.invalid_spell"));
    }

    default @NotNull ISpellCaster getSpellCaster(ItemStack stack) {
        return new SpellCaster(stack);
    }

    @Override
    default ISpellCaster getSpellCaster() {
        return getSpellCaster(new CompoundTag());
    }

    @Override
    default ISpellCaster getSpellCaster(CompoundTag tag) {
        return new SpellCaster(tag);
    }

    default boolean setSpell(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        caster.setSpell(spell);
        return true;
    }

    default boolean isScribedSpellValid(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell) {
        return spell.isValid();
    }

    @Override
    default boolean shouldDisplay(ItemStack stack) {
        return true;
    }

    default void getInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        ISpellCaster caster = getSpellCaster(stack);

        if (caster.getSpell().isEmpty()) {
            tooltip2.add(Component.translatable("ars_nouveau.tooltip.can_inscribe"));
            return;
        }
        if (!caster.getSpellName().isEmpty()) {
            tooltip2.add(Component.literal(caster.getSpellName()));
        }
        if (caster.isSpellHidden()) {
            tooltip2.add(Component.literal(caster.getHiddenRecipe()).withStyle(Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath("minecraft", "alt")).withColor(ChatFormatting.GOLD)));
        } else {
            Spell spell = caster.getSpell();
            tooltip2.add(Component.literal(spell.getDisplayString()));
        }
        if (!caster.getFlavorText().isEmpty())
            tooltip2.add(Component.literal(caster.getFlavorText()).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE)));
    }
}
