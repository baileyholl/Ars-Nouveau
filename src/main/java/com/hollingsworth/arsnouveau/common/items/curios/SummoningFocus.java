package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class SummoningFocus extends ArsNouveauCurio implements ISpellModifierItem {
    public SummoningFocus() {
        super();
    }

    public static List<AbstractCastMethod> sympatheticMethods = new ArrayList<>();

    static {
        sympatheticMethods.add(MethodSelf.INSTANCE);
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        builder.addDamageModifier(1.0f);
        return builder;
    }

    public static boolean containsThis(Level world, Entity entity) {
        if (!world.isClientSide && entity instanceof Player) {
            IItemHandlerModifiable items = CuriosUtil.getAllWornItems((LivingEntity) entity).orElse(null);
            if (items != null) {
                for (int i = 0; i < items.getSlots(); i++) {
                    Item item = items.getStackInSlot(i).getItem();
                    if (item instanceof SummoningFocus) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void summonedEvent(SummonEvent event) {
        if (!event.world.isClientSide && SummoningFocus.containsThis(event.world, event.summon.getOwner())) {
            event.summon.setTicksLeft(event.summon.getTicksLeft() * 2);
            if (event.summon.getLivingEntity() != null) {
                event.summon.getLivingEntity().addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 500, 2));
                event.summon.getLivingEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 500, 1));
            }
        }
    }

    @SubscribeEvent
    public static void castSpell(SpellCastEvent event) {
        if (!event.getWorld().isClientSide && event.getEntity() instanceof Player && SummoningFocus.containsThis(event.getWorld(), event.getEntity())) {
            if (event.spell.getCastMethod() != null && sympatheticMethods.contains(event.spell.getCastMethod())) {
                for (LivingEntity i : event.getWorld().getEntitiesOfClass(LivingEntity.class, new AABB(event.getEntity().blockPosition()).inflate(30), ISummon.class::isInstance)) {
                    if (event.getEntity().equals(((ISummon) i).getOwnerAlt())) {
                        EntitySpellResolver spellResolver = new EntitySpellResolver(event.context.clone().withWrappedCaster(new LivingCaster(i)));
                        spellResolver.onCast(ItemStack.EMPTY, i.level);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void summonDeathEvent(SummonEvent.Death event) {
        if (!event.world.isClientSide && SummoningFocus.containsThis(event.world, event.summon.getOwnerAlt())) {
            DamageSource source = event.source;
            if (source != null && source.getEntity() != null && source.getEntity() != event.summon.getOwnerAlt()) {
                source.getEntity().hurt(source.getEntity().level.damageSources().thorns(source.getEntity()), 5.0f);
            }
        }
    }
}
