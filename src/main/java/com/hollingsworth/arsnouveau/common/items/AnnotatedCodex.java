package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AnnotatedCodex extends ModItem{
    public AnnotatedCodex(Properties properties) {
        super(properties);
    }

    public AnnotatedCodex(Properties properties, String registryName) {
        super(properties, registryName);
    }

    public AnnotatedCodex(String registryName) {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1), registryName);
    }

    public int getUnlockLevelCost(Collection<AbstractSpellPart> spellParts){
        int expPerGlyph = Config.CODEX_COST_PER_GLYPH.get();
        return ScribesTile.getLevelsFromExp(expPerGlyph * spellParts.size());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pPlayer.level.isClientSide)
            return super.use(pLevel, pPlayer, pUsedHand);
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        CodexData data = new CodexData(stack);

        IPlayerCap playerCap = CapabilityRegistry.getPlayerDataCap(pPlayer).orElse(null);
        if(playerCap == null)
            return super.use(pLevel, pPlayer, pUsedHand);
        Collection<AbstractSpellPart> known = playerCap.getKnownGlyphs();
        Collection<AbstractSpellPart> storedGlyphs = data.getGlyphs();

        if(data.getPlayerID() == null){ // Player writing to codex
            int levelCost = getUnlockLevelCost(playerCap.getKnownGlyphs());
            int expCost = ScribesTile.getExperienceForLevel(levelCost);
            if(expCost > ScribesTile.getTotalPlayerExperience(pPlayer)){
                PortUtil.sendMessageNoSpam(pPlayer, new TranslatableComponent("ars_nouveau.codex_not_enough_exp", levelCost));
            }else {
                data.setGlyphs(playerCap.getKnownGlyphs());
                data.setPlayer(pPlayer);
                PortUtil.sendMessageNoSpam(pPlayer, new TranslatableComponent("ars_nouveau.recorded_codex"));
                pPlayer.giveExperiencePoints(-expCost);
            }
        }else if(pPlayer.getUUID().equals(data.getPlayerID())){ // Player updating codex
            Collection<AbstractSpellPart> difference = new ArrayList<>();
            for(AbstractSpellPart spellPart : known){
                if (!storedGlyphs.contains(spellPart)) {
                    difference.add(spellPart);
                }
            }
            int levelCost = getUnlockLevelCost(difference);
            if(!difference.isEmpty()) {
                int expCost = ScribesTile.getExperienceForLevel(levelCost);
                if(expCost > ScribesTile.getTotalPlayerExperience(pPlayer)){
                    PortUtil.sendMessageNoSpam(pPlayer, new TranslatableComponent("ars_nouveau.codex_not_enough_exp", levelCost));
                }else {
                    pPlayer.giveExperiencePoints(-expCost);
                    data.setGlyphs(playerCap.getKnownGlyphs());
                    PortUtil.sendMessageNoSpam(pPlayer, new TranslatableComponent("ars_nouveau.updated_codex"));
                }
            }else{
                PortUtil.sendMessageNoSpam(pPlayer, new TranslatableComponent("ars_nouveau.codex_up_to_date"));
            }
        }else{ // Player consuming codex
            int numUnlocked = 0;
            for(AbstractSpellPart storedPart : storedGlyphs){
                if(!known.contains(storedPart)){
                    playerCap.unlockGlyph(storedPart);
                    numUnlocked++;
                }
            }
            if(numUnlocked > 0){
                stack.shrink(1);
                PortUtil.sendMessageNoSpam(pPlayer, new TranslatableComponent("ars_nouveau.consumed_codex", numUnlocked));
                CapabilityRegistry.EventHandler.syncPlayerCap(pPlayer);
            }else{
                PortUtil.sendMessageNoSpam(pPlayer, new TranslatableComponent("ars_nouveau.codex_no_use"));
            }
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        CodexData data = new CodexData(stack);
        if(data.glyphs.isEmpty()){
            tooltip2.add(new TranslatableComponent("ars_nouveau.codex_tooltip"));
        }else{
            tooltip2.add(new TranslatableComponent("ars_nouveau.contains_glyphs", data.glyphs.size()));
        }
        if(data.playerName != null)
            tooltip2.add(new TranslatableComponent("ars_nouveau.recorded_by", data.playerName));
    }

    public static class CodexData{
        ItemStack stack;
        private List<AbstractSpellPart> glyphs = new ArrayList<>();
        private UUID playerID;
        public String playerName;

        public CodexData(ItemStack stack){
            this.stack = stack;
            CompoundTag tag = stack.getOrCreateTag();
            ArsNouveauAPI api = ArsNouveauAPI.getInstance();
            for(String s : NBTUtil.readStrings(tag, "glyph_")){
                if(api.getSpellpartMap().containsKey(s)){
                    glyphs.add(api.getSpellpartMap().get(s));
                }
            }
            playerName = tag.contains("playerName") ? tag.getString("playerName") : null;
            playerID = tag.hasUUID("player") ? tag.getUUID("player") : null;
        }

        public void setPlayer(Player player){
            this.playerID = player.getUUID();
            this.playerName = player.getName().getString();
            write();
        }

        public void setGlyphs(Collection<AbstractSpellPart> glyphs){
            this.glyphs = new ArrayList<>(glyphs);
            write();
        }

        public UUID getPlayerID(){
            return playerID;
        }

        public List<AbstractSpellPart> getGlyphs(){
            return glyphs;
        }

        public void write(){
            CompoundTag tag = new CompoundTag();
            NBTUtil.writeStrings(tag, "glyph_", glyphs.stream().map(s -> s.getId()).collect(Collectors.toList()));
            if(playerID != null){
                tag.putUUID("player", playerID);
            }
            if(playerName != null){
                tag.putString("playerName", playerName);
            }
            stack.setTag(tag);
        }
    }
}
