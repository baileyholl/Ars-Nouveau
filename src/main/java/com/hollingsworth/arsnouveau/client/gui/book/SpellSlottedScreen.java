package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.gui.GuiUtils;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiSpellSlot;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class SpellSlottedScreen extends BaseBook{
    public AbstractCaster<?> caster;
    public GuiSpellSlot selectedSlotButton;
    public int selectedSpellSlot = 0;
    public InteractionHand hand;
    public ItemStack bookStack;
    public Player player;
    public Minecraft mc;
    IPlayerCap playerCap;


    public SpellSlottedScreen(InteractionHand hand) {
        super();
        this.hand = hand;
        mc = Minecraft.getInstance();
        player = mc.player;
        playerCap = CapabilityRegistry.getPlayerDataCap(player);
        bookStack = player.getItemInHand(hand);
        this.caster = SpellCasterRegistry.from(bookStack);
        selectedSpellSlot = caster.getCurrentSlot();
    }

    @Override
    public void init() {
        super.init();
    }

    public void onBookstackUpdated(ItemStack stack) {
        this.bookStack = stack;
        this.caster = SpellCasterRegistry.from(stack);
    }

    public void initSpellSlots(Consumer<GuiSpellSlot> onSlotChanged){
        for (int i = 0; i < caster.getMaxSlots(); i++) {
            String name = caster.getSpellName(i);
            GuiSpellSlot slot = new GuiSpellSlot(bookLeft + 281, bookTop - 1 + 15 * (i + 1), i, name, (b) ->{
                if(!(b instanceof GuiSpellSlot button) || this.selectedSpellSlot == button.slotNum) {
                    return;
                }
                this.selectedSlotButton.isSelected = false;
                this.selectedSlotButton = button;
                button.isSelected = true;
                this.selectedSpellSlot = this.selectedSlotButton.slotNum;
                onSlotChanged.accept(selectedSlotButton);
            });

            if (i == selectedSpellSlot) {
                selectedSlotButton = slot;
                slot.isSelected = true;
            }else{
                slot.isSelected = false;
            }
            addRenderableWidget(slot);
        }
    }

    @Override
    protected TooltipComponent getClientImageTooltip(int mouseX, int mouseY) {
        for (Renderable renderable : renderables) {
            if(renderable instanceof AbstractWidget widget && !GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)){
                continue;
            }
            if(renderable instanceof GuiSpellSlot spellSlot){
                Spell spell = caster.getSpell(spellSlot.slotNum);
                if(spell.isEmpty()){
                    return null;
                }
                return new SpellTooltip(spell, false);
            }
        }
        return super.getClientImageTooltip(mouseX, mouseY);
    }
}
