package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.LavaLily;
import com.hollingsworth.arsnouveau.common.command.DataDumpCommand;
import com.hollingsworth.arsnouveau.common.command.ResetCommand;
import com.hollingsworth.arsnouveau.common.items.VoidJar;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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


    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void itemPickupEvent( EntityItemPickupEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack pickingUp = event.getItem().getItem();
        boolean voided = VoidJar.tryVoiding(player, pickingUp);
        if (voided) event.setResult(Event.Result.ALLOW);
    }


    @SubscribeEvent
    public static void livingHurtEvent(LivingHurtEvent e){
       if(!e.getEntityLiving().level.isClientSide && e.getEntityLiving() instanceof PlayerEntity && e.getEntityLiving().isBlocking()){
           if(e.getEntityLiving().isHolding(ItemsRegistry.ENCHANTERS_SHIELD)){
               e.getEntityLiving().addEffect(new EffectInstance(ModPotions.MANA_REGEN_EFFECT, 200, 1));
           }
       }
    }

    @SubscribeEvent
    public static void livingAttackEvent(LivingAttackEvent e){
        if(e.getSource() == DamageSource.HOT_FLOOR && e.getEntityLiving() != null && !e.getEntity().getCommandSenderWorld().isClientSide){
            World world = e.getEntity().level;
            if(world.getBlockState(e.getEntityLiving().blockPosition()).getBlock() instanceof LavaLily){
                e.setCanceled(true);
            }
        }
    }



    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent e) {
        if(e.getEntityLiving() == null  || e.getEntityLiving().getEffect(ModPotions.SNARE_EFFECT) == null)
            return;
        e.getEntityLiving().setDeltaMovement(0,0,0);

    }


    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if(e.getEntityLiving().getCommandSenderWorld().isClientSide || !Config.SPAWN_BOOK.get())
            return;
        CompoundNBT tag = e.getPlayer().getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        String book_tag = "an_book_";
        if(tag.getBoolean(book_tag))
            return;

        LivingEntity entity = e.getEntityLiving();
        e.getEntityLiving().getCommandSenderWorld().addFreshEntity(new ItemEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(ItemsRegistry.wornNotebook)));
        tag.putBoolean(book_tag, true);
        e.getPlayer().getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, tag);
    }


    @SubscribeEvent
    public static void clientTickEnd(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            ClientInfo.ticksInGame++;
        }
    }

    @SubscribeEvent
    public static void playerDamaged(LivingHurtEvent e){
        if(e.getEntityLiving() != null && e.getEntityLiving().getActiveEffectsMap().containsKey(ModPotions.SHIELD_POTION)
                && (e.getSource() == DamageSource.MAGIC || e.getSource() == DamageSource.GENERIC || e.getSource() instanceof EntityDamageSource)){
            float damage = e.getAmount() - (1.0f + 0.5f * e.getEntityLiving().getActiveEffectsMap().get(ModPotions.SHIELD_POTION).getAmplifier());
            e.setAmount(Math.max(0, damage));
        }
    }

    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent e){
        if(e.getEntityLiving() != null && e.getSource() == DamageSource.LIGHTNING_BOLT && e.getEntityLiving().getEffect(ModPotions.SHOCKED_EFFECT) != null){
            float damage = e.getAmount() + 3.0f + 3.0f * e.getEntityLiving().getEffect(ModPotions.SHOCKED_EFFECT).getAmplifier();
            e.setAmount(Math.max(0, damage));
        }
        LivingEntity entity = e.getEntityLiving();
        if(entity != null  && entity.getEffect(ModPotions.HEX_EFFECT) != null &&
                (entity.getEffect(Effects.POISON) != null || entity.getEffect(Effects.WITHER) != null || entity.isOnFire())){
            e.setAmount(e.getAmount() + 0.5f + 0.33f*entity.getEffect(ModPotions.HEX_EFFECT).getAmplifier());

        }
    }

    @SubscribeEvent
    public static void entityHeal(LivingHealEvent e){
        LivingEntity entity = e.getEntityLiving();
        if(entity != null  && entity.getEffect(ModPotions.HEX_EFFECT) != null){
            e.setAmount(e.getAmount()/2.0f);
        }
    }

    @SubscribeEvent
    public static void dispelEvent(DispelEvent event){
        if(event.rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) event.rayTraceResult).getEntity() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) event.rayTraceResult).getEntity();
            if(entity instanceof WitchEntity){
                if(entity.getHealth() <= entity.getMaxHealth()/2){
                    entity.remove();
                    ParticleUtil.spawnPoof((ServerWorld) event.world, entity.blockPosition());
                    event.world.addFreshEntity(new ItemEntity(event.world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(ItemsRegistry.WIXIE_SHARD)));
                }
            }

        }

    }


    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event){
        ResetCommand.register(event.getDispatcher());
        DataDumpCommand.register(event.getDispatcher());
    }

    private EventHandler(){}

}
