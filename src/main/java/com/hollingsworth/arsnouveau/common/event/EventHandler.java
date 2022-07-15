package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.event.FlightRefreshEvent;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.LavaLily;
import com.hollingsworth.arsnouveau.common.command.DataDumpCommand;
import com.hollingsworth.arsnouveau.common.command.PathCommand;
import com.hollingsworth.arsnouveau.common.command.ResetCommand;
import com.hollingsworth.arsnouveau.common.command.ToggleLightCommand;
import com.hollingsworth.arsnouveau.common.compat.CaelusHandler;
import com.hollingsworth.arsnouveau.common.items.VoidJar;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.ritual.RitualFlight;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventHandler {


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void itemPickupEvent(EntityItemPickupEvent event) {
        Player player = event.getPlayer();
        ItemStack pickingUp = event.getItem().getItem();
        boolean voided = VoidJar.tryVoiding(player, pickingUp);
        if (voided) event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void itemPickupEvent(PlayerEvent.ItemPickupEvent event) {
        Player player = event.getPlayer();
        ItemStack pickingUp = event.getStack();
        VoidJar.tryVoiding(player, pickingUp);
    }


    @SubscribeEvent
    public static void livingHurtEvent(LivingHurtEvent e) {
        if (!e.getEntityLiving().level.isClientSide && e.getEntityLiving() instanceof Player && e.getEntityLiving().isBlocking()) {
            if (e.getEntityLiving().isHolding(ItemsRegistry.ENCHANTERS_SHIELD.asItem())) {
                e.getEntityLiving().addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 200, 1));
                e.getEntityLiving().addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT.get(), 200, 1));
            }
        }
    }

    @SubscribeEvent
    public static void livingAttackEvent(LivingAttackEvent e) {
        if (e.getSource() == DamageSource.HOT_FLOOR && e.getEntityLiving() != null && !e.getEntity().getCommandSenderWorld().isClientSide) {
            Level world = e.getEntity().level;
            if (world.getBlockState(e.getEntityLiving().blockPosition()).getBlock() instanceof LavaLily) {
                e.setCanceled(true);
            }
        }
    }


    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent e) {
        if (e.getEntityLiving() == null || !e.getEntityLiving().hasEffect(ModPotions.SNARE_EFFECT.get()))
            return;
        e.getEntityLiving().setDeltaMovement(0, 0, 0);

    }


    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntityLiving().getCommandSenderWorld().isClientSide || !Config.SPAWN_BOOK.get())
            return;
        CompoundTag tag = e.getPlayer().getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        String book_tag = "an_book_";
        if (tag.getBoolean(book_tag))
            return;

        LivingEntity entity = e.getEntityLiving();
        e.getEntityLiving().getCommandSenderWorld().addFreshEntity(new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(ItemsRegistry.WORN_NOTEBOOK)));
        tag.putBoolean(book_tag, true);
        e.getPlayer().getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
    }


    @SubscribeEvent
    public static void clientTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientInfo.ticksInGame++;
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGlideTick(TickEvent.PlayerTickEvent event) {
        if (ArsNouveau.caelusLoaded && event.player.hasEffect(ModPotions.GLIDE_EFFECT.get())) {
            CaelusHandler.setFlying(event.player);
        }

        if (event.player.hasEffect(ModPotions.FLIGHT_EFFECT.get()) && event.player.level.getGameTime() % 20 == 0 && event.player.getEffect(ModPotions.FLIGHT_EFFECT.get()).getDuration() <= 30 * 20) {
            FlightRefreshEvent flightRefreshEvent = new FlightRefreshEvent(event.player);
            MinecraftForge.EVENT_BUS.post(flightRefreshEvent);
        }
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (!event.getEntityLiving().level.isClientSide && event.getEntityLiving() instanceof Player entity) {
            if (entity.getEffect(ModPotions.FLIGHT_EFFECT.get()) == null && RitualFlight.RitualFlightHandler.canPlayerStillFly(entity) != null) {
                RitualFlight.RitualFlightHandler.grantFlight(entity);
            }

        }
    }

    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent e) {
        if (e.getEntityLiving() != null && e.getSource() == DamageSource.LIGHTNING_BOLT && e.getEntityLiving().hasEffect(ModPotions.SHOCKED_EFFECT.get())) {
            float damage = e.getAmount() + 3.0f + 3.0f * e.getEntityLiving().getEffect(ModPotions.SHOCKED_EFFECT.get()).getAmplifier();
            e.setAmount(Math.max(0, damage));
        }
        LivingEntity entity = e.getEntityLiving();
        if (entity != null && entity.hasEffect(ModPotions.HEX_EFFECT.get()) &&
                (entity.hasEffect(MobEffects.POISON) || entity.hasEffect(MobEffects.WITHER) || entity.isOnFire() || entity.hasEffect(ModPotions.SHOCKED_EFFECT.get()))) {
            e.setAmount(e.getAmount() + 0.5f + 0.33f * entity.getEffect(ModPotions.HEX_EFFECT.get()).getAmplifier());

        }
    }

    @SubscribeEvent
    public static void entityHeal(LivingHealEvent e) {
        LivingEntity entity = e.getEntityLiving();
        if (entity != null && entity.hasEffect(ModPotions.HEX_EFFECT.get())) {
            e.setAmount(e.getAmount() / 2.0f);
        }
    }

    @SubscribeEvent
    public static void dispelEvent(DispelEvent event) {
        if (event.rayTraceResult instanceof EntityHitResult && ((EntityHitResult) event.rayTraceResult).getEntity() instanceof LivingEntity entity) {
            if (entity instanceof Witch) {
                if (entity.getHealth() <= entity.getMaxHealth() / 2) {
                    entity.remove(Entity.RemovalReason.KILLED);
                    ParticleUtil.spawnPoof((ServerLevel) event.world, entity.blockPosition());
                    event.world.addFreshEntity(new ItemEntity(event.world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(ItemsRegistry.WIXIE_SHARD)));
                }
            }

        }

    }

    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event) {
        ResetCommand.register(event.getDispatcher());
        DataDumpCommand.register(event.getDispatcher());
        PathCommand.register(event.getDispatcher());
        ToggleLightCommand.register(event.getDispatcher());
    }

    private EventHandler() {
    }

}
