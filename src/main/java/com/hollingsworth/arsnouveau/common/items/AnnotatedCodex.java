package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AnnotatedCodex extends ModItem {

    public AnnotatedCodex(Properties properties) {
        super(properties);
    }

    public AnnotatedCodex() {
        this(ItemsRegistry.defaultItemProperties().stacksTo(1));
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
        CodexData data = new CodexData(stack);

        IPlayerCap playerCap = CapabilityRegistry.getPlayerDataCap(pPlayer).orElse(null);
        if (playerCap == null)
            return super.use(pLevel, pPlayer, pUsedHand);
        Collection<AbstractSpellPart> known = playerCap.getKnownGlyphs();
        Collection<AbstractSpellPart> storedGlyphs = data.getGlyphs();

        if (data.getPlayerID() == null) { // Player writing to codex
            int levelCost = getUnlockLevelCost(playerCap.getKnownGlyphs());
            int expCost = ScribesTile.getExperienceForLevel(levelCost);
            if (expCost > ScribesTile.getTotalPlayerExperience(pPlayer)) {
                PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("ars_nouveau.codex_not_enough_exp", levelCost));
            } else {
                data.setGlyphs(playerCap.getKnownGlyphs());
                data.setPlayer(pPlayer);
                PortUtil.sendMessageNoSpam(pPlayer, Component.translatable("ars_nouveau.recorded_codex"));
                pPlayer.giveExperiencePoints(-expCost);
            }
        } else if (pPlayer.getUUID().equals(data.getPlayerID())) { // Player updating codex
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
                    data.setGlyphs(playerCap.getKnownGlyphs());
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        CodexData data = new CodexData(stack);
        if (data.glyphs.isEmpty()) {
            tooltip2.add(Component.translatable("ars_nouveau.codex_tooltip"));
        } else {
            tooltip2.add(Component.translatable("ars_nouveau.contains_glyphs", data.glyphs.size()));
        }
        if (data.playerName != null)
            tooltip2.add(Component.translatable("ars_nouveau.recorded_by", data.playerName));
    }

    public static class CodexData {
        ItemStack stack;
        private List<AbstractSpellPart> glyphs = new ArrayList<>();
        private UUID playerID;
        public String playerName;

        public CodexData(ItemStack stack) {
            this.stack = stack;
            CompoundTag tag = stack.getOrCreateTag();
            for (ResourceLocation s : NBTUtil.readResourceLocations(tag, "glyph_")) {
                if (GlyphRegistry.getSpellpartMap().containsKey(s)) {
                    glyphs.add(GlyphRegistry.getSpellpartMap().get(s));
                }
            }
            playerName = tag.contains("playerName") ? tag.getString("playerName") : null;
            playerID = tag.hasUUID("player") ? tag.getUUID("player") : null;
        }

        public void setPlayer(Player player) {
            this.playerID = player.getUUID();
            this.playerName = player.getName().getString();
            write();
        }

        public void setGlyphs(Collection<AbstractSpellPart> glyphs) {
            this.glyphs = new ArrayList<>(glyphs);
            write();
        }

        public UUID getPlayerID() {
            return playerID;
        }

        public List<AbstractSpellPart> getGlyphs() {
            return glyphs;
        }

        public void write() {
            CompoundTag tag = new CompoundTag();
            NBTUtil.writeResourceLocations(tag, "glyph_", glyphs.stream().map(AbstractSpellPart::getRegistryName).collect(Collectors.toList()));
            if (playerID != null) {
                tag.putUUID("player", playerID);
            }
            if (playerName != null) {
                tag.putString("playerName", playerName);
            }
            stack.setTag(tag);
        }
    }
}
