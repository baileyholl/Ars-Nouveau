package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class SummoningFocus extends ModItem implements ISpellModifierItem {
    public SummoningFocus(Properties properties) {
        super(properties);
    }

    public SummoningFocus(Properties properties, String registryName) {
        super(properties, registryName);
    }

    public SummoningFocus(String registryName) {
        super(registryName);
    }

    @Override
    public SpellStats.Builder applyItemModifiers(ItemStack stack, SpellStats.Builder builder, AbstractSpellPart spellPart, RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellContext spellContext) {
        builder.addDamageModifier(1.0f);
        return builder;
    }

    public SpellStats.Builder getSimpleStats(SpellStats.Builder builder){
        builder.addDamageModifier(1.0f);
        return builder;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        tooltip2.add(new TranslationTextComponent("tooltip.ars_nouveau.summon_focus"));
        getSimpleStats(new SpellStats.Builder()).build().addTooltip(tooltip2);
    }

    public static boolean containsThis(World world, ISummon summon){
        if(!world.isClientSide && summon.getOwner((ServerWorld) world) instanceof PlayerEntity) {
            IItemHandlerModifiable items = CuriosUtil.getAllWornItems((LivingEntity) summon.getOwner((ServerWorld) world)).orElse(null);
            if(items != null){
                for(int i = 0; i < items.getSlots(); i++){
                    Item item = items.getStackInSlot(i).getItem();
                    if(item instanceof SummoningFocus){
                       return true;
                    }

                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void summonedEvent(SummonEvent event){
        if(!event.world.isClientSide && SummoningFocus.containsThis(event.world, event.summon)){
            event.summon.setTicksLeft(event.summon.getTicksLeft() * 2);
            if (event.summon.getLivingEntity() != null) {
                event.summon.getLivingEntity().addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 500, 2));
                event.summon.getLivingEntity().addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 500, 1));
            }
        }
    }

    @SubscribeEvent
    public static void summonDeathEvent(SummonEvent.Death event){
        if(!event.world.isClientSide && SummoningFocus.containsThis(event.world, event.summon)){
            DamageSource source = event.source;
            if(source != null && source.getEntity() != null){
                source.getEntity().hurt(DamageSource.thorns(source.getEntity()).bypassArmor(), 5.0f);
            }
        }
    }
}
