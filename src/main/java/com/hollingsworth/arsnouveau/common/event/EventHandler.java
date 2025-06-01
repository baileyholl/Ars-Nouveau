package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.event.SuccessfulTreeGrowthEvent;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.recipe.MultiRecipeWrapper;
import com.hollingsworth.arsnouveau.api.registry.*;
import com.hollingsworth.arsnouveau.api.ritual.RitualEventQueue;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.command.*;
import com.hollingsworth.arsnouveau.common.compat.CaelusHandler;
import com.hollingsworth.arsnouveau.common.crafting.recipes.DispelEntityRecipe;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import com.hollingsworth.arsnouveau.common.entity.debug.FixedStack;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.common.items.VoidJar;
import com.hollingsworth.arsnouveau.common.lib.PotionEffectTags;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketInitDocs;
import com.hollingsworth.arsnouveau.common.network.PacketJoinedServer;
import com.hollingsworth.arsnouveau.common.network.PotionSyncPacket;
import com.hollingsworth.arsnouveau.common.perk.JumpHeightPerk;
import com.hollingsworth.arsnouveau.common.ritual.DenySpawnRitual;
import com.hollingsworth.arsnouveau.common.ritual.RitualFlight;
import com.hollingsworth.arsnouveau.common.ritual.RitualGravity;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectGlide;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectWololo;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.*;
import com.hollingsworth.arsnouveau.setup.reward.Rewards;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
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
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.village.VillageSiegeEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.*;


@EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void resourceLoadEvent(AddReloadListenerEvent event) {
        event.addListener(new SimplePreparableReloadListener<>() {
            @SuppressWarnings({"NullableProblems", "DataFlowIssue"})
            @Override
            protected Object prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                return null;
            }

            @SuppressWarnings("NullableProblems")
            @Override
            protected void apply(Object pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                MultiRecipeWrapper.RECIPE_CACHE = new HashMap<>();
                EffectWololo.recipeCache = new FixedStack<>(EffectWololo.MAX_RECIPE_CACHE);
                ArsNouveauAPI.getInstance().onResourceReload();
                EventQueue.getServerInstance().addEvent(new ITimedEvent() {
                    boolean expired;

                    @Override
                    public void tick(ServerTickEvent serverTickEvent) {
                        GenericRecipeRegistry.reloadAll(serverTickEvent.getServer().getRecipeManager());
                        CasterTomeRegistry.reloadTomeData(serverTickEvent.getServer().getRecipeManager(), serverTickEvent.getServer().registryAccess());
                        BuddingConversionRegistry.reloadBuddingConversionRecipes(serverTickEvent.getServer().getRecipeManager());
                        AlakarkinosConversionRegistry.reloadAlakarkinosRecipes(serverTickEvent.getServer().getRecipeManager());
                        ScryRitualRegistry.reloadScryRitualRecipes(serverTickEvent.getServer().getRecipeManager());
                        for (ServerPlayer player : serverTickEvent.getServer().getPlayerList().getPlayers()) {
                            Networking.sendToPlayerClient(new PacketInitDocs(), player);
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
    public static void itemPickupEvent(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        ItemStack pickingUp = event.getItemEntity().getItem();
        VoidJar.tryVoiding(player, pickingUp);
    }

    @SubscribeEvent
    public static void shieldEvent(LivingShieldBlockEvent e) {
        if (!e.getEntity().level.isClientSide && e.getEntity() instanceof Player player && player.isBlocking()) {
            if (player.getUseItem().getItem() == ItemsRegistry.ENCHANTERS_SHIELD.asItem()) {
                player.addEffect(new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 200, 1));
                player.addEffect(new MobEffectInstance(ModPotions.SPELL_DAMAGE_EFFECT, 200, 1));
            }
        }
    }

    @SubscribeEvent
    public static void livingHurtEvent(LivingDamageEvent.Post e) {
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
    public static void livingSpawnEvent(FinalizeSpawnEvent checkSpawn) {
        if (checkSpawn.getLevel() instanceof Level level && !level.isClientSide) {
            RitualEventQueue.getRitual(level, DenySpawnRitual.class, ritu -> ritu.denySpawn(checkSpawn));
        }
    }

    @SubscribeEvent
    public static void villageSiegeEvent(VillageSiegeEvent checkSpawn) {
        if (checkSpawn.getLevel() instanceof Level level && !level.isClientSide) {
            RitualEventQueue.getRitual(level, DenySpawnRitual.class, ritu -> ritu.denySiege(checkSpawn));
        }
    }


    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent e) {
        e.getEntity();
        if (e.getEntity().hasEffect(ModPotions.SNARE_EFFECT)) {
            e.getEntity().setDeltaMovement(0, 0, 0);
            return;
        }
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity().getCommandSenderWorld().isClientSide)
            return;
        if (e.getEntity() instanceof ServerPlayer serverPlayer) {
            Networking.sendToPlayerClient(new PacketInitDocs(), serverPlayer);
            boolean isContributor = Rewards.CONTRIBUTORS.contains(serverPlayer.getUUID());
            if (isContributor) {
                Networking.sendToPlayerClient(new PacketJoinedServer(true), serverPlayer);
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGlideTick(PlayerTickEvent.Pre event) {
        var player = event.getEntity();
        if (ArsNouveau.caelusLoaded && EffectGlide.canGlide(player)) {
            CaelusHandler.setFlying(player);
        }

        if (player.hasEffect(ModPotions.FLIGHT_EFFECT)
                && player.level.getGameTime() % 20 == 0
                && player.getEffect(ModPotions.FLIGHT_EFFECT).getDuration() <= 30 * 20
                && player instanceof ServerPlayer serverPlayer) {
            RitualEventQueue.getRitual(player.level, RitualFlight.class, flight -> flight.attemptRefresh(serverPlayer));
        }

        if (player.level.getGameTime() % RitualGravity.renewInterval == 0 && player instanceof ServerPlayer serverPlayer) {
            MobEffectInstance gravity = player.getEffect(ModPotions.GRAVITY_EFFECT);
            if (gravity == null || gravity.getDuration() <= RitualGravity.renewThreshold) {
                RitualEventQueue.getRitual(player.level, RitualGravity.class, ritual -> !serverPlayer.isCreative() && ritual.attemptRefresh(serverPlayer));
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
    public static void entityHurt(LivingDamageEvent.Pre e) {
        var container = e.getContainer();
        var source = container.getSource();
        var amount = container.getNewDamage();
        if (e.getEntity().hasEffect(ModPotions.DEFENCE_EFFECT) && (source.is(DamageTypes.MAGIC) || source.is(DamageTypes.GENERIC) || source.is(DamageTypes.MOB_ATTACK))) {
            if (amount > 0.5) {
                container.setNewDamage((float) Math.max(0.5, amount - 1.0f - e.getEntity().getEffect(ModPotions.DEFENCE_EFFECT).getAmplifier()));
            }
        }

        if (source.is(DamageTypes.LIGHTNING_BOLT) && e.getEntity().hasEffect(ModPotions.SHOCKED_EFFECT)) {
            float damage = amount + 3.0f + 3.0f * e.getEntity().getEffect(ModPotions.SHOCKED_EFFECT).getAmplifier();
            container.setNewDamage(Math.max(0, damage));
        }
        LivingEntity entity = e.getEntity();
        if (entity.hasEffect(ModPotions.HEX_EFFECT)
                && (entity.hasEffect(MobEffects.POISON)
                || entity.hasEffect(MobEffects.WITHER)
                || entity.isOnFire()
                || entity.hasEffect(ModPotions.SHOCKED_EFFECT)
                || entity.getTicksFrozen() >= entity.getTicksRequiredToFreeze())) {
            container.setNewDamage(amount + 0.5f + 0.33f * entity.getEffect(ModPotions.HEX_EFFECT).getAmplifier());
        }
        double warding = PerkUtil.valueOrZero(entity, PerkAttributes.WARDING);
        double feather = PerkUtil.valueOrZero(entity, PerkAttributes.FEATHER);
        if (source.is(Tags.DamageTypes.IS_MAGIC)) {
            container.setNewDamage((float) (amount - warding));
        }

        if (source.is(DamageTypes.FALL)) {
            container.setNewDamage((float) (amount - (amount * feather)));
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
        if (entity.hasEffect(ModPotions.HEX_EFFECT)) {
            e.setAmount(e.getAmount() / 2.0f);
        }

        if (entity.hasEffect(ModPotions.RECOVERY_EFFECT)) {
            e.setAmount(e.getAmount() + 1 + entity.getEffect(ModPotions.RECOVERY_EFFECT).getAmplifier());
        }
    }

    @SubscribeEvent
    public static void eatEvent(LivingEntityUseItemEvent.Finish event) {
        if (!event.getEntity().level.isClientSide && event.getItem().getItem().getFoodProperties(event.getItem(), event.getEntity()) != null) {
            if (event.getEntity() instanceof Player player) {
                FoodData stats = player.getFoodData();
                stats.saturationLevel = (float) (stats.saturationLevel * PerkUtil.perkValue(player, PerkAttributes.WHIRLIESPRIG));
            }
        }
    }

    private static void replaceEntityWithItems(ServerLevel level, Entity entity, ItemStack... items) {
        entity.remove(Entity.RemovalReason.KILLED);
        ParticleUtil.spawnPoof(level, entity.blockPosition());
        for (ItemStack item : items) {
            level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), item));
        }
    }

    @SubscribeEvent
    public static void dispelEvent(DispelEvent event) {
        if (event.rayTraceResult instanceof EntityHitResult hit && event.world instanceof ServerLevel level) {
            Entity entity = hit.getEntity();
            if (!entity.isAlive()) return;
            for (RecipeHolder<DispelEntityRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeRegistry.DISPEL_ENTITY_TYPE.get())) {
                var recipe = holder.value();
                if (recipe.matches(event.shooter, entity)) {
                    replaceEntityWithItems(level, entity, recipe.result(event.shooter, entity).toArray(ItemStack[]::new));
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event) {
        ResetCommand.register(event.getDispatcher());
        DataDumpCommand.register(event.getDispatcher());
        ToggleLightCommand.register(event.getDispatcher());
        AddTomeCommand.register(event.getDispatcher());
        SummonAnimHeadCommand.register(event.getDispatcher());
        LearnGlyphCommand.register(event.getDispatcher());
        AdoptCommand.register(event.getDispatcher());
        DroplessMobsCommand.register(event.getDispatcher());
        DebugNumberCommand.register(event.getDispatcher());
        if (!FMLEnvironment.production) {
            ExportDocsCommand.register(event.getDispatcher());
        }
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

            level1.add((trader, rand) -> itemToEmer(trader, BlockRegistry.SOURCEBERRY_BUSH, 16, 16, 2));
            level1.add((trader, rand) -> itemToEmer(trader, ItemsRegistry.MAGE_FIBER, 16, 16, 2));

            for (ItemStack fruit : Ingredient.of(ItemTagProvider.SHADY_WIZARD_FRUITS).getItems()) {
                level1.add((trader, rand) -> itemToEmer(trader, fruit.getItem(), 6, 16, 2));
            }

            level1.add((trader, rand) -> itemToEmer(trader, Items.AMETHYST_SHARD, 32, 16, 2));

            level1.add((trader, rand) -> emerToItem(trader, ItemsRegistry.SOURCE_BERRY_ROLL, 4, 16, 2));

            level2.add((trader, rand) -> emerToItem(trader, BlockRegistry.GHOST_WEAVE, 1, 8, 2));
            level2.add((trader, rand) -> emerToItem(trader, BlockRegistry.MIRROR_WEAVE, 1, 8, 2));
            level2.add((trader, rand) -> emerToItem(trader, BlockRegistry.FALSE_WEAVE, 1, 8, 2));
            level2.add((trader, rand) -> emerToItem(trader, ItemsRegistry.WARP_SCROLL, 1, 8, 2));

            for (ItemStack wilden : Ingredient.of(ItemTagProvider.WILDEN_DROP_TAG).getItems()) {
                level2.add((trader, rand) -> itemToEmer(trader, wilden.getItem(), 4, 8, 12));
            }

            List<RitualTablet> tablets = new ArrayList<>(RitualRegistry.getRitualItemMap().values());
            for (RitualTablet tablet : tablets) {
                if (tablet.getDefaultInstance().is(ItemTagProvider.RITUAL_TRADE_BLACKLIST)) continue;
                level3.add((trader, rand) -> emerToItem(trader, tablet, 4, 1, 12));
            }

            for (ItemStack shard : Ingredient.of(ItemTagProvider.SUMMON_SHARDS_TAG).getItems()) {
                level4.add((trader, rand) -> emerToItem(trader, shard.getItem(), 20, 1, 20));
            }
            level5.add((trader, rand) -> emerToItem(trader, ItemsRegistry.SOURCE_BERRY_PIE, 4, 8, 2));
            level5.add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 48), DungeonLootTables.getRandomItem(DungeonLootTables.RARE_LOOT), 1, 20, 0.2F));

        }
    }

    public static MerchantOffer emerToItem(Entity trader, ItemLike itemLike, int cost, int uses, int exp) {
        return new VillagerTrades.ItemsForEmeralds(itemLike.asItem(), cost, uses, exp).getOffer(trader, trader.getRandom());
    }

    public static MerchantOffer itemToEmer(Entity trader, ItemLike itemLike, int cost, int uses, int exp) {
        return new VillagerTrades.EmeraldForItems(itemLike.asItem(), cost, uses, exp).getOffer(trader, trader.getRandom());
    }


    @SubscribeEvent
    public static void onPotionAdd(MobEffectEvent.Added event) {
        LivingEntity target = event.getEntity();
        Entity applier = event.getEffectSource();
        if (target.level.isClientSide)
            return;
        double bonus = 0.0;
        Holder<MobEffect> holder = event.getEffectInstance().getEffect();
        MobEffect effect = holder.value();
        if (effect.isBeneficial()) {
            bonus = PerkUtil.valueOrZero(target, PerkAttributes.WIXIE);
        } else if (applier instanceof LivingEntity living) {
            bonus = PerkUtil.valueOrZero(living, PerkAttributes.WIXIE);
        }

        if (bonus > 0.0) {
            event.getEffectInstance().duration = (int) (event.getEffectInstance().duration * bonus);
        }

        if (holder.is(PotionEffectTags.TO_SYNC)) {
            Networking.sendToNearbyClient(target.level(), target, new PotionSyncPacket(target.getId(), effect, event.getEffectInstance().getDuration()));
        }
    }

    @SubscribeEvent
    public static void onPotionRemove(MobEffectEvent.Remove event) {
        syncPotionRemoval(event);
    }

    @SubscribeEvent
    public static void onPotionExpire(MobEffectEvent.Expired event) {
        syncPotionRemoval(event);
    }

    private static void syncPotionRemoval(MobEffectEvent event) {
        if (event.getEntity() instanceof LivingEntity && event.getEffectInstance() != null && !event.getEntity().level.isClientSide) {
            LivingEntity target = event.getEntity();
            Holder<MobEffect> holder = event.getEffectInstance().getEffect();
            MobEffect effect = holder.value();
            if (holder.is(PotionEffectTags.TO_SYNC)) {
                Networking.sendToNearbyClient(target.level(), target, new PotionSyncPacket(target.getId(), effect, -1));
            }
        }
    }

    @SubscribeEvent
    public static void treeGrow(SuccessfulTreeGrowthEvent event) {
        Set<UUID> sprigs = Whirlisprig.WHIRLI_MAP.getEntities(event.level);
        List<UUID> sprigsToRemove = new ArrayList<>();

        for (UUID uuid : sprigs) {
            if (event.level.getEntity(uuid) instanceof Whirlisprig whirlisprig) {
                if (BlockUtil.distanceFrom(whirlisprig.blockPosition(), event.pos) <= 10 && !whirlisprig.isTamed()) {
                    whirlisprig.droppingShards = true;
                }
            } else {
                sprigsToRemove.add(uuid);
            }
        }
        for (UUID uuid : sprigsToRemove) {
            Whirlisprig.WHIRLI_MAP.removeEntity(event.level, uuid);
        }
    }

    @SubscribeEvent
    public static void endDupePatch(EntityTravelToDimensionEvent event) {
        if (event.getDimension() == Level.END) {
            if (event.getEntity() instanceof EnchantedFallingBlock) {
                event.setCanceled(true);
            }
        }
    }

    private EventHandler() {
    }

}
