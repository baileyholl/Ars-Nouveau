package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.items.data.CodexData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AnnotatedCodex extends ModItem {

    public AnnotatedCodex(Properties properties) {
        super(properties);
    }

    public AnnotatedCodex() {
        this(ItemsRegistry.defaultItemProperties().stacksTo(1).component(DataComponentRegistry.CODEX_DATA, new CodexData(Optional.empty(), null, List.of())));
    }

    public int getUnlockLevelCost(Collection<AbstractSpellPart> spellParts) {
        int expPerGlyph = ServerConfig.CODEX_COST_PER_GLYPH.get();
        return ScribesTile.getLevelsFromExp(expPerGlyph * spellParts.size());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pPlayer.level.isClientSide)
            return super.use(pLevel, pPlayer, pUsedHand);
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        CodexData data = stack.get(DataComponentRegistry.CODEX_DATA);

        IPlayerCap playerCap = CapabilityRegistry.getPlayerDataCap(pPlayer);
        if (playerCap == null)
            return super.use(pLevel, pPlayer, pUsedHand);
        Collection<AbstractSpellPart> known = playerCap.getKnownGlyphs();
        Collection<AbstractSpellPart> storedGlyphs = data.glyphIds().stream().map(GlyphRegistry::getSpellPart).toList();

        if (!data.wasRecorded()) { // Player writing to codex
            int levelCost = getUnlockLevelCost(playerCap.getKnownGlyphs());
            int expCost = ScribesTile.getExperienceForLevel(levelCost);
            if (expCost > ScribesTile.getTotalPlayerExperience(pPlayer)) {
                PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("ars_nouveau.codex_not_enough_exp", levelCost));
            } else {
                var newData = new CodexData(pPlayer.getUUID(), pPlayer.getName().getString(), playerCap.getKnownGlyphs().stream().map(AbstractSpellPart::getRegistryName).toList());
                stack.set(DataComponentRegistry.CODEX_DATA, newData);
                PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("ars_nouveau.recorded_codex"));
                pPlayer.giveExperiencePoints(-expCost);
            }
        } else if (pPlayer.getUUID().equals(data.uuid())) { // Player updating codex
            Collection<AbstractSpellPart> difference = new ArrayList<>();
            for (AbstractSpellPart spellPart : known) {
                if (!storedGlyphs.contains(spellPart)) {
                    difference.add(spellPart);
                }
            }
            int levelCost = getUnlockLevelCost(difference);
            if (!difference.isEmpty()) {
                int expCost = ScribesTile.getExperienceForLevel(levelCost);
                if (expCost > ScribesTile.getTotalPlayerExperience(pPlayer)) {
                    PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("ars_nouveau.codex_not_enough_exp", levelCost));
                } else {
                    pPlayer.giveExperiencePoints(-expCost);
                    var newData = new CodexData(pPlayer.getUUID(), pPlayer.getName().getString(), playerCap.getKnownGlyphs().stream().map(AbstractSpellPart::getRegistryName).toList());
                    stack.set(DataComponentRegistry.CODEX_DATA, newData);

                    PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("ars_nouveau.updated_codex"));
                }
            } else {
                PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("ars_nouveau.codex_up_to_date"));
            }
        } else { // Player consuming codex
            int numUnlocked = 0;
            for (AbstractSpellPart storedPart : storedGlyphs) {
                if (!known.contains(storedPart)) {
                    playerCap.unlockGlyph(storedPart);
                    numUnlocked++;
                }
            }
            if (numUnlocked > 0) {
                stack.shrink(1);
                PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("ars_nouveau.consumed_codex", numUnlocked));
                CapabilityRegistry.EventHandler.syncPlayerCap(pPlayer);
            } else {
                PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("ars_nouveau.codex_no_use"));
            }
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        stack.addToTooltip(DataComponentRegistry.CODEX_DATA, context, tooltip2::add, flagIn);
    }
}
