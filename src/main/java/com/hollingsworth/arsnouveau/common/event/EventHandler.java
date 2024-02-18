package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import com.hollingsworth.arsnouveau.api.registry.CasterTomeRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.RitualEventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.LavaLily;
import com.hollingsworth.arsnouveau.common.command.*;
import com.hollingsworth.arsnouveau.common.compat.CaelusHandler;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.items.VoidJar;
import com.hollingsworth.arsnouveau.common.lib.PotionEffectTags;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketJoinedServer;
import com.hollingsworth.arsnouveau.common.network.PotionSyncPacket;
import com.hollingsworth.arsnouveau.common.perk.JumpHeightPerk;
import com.hollingsworth.arsnouveau.common.perk.LootingPerk;
import com.hollingsworth.arsnouveau.common.ritual.DenySpawnRitual;
import com.hollingsworth.arsnouveau.common.ritual.RitualFlight;
import com.hollingsworth.arsnouveau.common.ritual.RitualGravity;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectGlide;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.hollingsworth.arsnouveau.setup.registry.VillagerRegistry;
import com.hollingsworth.arsnouveau.setup.reward.Rewards;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;


@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void resourceLoadEvent(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<>() {
            @Override
            protected Object prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                return null;
            }

            @Override
            protected void apply(Object pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                MultiRecipeWrapper.RECIPE_CACHE = new HashMap<>();
                ArsNouveauAPI.getInstance().onResourceReload();
                EventQueue.getServerInstance().addEvent(new ITimedEvent() {
                    boolean expired;

                    @Override
                    public void tickEvent(TickEvent event) {
                        if (event instanceof TickEvent.ServerTickEvent serverTickEvent) {
                            CasterTomeRegistry.reloadTomeData(serverTickEvent.getServer().getRecipeManager(), ((TickEvent.ServerTickEvent) event).getServer().getLevel(Level.OVERWORLD));
                        }
                        expired = true;
                    }

                    @Override
                    public void tick(boolean serverSide) {

                    }

                    @Override
                    public boolean isExpired() {
                        return expired;
                    }
                });
            }
        });
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void itemPickupEvent(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack pickingUp = event.getItem().getItem();
        boolean voided = VoidJar.tryVoiding(player, pickingUp);
        if (voided) event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void itemPickupEvent(PlayerEvent.ItemPickupEvent event) {
        Player player = event.getEntity();
        ItemStack pickingUp = event.getStack();
        VoidJar.tryVoiding(player, pickingUp);
    }

    @SubscribeEvent
    public static void shieldEvent(ShieldBlockEvent e) {
        if (!e.getEntity().level.isClientSide && e.getEntity() instanceof Player player && player.isBlocking()) {
            if (player.getUseItem().getItem() == ItemsRegistry.ENCHANTERS_SHIELD.asItem()) {
                player.addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 200, 1));
                player.addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT.get(), 200, 1));
            }
        }
    }

    @SubscribeEvent
    public static void livingHurtEvent(LivingHurtEvent e) {
        if (e.getEntity().level.isClientSide)
            return;
        if (e.getSource().getEntity() instanceof LivingEntity livingUser) {
            if (livingUser instanceof Player)
                return;
            if (livingUser.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof EnchantersSword && BlockUtil.distanceFrom(livingUser.position, e.getEntity().position) < 3) {
                livingUser.getItemInHand(InteractionHand.MAIN_HAND).getItem().hurtEnemy(livingUser.getMainHandItem(), e.getEntity(), livingUser);
            }
        }
    }

    @SubscribeEvent
    public static void livingAttackEvent(LivingAttackEvent e) {
        if (e.getSource().is(DamageTypes.HOT_FLOOR) && e.getEntity() != null && !e.getEntity().getCommandSenderWorld().isClientSide) {
            Level world = e.getEntity().level;
            if (world.getBlockState(e.getEntity().blockPosition()).getBlock() instanceof LavaLily) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void livingSpawnEvent(MobSpawnEvent.FinalizeSpawn checkSpawn) {
        if (checkSpawn.getLevel() instanceof Level level && !level.isClientSide) {
            RitualEventQueue.getRitual(level, DenySpawnRitual.class, ritu -> ritu.denySpawn(checkSpawn));
        }
    }


    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent e) {
        if (e.getEntity() != null && e.getEntity().hasEffect(ModPotions.SNARE_EFFECT.get())) {
            e.getEntity().setDeltaMovement(0, 0, 0);
            return;
        }
    }


    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity().getCommandSenderWorld().isClientSide)
            return;
        if (e.getEntity() instanceof ServerPlayer serverPlayer) {
            boolean isContributor = Rewards.CONTRIBUTORS.contains(serverPlayer.getUUID());
            if (isContributor) {
                Networking.sendToPlayerClient(new PacketJoinedServer(true), (ServerPlayer) e.getEntity());
            }
        }
        CompoundTag tag = e.getEntity().getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        String book_tag = "an_book_";
        if (!tag.getBoolean(book_tag) && Config.SPAWN_BOOK.get()) {
            Player entity = e.getEntity();
            ItemHandlerHelper.giveItemToPlayer(entity, new ItemStack(ItemsRegistry.WORN_NOTEBOOK));
            tag.putBoolean(book_tag, true);
            e.getEntity().getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
        }
    }


    @SubscribeEvent
    public static void clientTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientInfo.ticksInGame++;
            if (ClientInfo.redTicks()) {
                ClientInfo.redOverlayTicks--;
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGlideTick(TickEvent.PlayerTickEvent event) {
        if (ArsNouveau.caelusLoaded && EffectGlide.canGlide(event.player)) {
            CaelusHandler.setFlying(event.player);
        }

        if (event.player.hasEffect(ModPotions.FLIGHT_EFFECT.get())
            && event.player.level.getGameTime() % 20 == 0
            && event.player.getEffect(ModPotions.FLIGHT_EFFECT.get()).getDuration() <= 30 * 20
            && event.player instanceof ServerPlayer serverPlayer) {
            RitualEventQueue.getRitual(event.player.level, RitualFlight.class, flight -> flight.attemptRefresh(serverPlayer));
        }

        if (event.player.level.getGameTime() % RitualGravity.renewInterval == 0 && event.player instanceof ServerPlayer serverPlayer) {
            MobEffectInstance gravity = event.player.getEffect(ModPotions.GRAVITY_EFFECT.get());
            if (gravity == null || gravity.getDuration() <= RitualGravity.renewThreshold) {
                RitualEventQueue.getRitual(event.player.level, RitualGravity.class, ritual -> !serverPlayer.isCreative() && ritual.attemptRefresh(serverPlayer));
            }
        }
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (!event.getEntity().level.isClientSide && event.getEntity() instanceof Player entity) {
            RitualEventQueue.getRitual(entity.level, RitualFlight.class, flight -> flight.onJumpEvent(event));
        }
    }

    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent e) {
        if (e.getEntity() != null && e.getEntity().hasEffect(ModPotions.DEFENCE_EFFECT.get()) && (e.getSource().is(DamageTypes.MAGIC) || e.getSource().is(DamageTypes.GENERIC) || e.getSource().is(DamageTypes.MOB_ATTACK))) {
            if (e.getAmount() > 0.5) {
                e.setAmount((float) Math.max(0.5, e.getAmount() - 1.0f - e.getEntity().getEffect(ModPotions.DEFENCE_EFFECT.get()).getAmplifier()));
            }
        }

        if (e.getEntity() != null && e.getSource().is(DamageTypes.LIGHTNING_BOLT) && e.getEntity().hasEffect(ModPotions.SHOCKED_EFFECT.get())) {
            float damage = e.getAmount() + 3.0f + 3.0f * e.getEntity().getEffect(ModPotions.SHOCKED_EFFECT.get()).getAmplifier();
            e.setAmount(Math.max(0, damage));
        }
        LivingEntity entity = e.getEntity();
        if (entity == null)
            return;
        if (entity.hasEffect(ModPotions.HEX_EFFECT.get())
            && (entity.hasEffect(MobEffects.POISON)
                || entity.hasEffect(MobEffects.WITHER)
                || entity.isOnFire()
                || entity.hasEffect(ModPotions.SHOCKED_EFFECT.get())
                || entity.getTicksFrozen() >= entity.getTicksRequiredToFreeze())) {
            e.setAmount(e.getAmount() + 0.5f + 0.33f * entity.getEffect(ModPotions.HEX_EFFECT.get()).getAmplifier());
        }
        double warding = PerkUtil.valueOrZero(entity, PerkAttributes.WARDING.get());
        double feather = PerkUtil.valueOrZero(entity, PerkAttributes.FEATHER.get());
        if (e.getSource().is(DamageTypes.MAGIC)) {
            e.setAmount((float) (e.getAmount() - warding));
        }

        if (e.getSource().is(DamageTypes.FALL)) {
            e.setAmount((float) (e.getAmount() - (e.getAmount() * feather)));
        }
    }

    @SubscribeEvent
    public static void fallEvent(LivingFallEvent fallEvent) {
        double jumpBonus = PerkUtil.countForPerk(JumpHeightPerk.INSTANCE, fallEvent.getEntity());
        fallEvent.setDistance((float) (fallEvent.getDistance() - (jumpBonus / 0.1)));
        if (CuriosUtil.hasItem(fallEvent.getEntity(), ItemsRegistry.BELT_OF_LEVITATION.asItem())) {
            fallEvent.setDistance(Math.max(0, fallEvent.getDistance() - 6));
        }
    }

    @SubscribeEvent
    public static void entityHeal(LivingHealEvent e) {
        LivingEntity entity = e.getEntity();
        if (entity != null && entity.hasEffect(ModPotions.HEX_EFFECT.get())) {
            e.setAmount(e.getAmount() / 2.0f);
        }

        if (entity != null && entity.hasEffect(ModPotions.RECOVERY_EFFECT.get())) {
            e.setAmount(e.getAmount() + 1 + entity.getEffect(ModPotions.RECOVERY_EFFECT.get()).getAmplifier());
        }
    }

    @SubscribeEvent
    public static void eatEvent(LivingEntityUseItemEvent.Finish event) {
        if (!event.getEntity().level.isClientSide && event.getItem().getItem().getFoodProperties(event.getItem(), event.getEntity()) != null && event.getItem().getItem().isEdible()) {
            if (event.getEntity() instanceof Player player) {
                FoodData stats = player.getFoodData();
                stats.saturationLevel = (float) (stats.saturationLevel * PerkUtil.perkValue(player, PerkAttributes.WHIRLIESPRIG.get()));
            }
        }
    }

    @SubscribeEvent
    public static void dispelEvent(DispelEvent event) {
        if (event.rayTraceResult instanceof EntityHitResult hit && hit.getEntity() instanceof Witch entity) {
            if (entity.getHealth() <= entity.getMaxHealth() / 2) {
                entity.remove(Entity.RemovalReason.KILLED);
                ParticleUtil.spawnPoof((ServerLevel) event.world, entity.blockPosition());
                event.world.addFreshEntity(new ItemEntity(event.world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(ItemsRegistry.WIXIE_SHARD)));
            }
        }

    }

    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event) {
        ResetCommand.register(event.getDispatcher());
        DataDumpCommand.register(event.getDispatcher());
        PathCommand.register(event.getDispatcher());
        ToggleLightCommand.register(event.getDispatcher());
        AddTomeCommand.register(event.getDispatcher());
        SummonAnimHeadCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerRegistry.SHARDS_TRADER.get()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            List<VillagerTrades.ItemListing> level1 = trades.get(1);
            List<VillagerTrades.ItemListing> level2 = trades.get(2);
            List<VillagerTrades.ItemListing> level3 = trades.get(3);
            List<VillagerTrades.ItemListing> level4 = trades.get(4);
            List<VillagerTrades.ItemListing> level5 = trades.get(5);

            level1.add((trader, rand) -> itemToEmer(BlockRegistry.SOURCEBERRY_BUSH, 16, 16, 2));
            level1.add((trader, rand) -> itemToEmer(ItemsRegistry.MAGE_FIBER, 16, 16, 2));
            level1.add((trader, rand) -> itemToEmer(BlockRegistry.BOMBEGRANTE_POD, 6, 16, 2));
            level1.add((trader, rand) -> itemToEmer(BlockRegistry.MENDOSTEEN_POD, 6, 16, 2));
            level1.add((trader, rand) -> itemToEmer(BlockRegistry.FROSTAYA_POD, 6, 16, 2));
            level1.add((trader, rand) -> itemToEmer(BlockRegistry.BASTION_POD, 6, 16, 2));
            level1.add((trader, rand) -> itemToEmer(Items.AMETHYST_SHARD, 32, 16, 2));

            level1.add((trader, rand) -> emerToItem(ItemsRegistry.SOURCE_BERRY_ROLL, 4, 16, 2));

            level2.add((trader, rand) -> emerToItem(BlockRegistry.GHOST_WEAVE, 1, 8, 2));
            level2.add((trader, rand) -> emerToItem(BlockRegistry.MIRROR_WEAVE, 1, 8, 2));
            level2.add((trader, rand) -> emerToItem(BlockRegistry.FALSE_WEAVE, 1, 8, 2));
            level2.add((trader, rand) -> emerToItem(ItemsRegistry.WARP_SCROLL, 1, 8, 2));

            level2.add((trader, rand) -> itemToEmer(ItemsRegistry.WILDEN_HORN, 4, 8, 12));
            level2.add((trader, rand) -> itemToEmer(ItemsRegistry.WILDEN_SPIKE, 4, 8, 12));
            level2.add((trader, rand) -> itemToEmer(ItemsRegistry.WILDEN_WING, 4, 8, 12));

            List<RitualTablet> tablets = new ArrayList<>(RitualRegistry.getRitualItemMap().values());
            for (RitualTablet tablet : tablets) {
                if (tablet.ritual.canBeTraded()) {
                    level3.add((trader, rand) -> emerToItem(tablet, 4, 1, 12));
                }
            }

            for (ItemStack shard : Ingredient.of(ItemTagProvider.SUMMON_SHARDS_TAG).getItems()) {
                level4.add((trader, rand) -> emerToItem(shard.getItem(), 20, 1, 20));
            }

            level5.add((trader, rand) -> emerToItem(ItemsRegistry.SOURCE_BERRY_PIE, 4, 8, 2));
            level5.add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 48), DungeonLootTables.getRandomItem(DungeonLootTables.RARE_LOOT), 1, 20, 0.2F));

        }
    }

    public static MerchantOffer emerToItem(ItemLike itemLike, int cost, int uses, int exp) {
        return new VillagerTrades.ItemsForEmeralds(itemLike.asItem(), cost, uses, exp).getOffer(null, null);
    }

    public static MerchantOffer itemToEmer(ItemLike itemLike, int cost, int uses, int exp) {
        return new VillagerTrades.EmeraldForItems(itemLike.asItem(), cost, uses, exp).getOffer(null, null);
    }


    @SubscribeEvent
    public static void onLootingEvent(LootingLevelEvent event) {
        if (event.getDamageSource() != null && event.getDamageSource().getEntity() instanceof LivingEntity living) {
            event.setLootingLevel(event.getLootingLevel() + Math.round(PerkUtil.countForPerk(LootingPerk.INSTANCE, living)));
        }
    }

    @SubscribeEvent
    public static void onPotionAdd(MobEffectEvent.Added event) {
        LivingEntity target = event.getEntity();
        Entity applier = event.getEffectSource();
        if (target.level.isClientSide)
            return;
        double bonus = 0.0;
        MobEffect effect = event.getEffectInstance().getEffect();
        if (effect.isBeneficial()) {
            bonus = PerkUtil.valueOrZero(target, PerkAttributes.WIXIE.get());
        } else if (applier instanceof LivingEntity living) {
            bonus = PerkUtil.valueOrZero(living, PerkAttributes.WIXIE.get());
        }

        if (bonus > 0.0) {
            event.getEffectInstance().duration = (int) (event.getEffectInstance().duration * bonus);
        }

        ForgeRegistries.MOB_EFFECTS.getHolder(effect).ifPresent(holder -> {
            if (holder.is(PotionEffectTags.TO_SYNC)) {
                Networking.sendToNearby(target.level(), target, new PotionSyncPacket(target.getId(), effect, event.getEffectInstance().getDuration()));
            }
        });

    }

    @SubscribeEvent
    public static void onPotionRemove(MobEffectEvent.Remove event) {
        if (event.getEntity() instanceof LivingEntity && event.getEffectInstance() != null && !event.getEntity().level.isClientSide) {
            LivingEntity target = event.getEntity();
            MobEffect effect = event.getEffectInstance().getEffect();
            ForgeRegistries.MOB_EFFECTS.getHolder(effect).ifPresent(holder -> {
                if (holder.is(PotionEffectTags.TO_SYNC)) {
                    Networking.sendToNearby(target.level(), target, new PotionSyncPacket(target.getId(), effect, -1));
                }
            });
        }
    }

    @SubscribeEvent
    public static void treeGrow(SaplingGrowTreeEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;
        Set<UUID> sprigs = Whirlisprig.WHIRLI_MAP.getEntities(level);
        List<UUID> sprigsToRemove = new ArrayList<>();

        for (UUID uuid : sprigs) {
            if (level.getEntity(uuid) instanceof Whirlisprig whirlisprig) {
                if (BlockUtil.distanceFrom(whirlisprig.blockPosition(), event.getPos()) <= 10 && !whirlisprig.isTamed()) {
                    whirlisprig.droppingShards = true;
                }
            } else {
                sprigsToRemove.add(uuid);
            }
        }
        for (UUID uuid : sprigsToRemove) {
            Whirlisprig.WHIRLI_MAP.removeEntity(level, uuid);
        }
    }

    private EventHandler() {
    }

}
