package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

public class EffectName extends AbstractEffect {

    public static EffectName INSTANCE = new EffectName();

    private EffectName() {
        super(GlyphLib.EffectNameID, "Name");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Component newName = null;
        InventoryManager manager = spellContext.getCaster().getInvManager();
        SlotReference slotRef = manager.findItem(i -> i.getItem() == Items.NAME_TAG, InteractType.EXTRACT);
        if(slotRef.getHandler() != null){
            ItemStack stack = slotRef.getHandler().getStackInSlot(slotRef.getSlot());
            newName = stack.getDisplayName().plainCopy();
        }
        if (newName == null && isRealPlayer(shooter) && shooter instanceof Player player) {
            ItemStack stack = StackUtil.getHeldCasterToolOrEmpty(player);
            if (stack != ItemStack.EMPTY && stack.getTag() != null) {
                ISpellCaster caster = CasterUtil.getCaster(stack);
                newName = Component.literal(caster.getSpellName());
            }
        }
        rayTraceResult.getEntity().setCustomName(newName);
        if (rayTraceResult.getEntity() instanceof Mob mob) {
            mob.setPersistenceRequired();
        } else if (rayTraceResult.getEntity() instanceof ItemEntity item) {
            item.getItem().setHoverName(newName);
        }
    }

    public SpellTier getTier() {
        return SpellTier.TWO;
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public int getDefaultManaCost() {
        return 25;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Names an entity after the set Spell Name. Can be overridden with a name tag in the hotbar.";
    }
}
