package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.common.spell.method.MethodOrbit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
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

    public static List<AbstractCastMethod> sympatheticMethods = new ArrayList<>();

    static{
        sympatheticMethods.add(MethodSelf.INSTANCE);
        sympatheticMethods.add(MethodOrbit.INSTANCE);
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
//        tooltip2.add(new TranslationTextComponent("tooltip.ars_nouveau.summon_focus"));
//        getSimpleStats(new SpellStats.Builder()).build().addTooltip(tooltip2);
    }

    public static boolean containsThis(World world, Entity entity){
        if(!world.isClientSide && entity instanceof PlayerEntity) {
            IItemHandlerModifiable items = CuriosUtil.getAllWornItems((LivingEntity) entity).orElse(null);
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
        if(!event.world.isClientSide && SummoningFocus.containsThis(event.world, event.summon.getOwner((ServerWorld) event.world))){
            event.summon.setTicksLeft(event.summon.getTicksLeft() * 2);
            if (event.summon.getLivingEntity() != null) {
                event.summon.getLivingEntity().addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 500, 2));
                event.summon.getLivingEntity().addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 500, 1));
            }
        }
    }
    @SubscribeEvent
    public static void castSpell(SpellCastEvent event){
        if(!event.getWorld().isClientSide && event.getEntity() instanceof PlayerEntity &&  SummoningFocus.containsThis(event.getWorld(), event.getEntityLiving())){
            if(event.spell.getCastMethod() != null && sympatheticMethods.contains(event.spell.getCastMethod())){
                for(LivingEntity i : event.getWorld().getLoadedEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(event.getEntityLiving().blockPosition()).inflate(30), (l) -> l instanceof ISummon)){
                    if(event.getEntityLiving().equals(((ISummon) i).getOwner((ServerWorld) event.getWorld()))){
                        EntitySpellResolver spellResolver = new EntitySpellResolver(new SpellContext(event.spell, i).withColors(event.context.colors));
                        spellResolver.onCast(ItemStack.EMPTY, i, i.level);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void summonDeathEvent(SummonEvent.Death event){
        if(!event.world.isClientSide && SummoningFocus.containsThis(event.world, event.summon.getOwner((ServerWorld) event.world))){
            DamageSource source = event.source;
            if(source != null && source.getEntity() != null){
                source.getEntity().hurt(DamageSource.thorns(source.getEntity()).bypassArmor(), 5.0f);
            }
        }
    }
}
