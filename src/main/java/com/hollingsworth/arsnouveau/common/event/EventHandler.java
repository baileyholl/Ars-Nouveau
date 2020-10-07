package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
//import com.hollingsworth.arsnouveau.api.client.RenderEventQueue;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.enchantment.ReactiveEnchantment;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOpenGUI;
import com.hollingsworth.arsnouveau.common.network.PacketReactiveSpell;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void playerClone(PlayerEvent.PlayerRespawnEvent e) {
        syncPlayerEvent(e.getPlayer());
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.StartTracking e) {
        syncPlayerEvent(e.getPlayer());
    }

    @SubscribeEvent
    public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        syncPlayerEvent(e.getPlayer());
    }

    @SubscribeEvent
    public static void livingHitEvent(LivingHurtEvent e){
        LivingEntity entity = e.getEntityLiving();
        if(entity.getEntityWorld().isRemote || !(entity instanceof PlayerEntity))
            return;

        for(ItemStack s : entity.getArmorInventoryList()){
            castSpell((PlayerEntity) entity, s);
        }
    }

    public static void castSpell(PlayerEntity playerIn, ItemStack s){
        if(EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT, s) * .25 >= Math.random() && s.hasTag() && s.getTag().contains("spell")){
            List<AbstractSpellPart> list = SpellParchment.getSpellRecipe(s);
            SpellResolver resolver = new SpellResolver(list, true);
            RayTraceResult result = playerIn.pick(5, 0, false);

            EntityRayTraceResult entityRes = MathUtil.getLookedAtEntity(playerIn, 25);
            ItemStack stack = playerIn.getHeldItemMainhand();
            Hand handIn = Hand.MAIN_HAND;
            if(entityRes != null && entityRes.getEntity() instanceof LivingEntity){
                resolver.onCastOnEntity(stack, playerIn, (LivingEntity) entityRes.getEntity(), handIn);
                return;
            }

            if(result.getType() == RayTraceResult.Type.BLOCK){
                ItemUseContext context = new ItemUseContext(playerIn, handIn, (BlockRayTraceResult) result);
                resolver.onCastOnBlock(context);
                return;
            }
            resolver.onCast(stack,playerIn,playerIn.getEntityWorld());
        }
    }

    @SubscribeEvent
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock e){
        LivingEntity entity = e.getEntityLiving();

        if(entity.getEntityWorld().isRemote || !(entity instanceof PlayerEntity))
            return;
        ItemStack s = e.getItemStack();
        castSpell((PlayerEntity) entity, s);
    }


    @SubscribeEvent
    public static void playerAttackEntity(AttackEntityEvent e){
        LivingEntity entity = e.getEntityLiving();

        if(entity == null || entity.getEntityWorld().isRemote || !(entity instanceof PlayerEntity))
            return;
        ItemStack s = e.getEntityLiving().getHeldItemMainhand();
        castSpell((PlayerEntity) entity, s);
    }


    @SubscribeEvent
    public static void leftClickAir(PlayerInteractEvent.LeftClickEmpty e){
        LivingEntity entity = e.getEntityLiving();
        if(!(entity instanceof PlayerEntity))
            return;
        if(EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT, e.getItemStack()) > 0)
            Networking.INSTANCE.sendToServer(new PacketReactiveSpell());
    }

    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent e) {

        if(e.getEntityLiving() == null  || e.getEntityLiving().getActivePotionEffect(Effects.SLOWNESS) == null)
            return;
        EffectInstance effectInstance = e.getEntityLiving().getActivePotionEffect(Effects.SLOWNESS);
        if(effectInstance.getAmplifier() >= 20){
            e.getEntityLiving().setMotion(0,0,0);
        }
    }

    public static void syncPlayerEvent(PlayerEntity playerEntity){
        if (playerEntity instanceof ServerPlayerEntity) {
            ManaCapability.getMana(playerEntity).ifPresent(mana -> {
                mana.setMaxMana(ManaUtil.getMaxMana(playerEntity));
                Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana()));
            });
        }
    }


    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if(e.getEntityLiving().getEntityWorld().isRemote)
            return;
        CompoundNBT tag = e.getPlayer().getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        String book_tag = "an_book_";
        if(tag.getBoolean(book_tag))
            return;

        LivingEntity entity = e.getEntityLiving();
        e.getEntityLiving().getEntityWorld().addEntity(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), new ItemStack(ItemsRegistry.wornNotebook)));
        tag.putBoolean(book_tag, true);
        e.getPlayer().getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, tag);
    }

    @SubscribeEvent
    public static void playerOnTick(TickEvent.PlayerTickEvent e) {

        if (e.player instanceof ServerPlayerEntity && e.player.world.getGameTime() % 5 == 0) {
            if (e.player.world.getGameTime() % 20 == 0) {
                ManaCapability.getMana(e.player).ifPresent(mana -> {
                    double regenPerSecond = ManaUtil.getManaRegen(e.player);
                    if (mana.getCurrentMana() != mana.getMaxMana()) {
                        mana.addMana((int) regenPerSecond);
                        Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana()));
                    }
                });
            }
            if (e.player.world.getGameTime() % 10 == 0) {
                ManaCapability.getMana(e.player).ifPresent(mana -> {
                    mana.setMaxMana(ManaUtil.getMaxMana(e.player));
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) e.player), new PacketUpdateMana(mana.getCurrentMana(), mana.getMaxMana()));
                });
            }
        }
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent e){
        World world = e.world;
        if(world.isRemote)
            return;
        EventQueue.getInstance().tick();
    }

    @SubscribeEvent
    public static void clientTickEnd(TickEvent.ClientTickEvent event){
       if(event.phase == TickEvent.Phase.END){
           ClientInfo.ticksInGame++;
       }
    }

    @SubscribeEvent
    public static void playerDamaged(LivingDamageEvent e){
        if(e.getEntityLiving() != null && e.getEntityLiving().getActivePotionMap().containsKey(ModPotions.SHIELD_POTION)
        && (e.getSource() == DamageSource.MAGIC || e.getSource() == DamageSource.GENERIC )){
            float damage = e.getAmount() - 1f * e.getEntityLiving().getActivePotionMap().get(ModPotions.SHIELD_POTION).getAmplifier();
            if (damage < 0) damage = 0;
            e.setAmount(damage);
        }
    }

}
