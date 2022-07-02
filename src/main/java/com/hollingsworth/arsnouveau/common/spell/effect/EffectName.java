package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nonnull;
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
        if (spellContext.castingTile instanceof IInventoryResponder) {
            newName = (((IInventoryResponder) spellContext.castingTile).getItem(new ItemStack(Items.NAME_TAG))).getDisplayName().plainCopy();
        } else if (shooter instanceof Player playerEntity) {
            NonNullList<ItemStack> list = playerEntity.inventory.items;
            for (int i = 0; i < 9; i++) {
                ItemStack stack = list.get(i);
                if (stack.getItem() == Items.NAME_TAG) {
                    newName = stack.getDisplayName().plainCopy();
                    break;
                }
            }
            if (newName == null) {
                ItemStack stack = StackUtil.getHeldSpellbook(playerEntity);
                if (stack != ItemStack.EMPTY && stack.getItem() instanceof SpellBook && stack.getTag() != null) {
                    ISpellCaster caster = CasterUtil.getCaster(stack);
                    newName = Component.literal(caster.getSpellName());
                }
            }
        }
        rayTraceResult.getEntity().setCustomName((newName != null) ? newName : Component.empty());
        if (rayTraceResult.getEntity() instanceof Mob) {
            ((Mob) rayTraceResult.getEntity()).setPersistenceRequired();
        }

    }

    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public int getDefaultManaCost() {
        return 25;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Names an entity after the set Spell Name. Can be overridden with a name tag in the hotbar.";
    }
}
