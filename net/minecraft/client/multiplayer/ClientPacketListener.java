package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.DemoIntroScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.dialog.DialogConnectionAccess;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.inventory.NautilusInventoryScreen;
import net.minecraft.client.gui.screens.inventory.TestInstanceBlockEditScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeFlyingSoundInstance;
import net.minecraft.client.resources.sounds.BeeSoundInstance;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.SnifferSoundInstance;
import net.minecraft.client.waypoints.ClientWaypointManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.Connection;
import net.minecraft.network.HashedPatchMap;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessagesTracker;
import net.minecraft.network.chat.LocalChatSession;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.chat.SignedMessageLink;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchFinishedPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchStartPacket;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundDebugBlockValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugChunkValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugEntityValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugEventPacket;
import net.minecraft.network.protocol.game.ClientboundDebugSamplePacket;
import net.minecraft.network.protocol.game.ClientboundDeleteChatPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameTestHighlightPosPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundMountScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.minecraft.network.protocol.game.ClientboundProjectilePowerPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookAddPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookRemovePacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookSettingsPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTestInstanceBlockStatus;
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket;
import net.minecraft.network.protocol.game.ClientboundTickingStepPacket;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundChunkBatchReceivedPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundConfigurationAcknowledgedPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.Crypt;
import net.minecraft.util.HashOps;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractMountInventoryMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.NautilusInventoryMenu;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientPacketListener extends ClientCommonPacketListenerImpl implements ClientGamePacketListener, TickablePacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Component UNSECURE_SERVER_TOAST_TITLE = Component.translatable("multiplayer.unsecureserver.toast.title");
    private static final Component UNSERURE_SERVER_TOAST = Component.translatable("multiplayer.unsecureserver.toast");
    private static final Component INVALID_PACKET = Component.translatable("multiplayer.disconnect.invalid_packet");
    private static final Component RECONFIGURE_SCREEN_MESSAGE = Component.translatable("connect.reconfiguring");
    private static final Component BAD_CHAT_INDEX = Component.translatable("multiplayer.disconnect.bad_chat_index");
    private static final Component COMMAND_SEND_CONFIRM_TITLE = Component.translatable("multiplayer.confirm_command.title");
    private static final Component BUTTON_RUN_COMMAND = Component.translatable("multiplayer.confirm_command.run_command");
    private static final Component BUTTON_SUGGEST_COMMAND = Component.translatable("multiplayer.confirm_command.suggest_command");
    private static final int PENDING_OFFSET_THRESHOLD = 64;
    public static final int TELEPORT_INTERPOLATION_THRESHOLD = 64;
    private static final Permission RESTRICTED_COMMAND = Permission.Atom.create("client/commands/restricted");
    static final PermissionCheck RESTRICTED_COMMAND_CHECK = new PermissionCheck.Require(RESTRICTED_COMMAND);
    private static final PermissionSet ALLOW_RESTRICTED_COMMANDS = p_454181_ -> p_454181_.equals(RESTRICTED_COMMAND);
    private static final ClientboundCommandsPacket.NodeBuilder<ClientSuggestionProvider> COMMAND_NODE_BUILDER = new ClientboundCommandsPacket.NodeBuilder<ClientSuggestionProvider>() {
        @Override
        public ArgumentBuilder<ClientSuggestionProvider, ?> createLiteral(String p_426062_) {
            return LiteralArgumentBuilder.literal(p_426062_);
        }

        @Override
        public ArgumentBuilder<ClientSuggestionProvider, ?> createArgument(String p_425699_, ArgumentType<?> p_426176_, @Nullable Identifier p_469833_) {
            RequiredArgumentBuilder<ClientSuggestionProvider, ?> requiredargumentbuilder = RequiredArgumentBuilder.argument(p_425699_, p_426176_);
            if (p_469833_ != null) {
                requiredargumentbuilder.suggests(SuggestionProviders.getProvider(p_469833_));
            }

            return requiredargumentbuilder;
        }

        @Override
        public ArgumentBuilder<ClientSuggestionProvider, ?> configure(
            ArgumentBuilder<ClientSuggestionProvider, ?> p_426126_, boolean p_425940_, boolean p_425720_
        ) {
            if (p_425940_) {
                p_426126_.executes(p_425695_ -> 0);
            }

            if (p_425720_) {
                p_426126_.requires(Commands.hasPermission(ClientPacketListener.RESTRICTED_COMMAND_CHECK));
            }

            return p_426126_;
        }
    };
    private final GameProfile localGameProfile;
    private ClientLevel level;
    private ClientLevel.ClientLevelData levelData;
    private final Map<UUID, PlayerInfo> playerInfoMap = Maps.newHashMap();
    private final Set<PlayerInfo> listedPlayers = new ReferenceOpenHashSet<>();
    private final ClientAdvancements advancements;
    private final ClientSuggestionProvider suggestionsProvider;
    private final ClientSuggestionProvider restrictedSuggestionsProvider;
    private final DebugQueryHandler debugQueryHandler = new DebugQueryHandler(this);
    private int serverChunkRadius = 3;
    private int serverSimulationDistance = 3;
    private final RandomSource random = RandomSource.createThreadSafe();
    public CommandDispatcher<ClientSuggestionProvider> commands = new CommandDispatcher<>();
    private ClientRecipeContainer recipes = new ClientRecipeContainer(Map.of(), SelectableRecipe.SingleInputSet.empty());
    private final UUID id = UUID.randomUUID();
    private Set<ResourceKey<Level>> levels;
    private final RegistryAccess.Frozen registryAccess;
    private final FeatureFlagSet enabledFeatures;
    private final PotionBrewing potionBrewing;
    private FuelValues fuelValues;
    private final HashedPatchMap.HashGenerator decoratedHashOpsGenerator;
    private OptionalInt removedPlayerVehicleId = OptionalInt.empty();
    private @Nullable LocalChatSession chatSession;
    private SignedMessageChain.Encoder signedMessageEncoder = SignedMessageChain.Encoder.UNSIGNED;
    private int nextChatIndex;
    private LastSeenMessagesTracker lastSeenMessages = new LastSeenMessagesTracker(20);
    private MessageSignatureCache messageSignatureCache = MessageSignatureCache.createDefault();
    private @Nullable CompletableFuture<Optional<ProfileKeyPair>> keyPairFuture;
    private @Nullable ClientInformation remoteClientInformation;
    private final ChunkBatchSizeCalculator chunkBatchSizeCalculator = new ChunkBatchSizeCalculator();
    private final PingDebugMonitor pingDebugMonitor;
    private final ClientDebugSubscriber debugSubscriber;
    private net.neoforged.neoforge.network.connection.ConnectionType connectionType;
    private @Nullable LevelLoadTracker levelLoadTracker;
    private boolean serverEnforcesSecureChat;
    private volatile boolean closed;
    private final Scoreboard scoreboard = new Scoreboard();
    private final ClientWaypointManager waypointManager = new ClientWaypointManager();
    private final SessionSearchTrees searchTrees = new SessionSearchTrees();
    private final List<WeakReference<CacheSlot<?, ?>>> cacheSlots = new ArrayList<>();
    private boolean clientLoaded;

    public ClientPacketListener(Minecraft p_253924_, Connection p_253614_, CommonListenerCookie p_295121_) {
        super(p_253924_, p_253614_, p_295121_);
        this.localGameProfile = p_295121_.localGameProfile();
        this.registryAccess = p_295121_.receivedRegistries();
        RegistryOps<HashCode> registryops = this.registryAccess.createSerializationContext(HashOps.CRC32C_INSTANCE);
        this.decoratedHashOpsGenerator = p_412017_ -> p_412017_.encodeValue(registryops)
            .getOrThrow(p_412015_ -> new IllegalArgumentException("Failed to hash " + p_412017_ + ": " + p_412015_))
            .asInt();
        this.enabledFeatures = p_295121_.enabledFeatures();
        this.advancements = new ClientAdvancements(p_253924_, this.telemetryManager);
        PermissionSet permissionset = p_454183_ -> {
            LocalPlayer localplayer = p_253924_.player;
            return localplayer != null && localplayer.permissions().hasPermission(p_454183_);
        };
        this.suggestionsProvider = new ClientSuggestionProvider(this, p_253924_, permissionset.union(ALLOW_RESTRICTED_COMMANDS));
        this.restrictedSuggestionsProvider = new ClientSuggestionProvider(this, p_253924_, PermissionSet.NO_PERMISSIONS);
        this.pingDebugMonitor = new PingDebugMonitor(this, p_253924_.getDebugOverlay().getPingLogger());
        this.debugSubscriber = new ClientDebugSubscriber(this, p_253924_.getDebugOverlay());
        if (p_295121_.chatState() != null) {
            p_253924_.gui.getChat().restoreState(p_295121_.chatState());
        }

        this.connectionType = p_295121_.connectionType();
        this.potionBrewing = PotionBrewing.bootstrap(this.enabledFeatures, this.registryAccess);
        this.fuelValues = FuelValues.vanillaBurnTimes(p_295121_.receivedRegistries(), this.enabledFeatures);
        this.levelLoadTracker = p_295121_.levelLoadTracker();
    }

    public ClientSuggestionProvider getSuggestionsProvider() {
        return this.suggestionsProvider;
    }

    public void close() {
        this.closed = true;
        this.clearLevel();
        this.telemetryManager.onDisconnect();
    }

    public void clearLevel() {
        this.clearCacheSlots();
        this.level = null;
        this.levelLoadTracker = null;
    }

    private void clearCacheSlots() {
        for (WeakReference<CacheSlot<?, ?>> weakreference : this.cacheSlots) {
            CacheSlot<?, ?> cacheslot = weakreference.get();
            if (cacheslot != null) {
                cacheslot.clear();
            }
        }

        this.cacheSlots.clear();
    }

    public RecipeAccess recipes() {
        return this.recipes;
    }

    @Override
    public void handleLogin(ClientboundLoginPacket p_105030_) {
        PacketUtils.ensureRunningOnSameThread(p_105030_, this, this.minecraft.packetProcessor());
        this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
        CommonPlayerSpawnInfo commonplayerspawninfo = p_105030_.commonPlayerSpawnInfo();
        List<ResourceKey<Level>> list = Lists.newArrayList(p_105030_.levels());
        Collections.shuffle(list);
        this.levels = Sets.newLinkedHashSet(list);
        ResourceKey<Level> resourcekey = commonplayerspawninfo.dimension();
        Holder<DimensionType> holder = commonplayerspawninfo.dimensionType();
        this.serverChunkRadius = p_105030_.chunkRadius();
        this.serverSimulationDistance = p_105030_.simulationDistance();
        boolean flag = commonplayerspawninfo.isDebug();
        boolean flag1 = commonplayerspawninfo.isFlat();
        int i = commonplayerspawninfo.seaLevel();
        ClientLevel.ClientLevelData clientlevel$clientleveldata = new ClientLevel.ClientLevelData(Difficulty.NORMAL, p_105030_.hardcore(), flag1);
        this.levelData = clientlevel$clientleveldata;
        this.level = new ClientLevel(
            this,
            clientlevel$clientleveldata,
            resourcekey,
            holder,
            this.serverChunkRadius,
            this.serverSimulationDistance,
            this.minecraft.levelRenderer,
            flag,
            commonplayerspawninfo.seed(),
            i
        );
        this.minecraft.setLevel(this.level);
        if (this.minecraft.player == null) {
            this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook());
            this.minecraft.player.setYRot(-180.0F);
            if (this.minecraft.getSingleplayerServer() != null) {
                this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
            }
        }

        this.setClientLoaded(false);
        this.debugSubscriber.clear();
        this.minecraft.levelRenderer.debugRenderer.refreshRendererList();
        this.minecraft.player.resetPos();
        net.neoforged.neoforge.client.ClientHooks.firePlayerLogin(this.minecraft.gameMode, this.minecraft.player, this.minecraft.getConnection().connection);
        this.minecraft.player.setId(p_105030_.playerId());
        this.level.addEntity(this.minecraft.player);
        this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
        this.minecraft.setCameraEntity(this.minecraft.player);
        this.startWaitingForNewLevel(this.minecraft.player, this.level, LevelLoadingScreen.Reason.OTHER, null, null);
        this.minecraft.player.setReducedDebugInfo(p_105030_.reducedDebugInfo());
        this.minecraft.player.setShowDeathScreen(p_105030_.showDeathScreen());
        this.minecraft.player.setDoLimitedCrafting(p_105030_.doLimitedCrafting());
        this.minecraft.player.setLastDeathLocation(commonplayerspawninfo.lastDeathLocation());
        this.minecraft.player.setPortalCooldown(commonplayerspawninfo.portalCooldown());
        this.minecraft.gameMode.setLocalMode(commonplayerspawninfo.gameType(), commonplayerspawninfo.previousGameType());
        this.minecraft.options.setServerRenderDistance(p_105030_.chunkRadius());
        this.chatSession = null;
        this.signedMessageEncoder = SignedMessageChain.Encoder.UNSIGNED;
        this.nextChatIndex = 0;
        this.lastSeenMessages = new LastSeenMessagesTracker(20);
        this.messageSignatureCache = MessageSignatureCache.createDefault();
        if (this.connection.isEncrypted()) {
            this.prepareKeyPair();
        }

        this.telemetryManager.onPlayerInfoReceived(commonplayerspawninfo.gameType(), p_105030_.hardcore());
        this.minecraft.quickPlayLog().log(this.minecraft);
        this.serverEnforcesSecureChat = p_105030_.enforcesSecureChat();
        if (this.serverData != null && !this.seenInsecureChatWarning && !this.enforcesSecureChat()) {
            SystemToast systemtoast = SystemToast.multiline(
                this.minecraft, SystemToast.SystemToastId.UNSECURE_SERVER_WARNING, UNSECURE_SERVER_TOAST_TITLE, UNSERURE_SERVER_TOAST
            );
            this.minecraft.getToastManager().addToast(systemtoast);
            this.seenInsecureChatWarning = true;
        }
    }

    @Override
    public void handleAddEntity(ClientboundAddEntityPacket p_104958_) {
        PacketUtils.ensureRunningOnSameThread(p_104958_, this, this.minecraft.packetProcessor());
        if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == p_104958_.getId()) {
            this.removedPlayerVehicleId = OptionalInt.empty();
        }

        Entity entity = this.createEntityFromPacket(p_104958_);
        if (entity != null) {
            entity.recreateFromPacket(p_104958_);
            this.level.addEntity(entity);
            this.postAddEntitySoundInstance(entity);
        } else {
            LOGGER.warn("Skipping Entity with id {}", p_104958_.getType());
        }

        if (entity instanceof Player player) {
            UUID uuid = player.getUUID();
            PlayerInfo playerinfo = this.playerInfoMap.get(uuid);
            if (playerinfo != null) {
                this.seenPlayers.put(uuid, playerinfo);
            }
        }
    }

    private @Nullable Entity createEntityFromPacket(ClientboundAddEntityPacket p_302232_) {
        EntityType<?> entitytype = p_302232_.getType();
        if (entitytype == EntityType.PLAYER) {
            PlayerInfo playerinfo = this.getPlayerInfo(p_302232_.getUUID());
            if (playerinfo == null) {
                LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", p_302232_.getUUID());
                return null;
            } else {
                return new RemotePlayer(this.level, playerinfo.getProfile());
            }
        } else {
            return entitytype.create(this.level, EntitySpawnReason.LOAD);
        }
    }

    private void postAddEntitySoundInstance(Entity p_233664_) {
        if (p_233664_ instanceof AbstractMinecart abstractminecart) {
            this.minecraft.getSoundManager().play(new MinecartSoundInstance(abstractminecart));
        } else if (p_233664_ instanceof Bee bee) {
            boolean flag = bee.isAngry();
            BeeSoundInstance beesoundinstance;
            if (flag) {
                beesoundinstance = new BeeAggressiveSoundInstance(bee);
            } else {
                beesoundinstance = new BeeFlyingSoundInstance(bee);
            }

            this.minecraft.getSoundManager().queueTickingSound(beesoundinstance);
        }
    }

    @Override
    public void handleSetEntityMotion(ClientboundSetEntityMotionPacket p_105092_) {
        PacketUtils.ensureRunningOnSameThread(p_105092_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_105092_.getId());
        if (entity != null) {
            entity.lerpMotion(p_105092_.getMovement());
        }
    }

    @Override
    public void handleSetEntityData(ClientboundSetEntityDataPacket p_105088_) {
        PacketUtils.ensureRunningOnSameThread(p_105088_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_105088_.id());
        if (entity != null) {
            entity.getEntityData().assignValues(p_105088_.packedItems());
        }
    }

    @Override
    public void handleEntityPositionSync(ClientboundEntityPositionSyncPacket p_380347_) {
        PacketUtils.ensureRunningOnSameThread(p_380347_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_380347_.id());
        if (entity != null) {
            Vec3 vec3 = p_380347_.values().position();
            entity.getPositionCodec().setBase(vec3);
            if (!entity.isLocalInstanceAuthoritative()) {
                float f = p_380347_.values().yRot();
                float f1 = p_380347_.values().xRot();
                boolean flag = entity.position().distanceToSqr(vec3) > 4096.0;
                if (this.level.isTickingEntity(entity) && !flag) {
                    entity.moveOrInterpolateTo(vec3, f, f1);
                } else {
                    entity.snapTo(vec3, f, f1);
                }

                if (!entity.isInterpolating() && entity.hasIndirectPassenger(this.minecraft.player)) {
                    entity.positionRider(this.minecraft.player);
                    this.minecraft.player.setOldPosAndRot();
                }

                entity.setOnGround(p_380347_.onGround());
            }
        }
    }

    @Override
    public void handleTeleportEntity(ClientboundTeleportEntityPacket p_105124_) {
        PacketUtils.ensureRunningOnSameThread(p_105124_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_105124_.id());
        if (entity == null) {
            if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == p_105124_.id()) {
                LOGGER.debug("Trying to teleport entity with id {}, that was formerly player vehicle, applying teleport to player instead", p_105124_.id());
                setValuesFromPositionPacket(p_105124_.change(), p_105124_.relatives(), this.minecraft.player, false);
                this.connection
                    .send(
                        new ServerboundMovePlayerPacket.PosRot(
                            this.minecraft.player.getX(),
                            this.minecraft.player.getY(),
                            this.minecraft.player.getZ(),
                            this.minecraft.player.getYRot(),
                            this.minecraft.player.getXRot(),
                            false,
                            false
                        )
                    );
            }
        } else {
            boolean flag = p_105124_.relatives().contains(Relative.X)
                || p_105124_.relatives().contains(Relative.Y)
                || p_105124_.relatives().contains(Relative.Z);
            boolean flag1 = this.level.isTickingEntity(entity) || !entity.isLocalInstanceAuthoritative() || flag;
            boolean flag2 = setValuesFromPositionPacket(p_105124_.change(), p_105124_.relatives(), entity, flag1);
            entity.setOnGround(p_105124_.onGround());
            if (!flag2 && entity.hasIndirectPassenger(this.minecraft.player)) {
                entity.positionRider(this.minecraft.player);
                this.minecraft.player.setOldPosAndRot();
                if (entity.isLocalInstanceAuthoritative()) {
                    this.connection.send(ServerboundMoveVehiclePacket.fromEntity(entity));
                }
            }
        }
    }

    @Override
    public void handleTickingState(ClientboundTickingStatePacket p_309203_) {
        PacketUtils.ensureRunningOnSameThread(p_309203_, this, this.minecraft.packetProcessor());
        if (this.minecraft.level != null) {
            TickRateManager tickratemanager = this.minecraft.level.tickRateManager();
            tickratemanager.setTickRate(p_309203_.tickRate());
            tickratemanager.setFrozen(p_309203_.isFrozen());
        }
    }

    @Override
    public void handleTickingStep(ClientboundTickingStepPacket p_308901_) {
        PacketUtils.ensureRunningOnSameThread(p_308901_, this, this.minecraft.packetProcessor());
        if (this.minecraft.level != null) {
            TickRateManager tickratemanager = this.minecraft.level.tickRateManager();
            tickratemanager.setFrozenTicksToRun(p_308901_.tickSteps());
        }
    }

    @Override
    public void handleSetHeldSlot(ClientboundSetHeldSlotPacket p_364031_) {
        PacketUtils.ensureRunningOnSameThread(p_364031_, this, this.minecraft.packetProcessor());
        if (Inventory.isHotbarSlot(p_364031_.slot())) {
            this.minecraft.player.getInventory().setSelectedSlot(p_364031_.slot());
        }
    }

    @Override
    public void handleMoveEntity(ClientboundMoveEntityPacket p_105036_) {
        PacketUtils.ensureRunningOnSameThread(p_105036_, this, this.minecraft.packetProcessor());
        Entity entity = p_105036_.getEntity(this.level);
        if (entity != null) {
            if (entity.isLocalInstanceAuthoritative()) {
                VecDeltaCodec vecdeltacodec1 = entity.getPositionCodec();
                Vec3 vec31 = vecdeltacodec1.decode(p_105036_.getXa(), p_105036_.getYa(), p_105036_.getZa());
                vecdeltacodec1.setBase(vec31);
            } else {
                if (p_105036_.hasPosition()) {
                    VecDeltaCodec vecdeltacodec = entity.getPositionCodec();
                    Vec3 vec3 = vecdeltacodec.decode(p_105036_.getXa(), p_105036_.getYa(), p_105036_.getZa());
                    vecdeltacodec.setBase(vec3);
                    if (p_105036_.hasRotation()) {
                        entity.moveOrInterpolateTo(vec3, p_105036_.getYRot(), p_105036_.getXRot());
                    } else {
                        entity.moveOrInterpolateTo(vec3);
                    }
                } else if (p_105036_.hasRotation()) {
                    entity.moveOrInterpolateTo(p_105036_.getYRot(), p_105036_.getXRot());
                }

                entity.setOnGround(p_105036_.isOnGround());
            }
        }
    }

    @Override
    public void handleMinecartAlongTrack(ClientboundMoveMinecartPacket p_363199_) {
        PacketUtils.ensureRunningOnSameThread(p_363199_, this, this.minecraft.packetProcessor());
        if (p_363199_.getEntity(this.level) instanceof AbstractMinecart abstractminecart) {
            if (abstractminecart.getBehavior() instanceof NewMinecartBehavior newminecartbehavior) {
                newminecartbehavior.lerpSteps.addAll(p_363199_.lerpSteps());
            }
        }
    }

    @Override
    public void handleRotateMob(ClientboundRotateHeadPacket p_105068_) {
        PacketUtils.ensureRunningOnSameThread(p_105068_, this, this.minecraft.packetProcessor());
        Entity entity = p_105068_.getEntity(this.level);
        if (entity != null) {
            entity.lerpHeadTo(p_105068_.getYHeadRot(), 3);
        }
    }

    @Override
    public void handleRemoveEntities(ClientboundRemoveEntitiesPacket p_182633_) {
        PacketUtils.ensureRunningOnSameThread(p_182633_, this, this.minecraft.packetProcessor());
        p_182633_.getEntityIds().forEach(p_448696_ -> {
            Entity entity = this.level.getEntity(p_448696_);
            if (entity != null) {
                if (entity.hasIndirectPassenger(this.minecraft.player)) {
                    LOGGER.debug("Remove entity {}:{} that has player as passenger", entity.getType(), p_448696_);
                    this.removedPlayerVehicleId = OptionalInt.of(p_448696_);
                }

                this.level.removeEntity(p_448696_, Entity.RemovalReason.DISCARDED);
                this.debugSubscriber.dropEntity(entity);
            }
        });
    }

    @Override
    public void handleMovePlayer(ClientboundPlayerPositionPacket p_105056_) {
        PacketUtils.ensureRunningOnSameThread(p_105056_, this, this.minecraft.packetProcessor());
        Player player = this.minecraft.player;
        if (!player.isPassenger()) {
            setValuesFromPositionPacket(p_105056_.change(), p_105056_.relatives(), player, false);
        }

        this.connection.send(new ServerboundAcceptTeleportationPacket(p_105056_.id()));
        this.connection
            .send(new ServerboundMovePlayerPacket.PosRot(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), false, false));
    }

    private static boolean setValuesFromPositionPacket(PositionMoveRotation p_380237_, Set<Relative> p_380116_, Entity p_379700_, boolean p_379954_) {
        PositionMoveRotation positionmoverotation = PositionMoveRotation.of(p_379700_);
        PositionMoveRotation positionmoverotation1 = PositionMoveRotation.calculateAbsolute(positionmoverotation, p_380237_, p_380116_);
        boolean flag = positionmoverotation.position().distanceToSqr(positionmoverotation1.position()) > 4096.0;
        if (p_379954_ && !flag) {
            p_379700_.moveOrInterpolateTo(positionmoverotation1.position(), positionmoverotation1.yRot(), positionmoverotation1.xRot());
            p_379700_.setDeltaMovement(positionmoverotation1.deltaMovement());
            return true;
        } else {
            p_379700_.setPos(positionmoverotation1.position());
            p_379700_.setDeltaMovement(positionmoverotation1.deltaMovement());
            p_379700_.setYRot(positionmoverotation1.yRot());
            p_379700_.setXRot(positionmoverotation1.xRot());
            PositionMoveRotation positionmoverotation2 = new PositionMoveRotation(p_379700_.oldPosition(), Vec3.ZERO, p_379700_.yRotO, p_379700_.xRotO);
            PositionMoveRotation positionmoverotation3 = PositionMoveRotation.calculateAbsolute(positionmoverotation2, p_380237_, p_380116_);
            p_379700_.setOldPosAndRot(positionmoverotation3.position(), positionmoverotation3.yRot(), positionmoverotation3.xRot());
            return false;
        }
    }

    @Override
    public void handleRotatePlayer(ClientboundPlayerRotationPacket p_379872_) {
        PacketUtils.ensureRunningOnSameThread(p_379872_, this, this.minecraft.packetProcessor());
        Player player = this.minecraft.player;
        Set<Relative> set = Relative.rotation(p_379872_.relativeY(), p_379872_.relativeX());
        PositionMoveRotation positionmoverotation = PositionMoveRotation.of(player);
        PositionMoveRotation positionmoverotation1 = PositionMoveRotation.calculateAbsolute(
            positionmoverotation, positionmoverotation.withRotation(p_379872_.yRot(), p_379872_.xRot()), set
        );
        player.setYRot(positionmoverotation1.yRot());
        player.setXRot(positionmoverotation1.xRot());
        player.setOldRot();
        this.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), false, false));
    }

    @Override
    public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket p_105070_) {
        PacketUtils.ensureRunningOnSameThread(p_105070_, this, this.minecraft.packetProcessor());
        p_105070_.runUpdates((p_284633_, p_284634_) -> this.level.setServerVerifiedBlockState(p_284633_, p_284634_, 19));
    }

    @Override
    public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket p_194241_) {
        PacketUtils.ensureRunningOnSameThread(p_194241_, this, this.minecraft.packetProcessor());
        int i = p_194241_.getX();
        int j = p_194241_.getZ();
        this.updateLevelChunk(i, j, p_194241_.getChunkData());
        ClientboundLightUpdatePacketData clientboundlightupdatepacketdata = p_194241_.getLightData();
        this.level.queueLightUpdate(() -> {
            this.applyLightData(i, j, clientboundlightupdatepacketdata, false);
            LevelChunk levelchunk = this.level.getChunkSource().getChunk(i, j, false);
            if (levelchunk != null) {
                this.enableChunkLight(levelchunk, i, j);
                this.minecraft.levelRenderer.onChunkReadyToRender(levelchunk.getPos());
            }
        });
    }

    @Override
    public void handleChunksBiomes(ClientboundChunksBiomesPacket p_275437_) {
        PacketUtils.ensureRunningOnSameThread(p_275437_, this, this.minecraft.packetProcessor());

        for (ClientboundChunksBiomesPacket.ChunkBiomeData clientboundchunksbiomespacket$chunkbiomedata : p_275437_.chunkBiomeData()) {
            this.level
                .getChunkSource()
                .replaceBiomes(
                    clientboundchunksbiomespacket$chunkbiomedata.pos().x,
                    clientboundchunksbiomespacket$chunkbiomedata.pos().z,
                    clientboundchunksbiomespacket$chunkbiomedata.getReadBuffer()
                );
        }

        for (ClientboundChunksBiomesPacket.ChunkBiomeData clientboundchunksbiomespacket$chunkbiomedata1 : p_275437_.chunkBiomeData()) {
            this.level
                .onChunkLoaded(new ChunkPos(clientboundchunksbiomespacket$chunkbiomedata1.pos().x, clientboundchunksbiomespacket$chunkbiomedata1.pos().z));
        }

        for (ClientboundChunksBiomesPacket.ChunkBiomeData clientboundchunksbiomespacket$chunkbiomedata2 : p_275437_.chunkBiomeData()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = this.level.getMinSectionY(); k <= this.level.getMaxSectionY(); k++) {
                        this.minecraft
                            .levelRenderer
                            .setSectionDirty(
                                clientboundchunksbiomespacket$chunkbiomedata2.pos().x + i, k, clientboundchunksbiomespacket$chunkbiomedata2.pos().z + j
                            );
                    }
                }
            }
        }
    }

    private void updateLevelChunk(int p_194199_, int p_194200_, ClientboundLevelChunkPacketData p_194201_) {
        this.level
            .getChunkSource()
            .replaceWithPacketData(
                p_194199_, p_194200_, p_194201_.getReadBuffer(), p_194201_.getHeightmaps(), p_194201_.getBlockEntitiesTagsConsumer(p_194199_, p_194200_)
            );
    }

    private void enableChunkLight(LevelChunk p_194213_, int p_194214_, int p_194215_) {
        LevelLightEngine levellightengine = this.level.getChunkSource().getLightEngine();
        LevelChunkSection[] alevelchunksection = p_194213_.getSections();
        ChunkPos chunkpos = p_194213_.getPos();

        for (int i = 0; i < alevelchunksection.length; i++) {
            LevelChunkSection levelchunksection = alevelchunksection[i];
            int j = this.level.getSectionYFromSectionIndex(i);
            levellightengine.updateSectionStatus(SectionPos.of(chunkpos, j), levelchunksection.hasOnlyAir());
        }

        this.level.setSectionRangeDirty(p_194214_ - 1, this.level.getMinSectionY(), p_194215_ - 1, p_194214_ + 1, this.level.getMaxSectionY(), p_194215_ + 1);
    }

    @Override
    public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket p_105014_) {
        PacketUtils.ensureRunningOnSameThread(p_105014_, this, this.minecraft.packetProcessor());
        this.level.getChunkSource().drop(p_105014_.pos());
        this.debugSubscriber.dropChunk(p_105014_.pos());
        this.queueLightRemoval(p_105014_);
    }

    private void queueLightRemoval(ClientboundForgetLevelChunkPacket p_194253_) {
        ChunkPos chunkpos = p_194253_.pos();
        this.level.queueLightUpdate(() -> {
            LevelLightEngine levellightengine = this.level.getLightEngine();
            levellightengine.setLightEnabled(chunkpos, false);

            for (int i = levellightengine.getMinLightSection(); i < levellightengine.getMaxLightSection(); i++) {
                SectionPos sectionpos = SectionPos.of(chunkpos, i);
                levellightengine.queueSectionData(LightLayer.BLOCK, sectionpos, null);
                levellightengine.queueSectionData(LightLayer.SKY, sectionpos, null);
            }

            for (int j = this.level.getMinSectionY(); j <= this.level.getMaxSectionY(); j++) {
                levellightengine.updateSectionStatus(SectionPos.of(chunkpos, j), true);
            }
        });
    }

    @Override
    public void handleBlockUpdate(ClientboundBlockUpdatePacket p_104980_) {
        PacketUtils.ensureRunningOnSameThread(p_104980_, this, this.minecraft.packetProcessor());
        this.level.setServerVerifiedBlockState(p_104980_.getPos(), p_104980_.getBlockState(), 19);
    }

    @Override
    public void handleConfigurationStart(ClientboundStartConfigurationPacket p_296485_) {
        PacketUtils.ensureRunningOnSameThread(p_296485_, this, this.minecraft.packetProcessor());
        this.minecraft.getChatListener().flushQueue();
        this.sendChatAcknowledgement();
        ChatComponent.State chatcomponent$state = this.minecraft.gui.getChat().storeState();
        this.minecraft.clearClientLevel(new ServerReconfigScreen(RECONFIGURE_SCREEN_MESSAGE, this.connection));
        this.connection
            .setupInboundProtocol(
                ConfigurationProtocols.CLIENTBOUND,
                new ClientConfigurationPacketListenerImpl(
                    this.minecraft,
                    this.connection,
                    new CommonListenerCookie(
                        new LevelLoadTracker(),
                        this.localGameProfile,
                        this.telemetryManager,
                        this.registryAccess,
                        this.enabledFeatures,
                        this.serverBrand,
                        this.serverData,
                        this.postDisconnectScreen,
                        this.serverCookies,
                        chatcomponent$state,
                        this.customReportDetails,
                        this.serverLinks(),
                        this.seenPlayers,
                        this.seenInsecureChatWarning,
                        this.connectionType
                    )
                )
            );
        this.send(ServerboundConfigurationAcknowledgedPacket.INSTANCE);
        this.connection.setupOutboundProtocol(ConfigurationProtocols.SERVERBOUND);
    }

    @Override
    public void handleTakeItemEntity(ClientboundTakeItemEntityPacket p_105122_) {
        PacketUtils.ensureRunningOnSameThread(p_105122_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_105122_.getItemId());
        LivingEntity livingentity = (LivingEntity)this.level.getEntity(p_105122_.getPlayerId());
        if (livingentity == null) {
            livingentity = this.minecraft.player;
        }

        if (entity != null) {
            if (entity instanceof ExperienceOrb) {
                this.level
                    .playLocalSound(
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.PLAYERS,
                        0.1F,
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F,
                        false
                    );
            } else {
                this.level
                    .playLocalSound(
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.ITEM_PICKUP,
                        SoundSource.PLAYERS,
                        0.2F,
                        (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F,
                        false
                    );
            }

            EntityRenderState entityrenderstate = this.minecraft.getEntityRenderDispatcher().extractEntity(entity, 1.0F);
            this.minecraft.particleEngine.add(new ItemPickupParticle(this.level, entityrenderstate, livingentity, entity.getDeltaMovement()));
            if (entity instanceof ItemEntity itementity) {
                ItemStack itemstack = itementity.getItem();
                if (!itemstack.isEmpty()) {
                    itemstack.shrink(p_105122_.getAmount());
                }

                if (itemstack.isEmpty()) {
                    this.level.removeEntity(p_105122_.getItemId(), Entity.RemovalReason.DISCARDED);
                }
            } else if (!(entity instanceof ExperienceOrb)) {
                this.level.removeEntity(p_105122_.getItemId(), Entity.RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public void handleSystemChat(ClientboundSystemChatPacket p_233708_) {
        PacketUtils.ensureRunningOnSameThread(p_233708_, this, this.minecraft.packetProcessor());
        this.minecraft.getChatListener().handleSystemMessage(p_233708_.content(), p_233708_.overlay());
    }

    @Override
    public void handlePlayerChat(ClientboundPlayerChatPacket p_233702_) {
        PacketUtils.ensureRunningOnSameThread(p_233702_, this, this.minecraft.packetProcessor());
        int i = this.nextChatIndex++;
        if (p_233702_.globalIndex() != i) {
            LOGGER.error("Missing or out-of-order chat message from server, expected index {} but got {}", i, p_233702_.globalIndex());
            this.connection.disconnect(BAD_CHAT_INDEX);
        } else {
            Optional<SignedMessageBody> optional = p_233702_.body().unpack(this.messageSignatureCache);
            if (optional.isEmpty()) {
                LOGGER.error("Message from player with ID {} referenced unrecognized signature id", p_233702_.sender());
                this.connection.disconnect(INVALID_PACKET);
            } else {
                this.messageSignatureCache.push(optional.get(), p_233702_.signature());
                UUID uuid = p_233702_.sender();
                PlayerInfo playerinfo = this.getPlayerInfo(uuid);
                if (playerinfo == null) {
                    LOGGER.error("Received player chat packet for unknown player with ID: {}", uuid);
                    this.minecraft.getChatListener().handleChatMessageError(uuid, p_233702_.signature(), p_233702_.chatType());
                } else {
                    RemoteChatSession remotechatsession = playerinfo.getChatSession();
                    SignedMessageLink signedmessagelink;
                    if (remotechatsession != null) {
                        signedmessagelink = new SignedMessageLink(p_233702_.index(), uuid, remotechatsession.sessionId());
                    } else {
                        signedmessagelink = SignedMessageLink.unsigned(uuid);
                    }

                    PlayerChatMessage playerchatmessage = new PlayerChatMessage(
                        signedmessagelink, p_233702_.signature(), optional.get(), p_233702_.unsignedContent(), p_233702_.filterMask()
                    );
                    playerchatmessage = playerinfo.getMessageValidator().updateAndValidate(playerchatmessage);
                    if (playerchatmessage != null) {
                        this.minecraft.getChatListener().handlePlayerChatMessage(playerchatmessage, playerinfo.getProfile(), p_233702_.chatType());
                    } else {
                        this.minecraft.getChatListener().handleChatMessageError(uuid, p_233702_.signature(), p_233702_.chatType());
                    }
                }
            }
        }
    }

    @Override
    public void handleDisguisedChat(ClientboundDisguisedChatPacket p_251920_) {
        PacketUtils.ensureRunningOnSameThread(p_251920_, this, this.minecraft.packetProcessor());
        this.minecraft.getChatListener().handleDisguisedChatMessage(p_251920_.message(), p_251920_.chatType());
    }

    @Override
    public void handleDeleteChat(ClientboundDeleteChatPacket p_241325_) {
        PacketUtils.ensureRunningOnSameThread(p_241325_, this, this.minecraft.packetProcessor());
        Optional<MessageSignature> optional = p_241325_.messageSignature().unpack(this.messageSignatureCache);
        if (optional.isEmpty()) {
            this.connection.disconnect(INVALID_PACKET);
        } else {
            this.lastSeenMessages.ignorePending(optional.get());
            if (!this.minecraft.getChatListener().removeFromDelayedMessageQueue(optional.get())) {
                this.minecraft.gui.getChat().deleteMessage(optional.get());
            }
        }
    }

    @Override
    public void handleAnimate(ClientboundAnimatePacket p_104968_) {
        PacketUtils.ensureRunningOnSameThread(p_104968_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_104968_.getId());
        if (entity != null) {
            if (p_104968_.getAction() == 0) {
                LivingEntity livingentity = (LivingEntity)entity;
                livingentity.swing(InteractionHand.MAIN_HAND);
            } else if (p_104968_.getAction() == 3) {
                LivingEntity livingentity1 = (LivingEntity)entity;
                livingentity1.swing(InteractionHand.OFF_HAND);
            } else if (p_104968_.getAction() == 2) {
                Player player = (Player)entity;
                player.stopSleepInBed(false, false);
            } else if (p_104968_.getAction() == 4) {
                this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.CRIT);
            } else if (p_104968_.getAction() == 5) {
                this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.ENCHANTED_HIT);
            }
        }
    }

    @Override
    public void handleHurtAnimation(ClientboundHurtAnimationPacket p_265581_) {
        PacketUtils.ensureRunningOnSameThread(p_265581_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_265581_.id());
        if (entity != null) {
            entity.animateHurt(p_265581_.yaw());
        }
    }

    @Override
    public void handleSetTime(ClientboundSetTimePacket p_105108_) {
        PacketUtils.ensureRunningOnSameThread(p_105108_, this, this.minecraft.packetProcessor());
        this.level.setTimeFromServer(p_105108_.gameTime(), p_105108_.dayTime(), p_105108_.tickDayTime());
        this.telemetryManager.setTime(p_105108_.gameTime());
    }

    @Override
    public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket p_105084_) {
        PacketUtils.ensureRunningOnSameThread(p_105084_, this, this.minecraft.packetProcessor());
        this.minecraft.level.setRespawnData(p_105084_.respawnData());
    }

    @Override
    public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket p_105102_) {
        PacketUtils.ensureRunningOnSameThread(p_105102_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_105102_.getVehicle());
        if (entity == null) {
            LOGGER.warn("Received passengers for unknown entity");
        } else {
            boolean flag = entity.hasIndirectPassenger(this.minecraft.player);
            entity.ejectPassengers();

            for (int i : p_105102_.getPassengers()) {
                Entity entity1 = this.level.getEntity(i);
                if (entity1 != null) {
                    entity1.startRiding(entity, true, false);
                    if (entity1 == this.minecraft.player) {
                        this.removedPlayerVehicleId = OptionalInt.empty();
                        if (!flag) {
                            if (entity instanceof AbstractBoat) {
                                this.minecraft.player.yRotO = entity.getYRot();
                                this.minecraft.player.setYRot(entity.getYRot());
                                this.minecraft.player.setYHeadRot(entity.getYRot());
                            }

                            Component component = Component.translatable("mount.onboard", this.minecraft.options.keyShift.getTranslatedKeyMessage());
                            this.minecraft.gui.setOverlayMessage(component, false);
                            this.minecraft.getNarrator().saySystemNow(component);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket p_105090_) {
        PacketUtils.ensureRunningOnSameThread(p_105090_, this, this.minecraft.packetProcessor());
        if (this.level.getEntity(p_105090_.getSourceId()) instanceof Leashable leashable) {
            leashable.setDelayedLeashHolderId(p_105090_.getDestId());
        }
    }

    private static ItemStack findTotem(Player p_104928_) {
        for (InteractionHand interactionhand : InteractionHand.values()) {
            ItemStack itemstack = p_104928_.getItemInHand(interactionhand);
            if (itemstack.has(DataComponents.DEATH_PROTECTION)) {
                return itemstack;
            }
        }

        return new ItemStack(Items.TOTEM_OF_UNDYING);
    }

    @Override
    public void handleEntityEvent(ClientboundEntityEventPacket p_105010_) {
        PacketUtils.ensureRunningOnSameThread(p_105010_, this, this.minecraft.packetProcessor());
        Entity entity = p_105010_.getEntity(this.level);
        if (entity != null) {
            switch (p_105010_.getEventId()) {
                case 21:
                    this.minecraft.getSoundManager().play(new GuardianAttackSoundInstance((Guardian)entity));
                    break;
                case 35:
                    int i = 40;
                    this.minecraft.particleEngine.createTrackingEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                    this.level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
                    if (entity == this.minecraft.player) {
                        this.minecraft.gameRenderer.displayItemActivation(findTotem(this.minecraft.player));
                    }
                    break;
                case 63:
                    this.minecraft.getSoundManager().play(new SnifferSoundInstance((Sniffer)entity));
                    break;
                default:
                    entity.handleEntityEvent(p_105010_.getEventId());
            }
        }
    }

    @Override
    public void handleDamageEvent(ClientboundDamageEventPacket p_270800_) {
        PacketUtils.ensureRunningOnSameThread(p_270800_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_270800_.entityId());
        if (entity != null) {
            entity.handleDamageEvent(p_270800_.getSource(this.level));
        }
    }

    @Override
    public void handleSetHealth(ClientboundSetHealthPacket p_105098_) {
        PacketUtils.ensureRunningOnSameThread(p_105098_, this, this.minecraft.packetProcessor());
        this.minecraft.player.hurtTo(p_105098_.getHealth());
        this.minecraft.player.getFoodData().setFoodLevel(p_105098_.getFood());
        this.minecraft.player.getFoodData().setSaturation(p_105098_.getSaturation());
    }

    @Override
    public void handleSetExperience(ClientboundSetExperiencePacket p_105096_) {
        PacketUtils.ensureRunningOnSameThread(p_105096_, this, this.minecraft.packetProcessor());
        this.minecraft.player.setExperienceValues(p_105096_.getExperienceProgress(), p_105096_.getTotalExperience(), p_105096_.getExperienceLevel());
    }

    @Override
    public void handleRespawn(ClientboundRespawnPacket p_105066_) {
        PacketUtils.ensureRunningOnSameThread(p_105066_, this, this.minecraft.packetProcessor());
        CommonPlayerSpawnInfo commonplayerspawninfo = p_105066_.commonPlayerSpawnInfo();
        ResourceKey<Level> resourcekey = commonplayerspawninfo.dimension();
        Holder<DimensionType> holder = commonplayerspawninfo.dimensionType();
        LocalPlayer localplayer = this.minecraft.player;
        ResourceKey<Level> resourcekey1 = localplayer.level().dimension();
        boolean flag = resourcekey != resourcekey1;
        LevelLoadingScreen.Reason levelloadingscreen$reason = this.determineLevelLoadingReason(localplayer.isDeadOrDying(), resourcekey, resourcekey1);
        if (flag) {
            Map<MapId, MapItemSavedData> map = this.level.getAllMapData();
            boolean flag1 = commonplayerspawninfo.isDebug();
            boolean flag2 = commonplayerspawninfo.isFlat();
            int i = commonplayerspawninfo.seaLevel();
            ClientLevel.ClientLevelData clientlevel$clientleveldata = new ClientLevel.ClientLevelData(
                this.levelData.getDifficulty(), this.levelData.isHardcore(), flag2
            );
            this.levelData = clientlevel$clientleveldata;
            this.level = new ClientLevel(
                this,
                clientlevel$clientleveldata,
                resourcekey,
                holder,
                this.serverChunkRadius,
                this.serverSimulationDistance,
                this.minecraft.levelRenderer,
                flag1,
                commonplayerspawninfo.seed(),
                i
            );
            this.level.addMapData(map);
            this.minecraft.setLevel(this.level);
            this.debugSubscriber.dropLevel();
        }

        this.minecraft.setCameraEntity(null);
        if (localplayer.hasContainerOpen()) {
            localplayer.closeContainer();
        }

        LocalPlayer localplayer1;
        if (p_105066_.shouldKeep((byte)2)) {
            localplayer1 = this.minecraft
                .gameMode
                .createPlayer(this.level, localplayer.getStats(), localplayer.getRecipeBook(), localplayer.getLastSentInput(), localplayer.isSprinting());
        } else {
            localplayer1 = this.minecraft.gameMode.createPlayer(this.level, localplayer.getStats(), localplayer.getRecipeBook());
        }

        this.setClientLoaded(false);
        this.startWaitingForNewLevel(localplayer1, this.level, levelloadingscreen$reason, localplayer.isDeadOrDying() ? null : resourcekey, localplayer.isDeadOrDying() ? null : resourcekey1);
        localplayer1.setId(localplayer.getId());
        this.minecraft.player = localplayer1;
        if (flag) {
            this.minecraft.getMusicManager().stopPlaying();
        }

        this.minecraft.setCameraEntity(localplayer1);
        if (p_105066_.shouldKeep((byte)2)) {
            List<SynchedEntityData.DataValue<?>> list = localplayer.getEntityData().getNonDefaultValues();
            if (list != null) {
                localplayer1.getEntityData().assignValues(list);
            }

            localplayer1.setDeltaMovement(localplayer.getDeltaMovement());
            localplayer1.setYRot(localplayer.getYRot());
            localplayer1.setXRot(localplayer.getXRot());
        } else {
            localplayer1.resetPos();
            localplayer1.setYRot(-180.0F);
        }

        if (p_105066_.shouldKeep((byte)1)) {
            localplayer1.getAttributes().assignAllValues(localplayer.getAttributes());
        } else {
            localplayer1.getAttributes().assignBaseValues(localplayer.getAttributes());
        }

        net.neoforged.neoforge.client.ClientHooks.firePlayerRespawn(this.minecraft.gameMode, localplayer, localplayer1, localplayer1.connection.connection);
        this.level.addEntity(localplayer1);
        localplayer1.input = new KeyboardInput(this.minecraft.options);
        this.minecraft.gameMode.adjustPlayer(localplayer1);
        localplayer1.setReducedDebugInfo(localplayer.isReducedDebugInfo());
        localplayer1.setShowDeathScreen(localplayer.shouldShowDeathScreen());
        localplayer1.setLastDeathLocation(commonplayerspawninfo.lastDeathLocation());
        localplayer1.setPortalCooldown(commonplayerspawninfo.portalCooldown());
        localplayer1.portalEffectIntensity = localplayer.portalEffectIntensity;
        localplayer1.oPortalEffectIntensity = localplayer.oPortalEffectIntensity;
        if (this.minecraft.screen instanceof DeathScreen || this.minecraft.screen instanceof DeathScreen.TitleConfirmScreen) {
            this.minecraft.setScreen(null);
        }

        this.minecraft.gameMode.setLocalMode(commonplayerspawninfo.gameType(), commonplayerspawninfo.previousGameType());
    }

    private LevelLoadingScreen.Reason determineLevelLoadingReason(boolean p_341642_, ResourceKey<Level> p_341617_, ResourceKey<Level> p_341637_) {
        LevelLoadingScreen.Reason levelloadingscreen$reason = LevelLoadingScreen.Reason.OTHER;
        if (!p_341642_) {
            if (p_341617_ == Level.NETHER || p_341637_ == Level.NETHER) {
                levelloadingscreen$reason = LevelLoadingScreen.Reason.NETHER_PORTAL;
            } else if (p_341617_ == Level.END || p_341637_ == Level.END) {
                levelloadingscreen$reason = LevelLoadingScreen.Reason.END_PORTAL;
            }
        }

        return levelloadingscreen$reason;
    }

    @Override
    public void handleExplosion(ClientboundExplodePacket p_105012_) {
        PacketUtils.ensureRunningOnSameThread(p_105012_, this, this.minecraft.packetProcessor());
        Vec3 vec3 = p_105012_.center();
        this.minecraft
            .level
            .playLocalSound(
                vec3.x(),
                vec3.y(),
                vec3.z(),
                p_105012_.explosionSound().value(),
                SoundSource.BLOCKS,
                4.0F,
                (1.0F + (this.minecraft.level.random.nextFloat() - this.minecraft.level.random.nextFloat()) * 0.2F) * 0.7F,
                false
            );
        this.minecraft.level.addParticle(p_105012_.explosionParticle(), vec3.x(), vec3.y(), vec3.z(), 1.0, 0.0, 0.0);
        this.minecraft.level.trackExplosionEffects(vec3, p_105012_.radius(), p_105012_.blockCount(), p_105012_.blockParticles());
        p_105012_.playerKnockback().ifPresent(this.minecraft.player::addDeltaMovement);
    }

    @Override
    public void handleMountScreenOpen(ClientboundMountScreenOpenPacket p_470765_) {
        PacketUtils.ensureRunningOnSameThread(p_470765_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_470765_.getEntityId());
        LocalPlayer localplayer = this.minecraft.player;
        int i = p_470765_.getInventoryColumns();
        SimpleContainer simplecontainer = new SimpleContainer(AbstractMountInventoryMenu.getInventorySize(i));
        if (entity instanceof AbstractHorse abstracthorse) {
            HorseInventoryMenu horseinventorymenu = new HorseInventoryMenu(
                p_470765_.getContainerId(), localplayer.getInventory(), simplecontainer, abstracthorse, i
            );
            localplayer.containerMenu = horseinventorymenu;
            this.minecraft.setScreen(new HorseInventoryScreen(horseinventorymenu, localplayer.getInventory(), abstracthorse, i));
        } else if (entity instanceof AbstractNautilus abstractnautilus) {
            NautilusInventoryMenu nautilusinventorymenu = new NautilusInventoryMenu(
                p_470765_.getContainerId(), localplayer.getInventory(), simplecontainer, abstractnautilus, i
            );
            localplayer.containerMenu = nautilusinventorymenu;
            this.minecraft.setScreen(new NautilusInventoryScreen(nautilusinventorymenu, localplayer.getInventory(), abstractnautilus, i));
        }
    }

    @Override
    public void handleOpenScreen(ClientboundOpenScreenPacket p_105042_) {
        PacketUtils.ensureRunningOnSameThread(p_105042_, this, this.minecraft.packetProcessor());
        MenuScreens.create(p_105042_.getType(), this.minecraft, p_105042_.getContainerId(), p_105042_.getTitle());
    }

    @Override
    public void handleContainerSetSlot(ClientboundContainerSetSlotPacket p_105000_) {
        PacketUtils.ensureRunningOnSameThread(p_105000_, this, this.minecraft.packetProcessor());
        Player player = this.minecraft.player;
        ItemStack itemstack = p_105000_.getItem();
        int i = p_105000_.getSlot();
        this.minecraft.getTutorial().onGetItem(itemstack);
        boolean flag;
        if (this.minecraft.screen instanceof CreativeModeInventoryScreen creativemodeinventoryscreen) {
            flag = !creativemodeinventoryscreen.isInventoryOpen();
        } else {
            flag = false;
        }

        if (p_105000_.getContainerId() == 0) {
            if (InventoryMenu.isHotbarSlot(i) && !itemstack.isEmpty()) {
                ItemStack itemstack1 = player.inventoryMenu.getSlot(i).getItem();
                if (itemstack1.isEmpty() || itemstack1.getCount() < itemstack.getCount()) {
                    itemstack.setPopTime(5);
                }
            }

            player.inventoryMenu.setItem(i, p_105000_.getStateId(), itemstack);
        } else if (p_105000_.getContainerId() == player.containerMenu.containerId && (p_105000_.getContainerId() != 0 || !flag)) {
            player.containerMenu.setItem(i, p_105000_.getStateId(), itemstack);
        }

        if (this.minecraft.screen instanceof CreativeModeInventoryScreen) {
            player.inventoryMenu.setRemoteSlot(i, itemstack);
            player.inventoryMenu.broadcastChanges();
        }
    }

    @Override
    public void handleSetCursorItem(ClientboundSetCursorItemPacket p_364656_) {
        PacketUtils.ensureRunningOnSameThread(p_364656_, this, this.minecraft.packetProcessor());
        this.minecraft.getTutorial().onGetItem(p_364656_.contents());
        if (!(this.minecraft.screen instanceof CreativeModeInventoryScreen)) {
            this.minecraft.player.containerMenu.setCarried(p_364656_.contents());
        }
    }

    @Override
    public void handleSetPlayerInventory(ClientboundSetPlayerInventoryPacket p_364719_) {
        PacketUtils.ensureRunningOnSameThread(p_364719_, this, this.minecraft.packetProcessor());
        this.minecraft.getTutorial().onGetItem(p_364719_.contents());
        this.minecraft.player.getInventory().setItem(p_364719_.slot(), p_364719_.contents());
    }

    @Override
    public void handleContainerContent(ClientboundContainerSetContentPacket p_104996_) {
        PacketUtils.ensureRunningOnSameThread(p_104996_, this, this.minecraft.packetProcessor());
        Player player = this.minecraft.player;
        if (p_104996_.containerId() == 0) {
            player.inventoryMenu.initializeContents(p_104996_.stateId(), p_104996_.items(), p_104996_.carriedItem());
        } else if (p_104996_.containerId() == player.containerMenu.containerId) {
            player.containerMenu.initializeContents(p_104996_.stateId(), p_104996_.items(), p_104996_.carriedItem());
        }
    }

    @Override
    public void handleOpenSignEditor(ClientboundOpenSignEditorPacket p_105044_) {
        PacketUtils.ensureRunningOnSameThread(p_105044_, this, this.minecraft.packetProcessor());
        BlockPos blockpos = p_105044_.getPos();
        if (this.level.getBlockEntity(blockpos) instanceof SignBlockEntity signblockentity) {
            this.minecraft.player.openTextEdit(signblockentity, p_105044_.isFrontText());
        } else {
            LOGGER.warn("Ignoring openTextEdit on an invalid entity: {} at pos {}", this.level.getBlockEntity(blockpos), blockpos);
        }
    }

    @Override
    public void handleBlockEntityData(ClientboundBlockEntityDataPacket p_104976_) {
        PacketUtils.ensureRunningOnSameThread(p_104976_, this, this.minecraft.packetProcessor());
        BlockPos blockpos = p_104976_.getPos();
        this.minecraft.level.getBlockEntity(blockpos, p_104976_.getType()).ifPresent(p_421289_ -> {
            ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(p_421289_.problemPath(), LOGGER);

            try {
                p_421289_.onDataPacket(connection, TagValueInput.create(problemreporter$scopedcollector, this.registryAccess, p_104976_.getTag()));
            } catch (Throwable throwable1) {
                try {
                    problemreporter$scopedcollector.close();
                } catch (Throwable throwable) {
                    throwable1.addSuppressed(throwable);
                }

                throw throwable1;
            }

            problemreporter$scopedcollector.close();
            if (p_421289_ instanceof CommandBlockEntity && this.minecraft.screen instanceof CommandBlockEditScreen) {
                ((CommandBlockEditScreen)this.minecraft.screen).updateGui();
            }
        });
    }

    @Override
    public void handleContainerSetData(ClientboundContainerSetDataPacket p_104998_) {
        PacketUtils.ensureRunningOnSameThread(p_104998_, this, this.minecraft.packetProcessor());
        Player player = this.minecraft.player;
        if (player.containerMenu.containerId == p_104998_.getContainerId()) {
            player.containerMenu.setData(p_104998_.getId(), p_104998_.getValue());
        }
    }

    @Override
    public void handleSetEquipment(ClientboundSetEquipmentPacket p_105094_) {
        PacketUtils.ensureRunningOnSameThread(p_105094_, this, this.minecraft.packetProcessor());
        if (this.level.getEntity(p_105094_.getEntity()) instanceof LivingEntity livingentity) {
            p_105094_.getSlots().forEach(p_323056_ -> livingentity.setItemSlot(p_323056_.getFirst(), p_323056_.getSecond()));
        }
    }

    @Override
    public void handleContainerClose(ClientboundContainerClosePacket p_104994_) {
        PacketUtils.ensureRunningOnSameThread(p_104994_, this, this.minecraft.packetProcessor());
        this.minecraft.player.clientSideCloseContainer();
    }

    @Override
    public void handleBlockEvent(ClientboundBlockEventPacket p_104978_) {
        PacketUtils.ensureRunningOnSameThread(p_104978_, this, this.minecraft.packetProcessor());
        this.minecraft.level.blockEvent(p_104978_.getPos(), p_104978_.getBlock(), p_104978_.getB0(), p_104978_.getB1());
    }

    @Override
    public void handleBlockDestruction(ClientboundBlockDestructionPacket p_104974_) {
        PacketUtils.ensureRunningOnSameThread(p_104974_, this, this.minecraft.packetProcessor());
        this.minecraft.level.destroyBlockProgress(p_104974_.getId(), p_104974_.getPos(), p_104974_.getProgress());
    }

    @Override
    public void handleGameEvent(ClientboundGameEventPacket p_105016_) {
        PacketUtils.ensureRunningOnSameThread(p_105016_, this, this.minecraft.packetProcessor());
        Player player = this.minecraft.player;
        ClientboundGameEventPacket.Type clientboundgameeventpacket$type = p_105016_.getEvent();
        float f = p_105016_.getParam();
        int i = Mth.floor(f + 0.5F);
        if (clientboundgameeventpacket$type == ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE) {
            player.displayClientMessage(Component.translatable("block.minecraft.spawn.not_valid"), false);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.START_RAINING) {
            this.level.getLevelData().setRaining(true);
            this.level.setRainLevel(0.0F);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.STOP_RAINING) {
            this.level.getLevelData().setRaining(false);
            this.level.setRainLevel(1.0F);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.CHANGE_GAME_MODE) {
            this.minecraft.gameMode.setLocalMode(GameType.byId(i));
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.WIN_GAME) {
            this.minecraft.setScreen(new WinScreen(true, () -> {
                this.minecraft.player.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
                this.minecraft.setScreen(null);
            }));
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.DEMO_EVENT) {
            Options options = this.minecraft.options;
            Component component = null;
            if (f == 0.0F) {
                this.minecraft.setScreen(new DemoIntroScreen());
            } else if (f == 101.0F) {
                component = Component.translatable(
                    "demo.help.movement",
                    options.keyUp.getTranslatedKeyMessage(),
                    options.keyLeft.getTranslatedKeyMessage(),
                    options.keyDown.getTranslatedKeyMessage(),
                    options.keyRight.getTranslatedKeyMessage()
                );
            } else if (f == 102.0F) {
                component = Component.translatable("demo.help.jump", options.keyJump.getTranslatedKeyMessage());
            } else if (f == 103.0F) {
                component = Component.translatable("demo.help.inventory", options.keyInventory.getTranslatedKeyMessage());
            } else if (f == 104.0F) {
                component = Component.translatable("demo.day.6", options.keyScreenshot.getTranslatedKeyMessage());
            }

            if (component != null) {
                this.minecraft.gui.getChat().addMessage(component);
                this.minecraft.getNarrator().saySystemQueued(component);
            }
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.PLAY_ARROW_HIT_SOUND) {
            this.level.playSound(player, player.getX(), player.getEyeY(), player.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.18F, 0.45F);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE) {
            this.level.setRainLevel(f);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE) {
            this.level.setThunderLevel(f);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.PUFFER_FISH_STING) {
            this.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.PUFFER_FISH_STING, SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT) {
            this.level.addParticle(ParticleTypes.ELDER_GUARDIAN, player.getX(), player.getY(), player.getZ(), 0.0, 0.0, 0.0);
            if (i == 1) {
                this.level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0F, 1.0F);
            }
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.IMMEDIATE_RESPAWN) {
            this.minecraft.player.setShowDeathScreen(f == 0.0F);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.LIMITED_CRAFTING) {
            this.minecraft.player.setDoLimitedCrafting(f == 1.0F);
        } else if (clientboundgameeventpacket$type == ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START && this.levelLoadTracker != null) {
            this.levelLoadTracker.loadingPacketsReceived();
        }
    }

    /** @deprecated Neo: use {@link #startWaitingForNewLevel(LocalPlayer, ClientLevel, LevelLoadingScreen.Reason, ResourceKey, ResourceKey)} instead. */
    @Deprecated
    private void startWaitingForNewLevel(LocalPlayer p_304688_, ClientLevel p_304528_, LevelLoadingScreen.Reason p_433545_) {
        this.startWaitingForNewLevel(p_304688_, p_304528_, p_433545_, null, null);
    }

    private void startWaitingForNewLevel(LocalPlayer p_304688_, ClientLevel p_304528_, LevelLoadingScreen.Reason p_433545_, @Nullable ResourceKey<Level> toDimension, @Nullable ResourceKey<Level> fromDimension) {
        if (this.levelLoadTracker == null) {
            this.levelLoadTracker = new LevelLoadTracker();
        }

        this.levelLoadTracker.startClientLoad(p_304688_, p_304528_, this.minecraft.levelRenderer);
        if (this.minecraft.screen instanceof LevelLoadingScreen levelloadingscreen) {
            levelloadingscreen.update(this.levelLoadTracker, p_433545_);
        } else {
            this.minecraft.gui.getChat().preserveCurrentChatScreen();
            this.minecraft.setScreenAndShow(net.neoforged.neoforge.client.DimensionTransitionScreenManager.getScreen(toDimension, fromDimension).create(this.levelLoadTracker, p_433545_));
        }
    }

    @Override
    public void handleMapItemData(ClientboundMapItemDataPacket p_105032_) {
        PacketUtils.ensureRunningOnSameThread(p_105032_, this, this.minecraft.packetProcessor());
        MapId mapid = p_105032_.mapId();
        MapItemSavedData mapitemsaveddata = this.minecraft.level.getMapData(mapid);
        if (mapitemsaveddata == null) {
            mapitemsaveddata = MapItemSavedData.createForClient(p_105032_.scale(), p_105032_.locked(), this.minecraft.level.dimension());
            this.minecraft.level.overrideMapData(mapid, mapitemsaveddata);
        }

        p_105032_.applyToMap(mapitemsaveddata);
        this.minecraft.getMapTextureManager().update(mapid, mapitemsaveddata);
    }

    @Override
    public void handleLevelEvent(ClientboundLevelEventPacket p_105024_) {
        PacketUtils.ensureRunningOnSameThread(p_105024_, this, this.minecraft.packetProcessor());
        if (p_105024_.isGlobalEvent()) {
            this.minecraft.level.globalLevelEvent(p_105024_.getType(), p_105024_.getPos(), p_105024_.getData());
        } else {
            this.minecraft.level.levelEvent(p_105024_.getType(), p_105024_.getPos(), p_105024_.getData());
        }
    }

    @Override
    public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket p_105126_) {
        PacketUtils.ensureRunningOnSameThread(p_105126_, this, this.minecraft.packetProcessor());
        this.advancements.update(p_105126_);
    }

    @Override
    public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket p_105072_) {
        PacketUtils.ensureRunningOnSameThread(p_105072_, this, this.minecraft.packetProcessor());
        Identifier identifier = p_105072_.getTab();
        if (identifier == null) {
            this.advancements.setSelectedTab(null, false);
        } else {
            AdvancementHolder advancementholder = this.advancements.get(identifier);
            this.advancements.setSelectedTab(advancementholder, false);
        }
    }

    @Override
    public void handleCommands(ClientboundCommandsPacket p_104990_) {
        PacketUtils.ensureRunningOnSameThread(p_104990_, this, this.minecraft.packetProcessor());
        var context = CommandBuildContext.simple(this.registryAccess, this.enabledFeatures);
        this.commands = new CommandDispatcher<>(p_104990_.getRoot(context, COMMAND_NODE_BUILDER));
        this.commands = net.neoforged.neoforge.client.ClientCommandHandler.mergeServerCommands(this.commands, context);
    }

    @Override
    public void handleStopSoundEvent(ClientboundStopSoundPacket p_105116_) {
        PacketUtils.ensureRunningOnSameThread(p_105116_, this, this.minecraft.packetProcessor());
        this.minecraft.getSoundManager().stop(p_105116_.getName(), p_105116_.getSource());
    }

    @Override
    public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket p_104988_) {
        PacketUtils.ensureRunningOnSameThread(p_104988_, this, this.minecraft.packetProcessor());
        this.suggestionsProvider.completeCustomSuggestions(p_104988_.id(), p_104988_.toSuggestions());
    }

    @Override
    public void handleUpdateRecipes(ClientboundUpdateRecipesPacket p_105132_) {
        PacketUtils.ensureRunningOnSameThread(p_105132_, this, this.minecraft.packetProcessor());
        this.recipes = new ClientRecipeContainer(p_105132_.itemSets(), p_105132_.stonecutterRecipes());

        net.neoforged.neoforge.client.ClientHooks.handleUpdateRecipes(this, v -> this.fuelValues = v);
    }

    @Override
    public void handleLookAt(ClientboundPlayerLookAtPacket p_105054_) {
        PacketUtils.ensureRunningOnSameThread(p_105054_, this, this.minecraft.packetProcessor());
        Vec3 vec3 = p_105054_.getPosition(this.level);
        if (vec3 != null) {
            this.minecraft.player.lookAt(p_105054_.getFromAnchor(), vec3);
        }
    }

    @Override
    public void handleTagQueryPacket(ClientboundTagQueryPacket p_105120_) {
        PacketUtils.ensureRunningOnSameThread(p_105120_, this, this.minecraft.packetProcessor());
        if (!this.debugQueryHandler.handleResponse(p_105120_.getTransactionId(), p_105120_.getTag())) {
            LOGGER.debug("Got unhandled response to tag query {}", p_105120_.getTransactionId());
        }
    }

    @Override
    public void handleAwardStats(ClientboundAwardStatsPacket p_104970_) {
        PacketUtils.ensureRunningOnSameThread(p_104970_, this, this.minecraft.packetProcessor());

        for (Entry<Stat<?>> entry : p_104970_.stats().object2IntEntrySet()) {
            Stat<?> stat = entry.getKey();
            int i = entry.getIntValue();
            this.minecraft.player.getStats().setValue(this.minecraft.player, stat, i);
        }

        if (this.minecraft.screen instanceof StatsScreen statsscreen) {
            statsscreen.onStatsUpdated();
        }
    }

    @Override
    public void handleRecipeBookAdd(ClientboundRecipeBookAddPacket p_379950_) {
        PacketUtils.ensureRunningOnSameThread(p_379950_, this, this.minecraft.packetProcessor());
        ClientRecipeBook clientrecipebook = this.minecraft.player.getRecipeBook();
        if (p_379950_.replace()) {
            clientrecipebook.clear();
        }

        for (ClientboundRecipeBookAddPacket.Entry clientboundrecipebookaddpacket$entry : p_379950_.entries()) {
            clientrecipebook.add(clientboundrecipebookaddpacket$entry.contents());
            if (clientboundrecipebookaddpacket$entry.highlight()) {
                clientrecipebook.addHighlight(clientboundrecipebookaddpacket$entry.contents().id());
            }

            if (clientboundrecipebookaddpacket$entry.notification()) {
                RecipeToast.addOrUpdate(this.minecraft.getToastManager(), clientboundrecipebookaddpacket$entry.contents().display());
            }
        }

        this.refreshRecipeBook(clientrecipebook);
    }

    @Override
    public void handleRecipeBookRemove(ClientboundRecipeBookRemovePacket p_379580_) {
        PacketUtils.ensureRunningOnSameThread(p_379580_, this, this.minecraft.packetProcessor());
        ClientRecipeBook clientrecipebook = this.minecraft.player.getRecipeBook();

        for (RecipeDisplayId recipedisplayid : p_379580_.recipes()) {
            clientrecipebook.remove(recipedisplayid);
        }

        this.refreshRecipeBook(clientrecipebook);
    }

    @Override
    public void handleRecipeBookSettings(ClientboundRecipeBookSettingsPacket p_380266_) {
        PacketUtils.ensureRunningOnSameThread(p_380266_, this, this.minecraft.packetProcessor());
        ClientRecipeBook clientrecipebook = this.minecraft.player.getRecipeBook();
        clientrecipebook.setBookSettings(p_380266_.bookSettings());
        this.refreshRecipeBook(clientrecipebook);
    }

    private void refreshRecipeBook(ClientRecipeBook p_380264_) {
        p_380264_.rebuildCollections();
        this.searchTrees.updateRecipes(p_380264_, this.level);
        if (this.minecraft.screen instanceof RecipeUpdateListener recipeupdatelistener) {
            recipeupdatelistener.recipesUpdated();
        }
    }

    @Override
    public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket p_105130_) {
        PacketUtils.ensureRunningOnSameThread(p_105130_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_105130_.getEntityId());
        if (entity instanceof LivingEntity) {
            Holder<MobEffect> holder = p_105130_.getEffect();
            MobEffectInstance mobeffectinstance = new MobEffectInstance(
                holder,
                p_105130_.getEffectDurationTicks(),
                p_105130_.getEffectAmplifier(),
                p_105130_.isEffectAmbient(),
                p_105130_.isEffectVisible(),
                p_105130_.effectShowsIcon(),
                null
            );
            if (!p_105130_.shouldBlend()) {
                mobeffectinstance.skipBlending();
            }

            ((LivingEntity)entity).forceAddEffect(mobeffectinstance, null);
        }
    }

    private <T> Registry.PendingTags<T> updateTags(ResourceKey<? extends Registry<? extends T>> p_363258_, TagNetworkSerialization.NetworkPayload p_361357_) {
        Registry<T> registry = this.registryAccess.lookupOrThrow(p_363258_);
        return registry.prepareTagReload(p_361357_.resolve(registry));
    }

    @Override
    public void handleUpdateTags(ClientboundUpdateTagsPacket p_294888_) {
        PacketUtils.ensureRunningOnSameThread(p_294888_, this, this.minecraft.packetProcessor());
        List<Registry.PendingTags<?>> list = new ArrayList<>(p_294888_.getTags().size());
        boolean flag = this.connection.isMemoryConnection();
        p_294888_.getTags().forEach((p_359138_, p_359139_) -> {
            if (!flag || RegistrySynchronization.isNetworkable((ResourceKey<? extends Registry<?>>)p_359138_)) {
                list.add(this.updateTags((ResourceKey<? extends Registry<?>>)p_359138_, p_359139_));
            }
        });
        list.forEach(Registry.PendingTags::apply);
        this.fuelValues = FuelValues.vanillaBurnTimes(this.registryAccess, this.enabledFeatures);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.TagsUpdatedEvent(this.registryAccess, true, flag));
        CreativeModeTabs.allTabs().stream().filter(net.minecraft.world.item.CreativeModeTab::hasSearchBar).forEach(tab -> {
            List<ItemStack> stacks = List.copyOf(tab.getDisplayItems());
            this.searchTrees.updateCreativeTags(stacks, net.neoforged.neoforge.client.CreativeModeTabSearchRegistry.getTagSearchKey(tab));
        });
    }

    @Override
    public void handlePlayerCombatEnd(ClientboundPlayerCombatEndPacket p_171771_) {
    }

    @Override
    public void handlePlayerCombatEnter(ClientboundPlayerCombatEnterPacket p_171773_) {
    }

    @Override
    public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket p_171775_) {
        PacketUtils.ensureRunningOnSameThread(p_171775_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_171775_.playerId());
        if (entity == this.minecraft.player) {
            if (this.minecraft.player.shouldShowDeathScreen()) {
                this.minecraft.setScreen(new DeathScreen(p_171775_.message(), this.level.getLevelData().isHardcore(), this.minecraft.player));
            } else {
                this.minecraft.player.respawn();
            }
        }
    }

    @Override
    public void handleChangeDifficulty(ClientboundChangeDifficultyPacket p_104984_) {
        PacketUtils.ensureRunningOnSameThread(p_104984_, this, this.minecraft.packetProcessor());
        this.levelData.setDifficulty(p_104984_.difficulty());
        this.levelData.setDifficultyLocked(p_104984_.locked());
    }

    @Override
    public void handleSetCamera(ClientboundSetCameraPacket p_105076_) {
        PacketUtils.ensureRunningOnSameThread(p_105076_, this, this.minecraft.packetProcessor());
        Entity entity = p_105076_.getEntity(this.level);
        if (entity != null) {
            this.minecraft.setCameraEntity(entity);
        }
    }

    @Override
    public void handleInitializeBorder(ClientboundInitializeBorderPacket p_171767_) {
        PacketUtils.ensureRunningOnSameThread(p_171767_, this, this.minecraft.packetProcessor());
        WorldBorder worldborder = this.level.getWorldBorder();
        worldborder.setCenter(p_171767_.getNewCenterX(), p_171767_.getNewCenterZ());
        long i = p_171767_.getLerpTime();
        if (i > 0L) {
            worldborder.lerpSizeBetween(p_171767_.getOldSize(), p_171767_.getNewSize(), i, this.level.getGameTime());
        } else {
            worldborder.setSize(p_171767_.getNewSize());
        }

        worldborder.setAbsoluteMaxSize(p_171767_.getNewAbsoluteMaxSize());
        worldborder.setWarningBlocks(p_171767_.getWarningBlocks());
        worldborder.setWarningTime(p_171767_.getWarningTime());
    }

    @Override
    public void handleSetBorderCenter(ClientboundSetBorderCenterPacket p_171781_) {
        PacketUtils.ensureRunningOnSameThread(p_171781_, this, this.minecraft.packetProcessor());
        this.level.getWorldBorder().setCenter(p_171781_.getNewCenterX(), p_171781_.getNewCenterZ());
    }

    @Override
    public void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket p_171783_) {
        PacketUtils.ensureRunningOnSameThread(p_171783_, this, this.minecraft.packetProcessor());
        this.level.getWorldBorder().lerpSizeBetween(p_171783_.getOldSize(), p_171783_.getNewSize(), p_171783_.getLerpTime(), this.level.getGameTime());
    }

    @Override
    public void handleSetBorderSize(ClientboundSetBorderSizePacket p_171785_) {
        PacketUtils.ensureRunningOnSameThread(p_171785_, this, this.minecraft.packetProcessor());
        this.level.getWorldBorder().setSize(p_171785_.getSize());
    }

    @Override
    public void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket p_171789_) {
        PacketUtils.ensureRunningOnSameThread(p_171789_, this, this.minecraft.packetProcessor());
        this.level.getWorldBorder().setWarningBlocks(p_171789_.getWarningBlocks());
    }

    @Override
    public void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket p_171787_) {
        PacketUtils.ensureRunningOnSameThread(p_171787_, this, this.minecraft.packetProcessor());
        this.level.getWorldBorder().setWarningTime(p_171787_.getWarningDelay());
    }

    @Override
    public void handleTitlesClear(ClientboundClearTitlesPacket p_171765_) {
        PacketUtils.ensureRunningOnSameThread(p_171765_, this, this.minecraft.packetProcessor());
        this.minecraft.gui.clearTitles();
        if (p_171765_.shouldResetTimes()) {
            this.minecraft.gui.resetTitleTimes();
        }
    }

    @Override
    public void handleServerData(ClientboundServerDataPacket p_233704_) {
        PacketUtils.ensureRunningOnSameThread(p_233704_, this, this.minecraft.packetProcessor());
        if (this.serverData != null) {
            this.serverData.motd = p_233704_.motd();
            p_233704_.iconBytes().map(ServerData::validateIcon).ifPresent(this.serverData::setIconBytes);
            ServerList.saveSingleServer(this.serverData);
        }
    }

    @Override
    public void handleCustomChatCompletions(ClientboundCustomChatCompletionsPacket p_240832_) {
        PacketUtils.ensureRunningOnSameThread(p_240832_, this, this.minecraft.packetProcessor());
        this.suggestionsProvider.modifyCustomCompletions(p_240832_.action(), p_240832_.entries());
    }

    @Override
    public void setActionBarText(ClientboundSetActionBarTextPacket p_171779_) {
        PacketUtils.ensureRunningOnSameThread(p_171779_, this, this.minecraft.packetProcessor());
        this.minecraft.gui.setOverlayMessage(p_171779_.text(), false);
    }

    @Override
    public void setTitleText(ClientboundSetTitleTextPacket p_171793_) {
        PacketUtils.ensureRunningOnSameThread(p_171793_, this, this.minecraft.packetProcessor());
        this.minecraft.gui.setTitle(p_171793_.text());
    }

    @Override
    public void setSubtitleText(ClientboundSetSubtitleTextPacket p_171791_) {
        PacketUtils.ensureRunningOnSameThread(p_171791_, this, this.minecraft.packetProcessor());
        this.minecraft.gui.setSubtitle(p_171791_.text());
    }

    @Override
    public void setTitlesAnimation(ClientboundSetTitlesAnimationPacket p_171795_) {
        PacketUtils.ensureRunningOnSameThread(p_171795_, this, this.minecraft.packetProcessor());
        this.minecraft.gui.setTimes(p_171795_.getFadeIn(), p_171795_.getStay(), p_171795_.getFadeOut());
    }

    @Override
    public void handleTabListCustomisation(ClientboundTabListPacket p_105118_) {
        PacketUtils.ensureRunningOnSameThread(p_105118_, this, this.minecraft.packetProcessor());
        this.minecraft.gui.getTabList().setHeader(p_105118_.header().getString().isEmpty() ? null : p_105118_.header());
        this.minecraft.gui.getTabList().setFooter(p_105118_.footer().getString().isEmpty() ? null : p_105118_.footer());
    }

    @Override
    public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket p_105062_) {
        PacketUtils.ensureRunningOnSameThread(p_105062_, this, this.minecraft.packetProcessor());
        if (p_105062_.getEntity(this.level) instanceof LivingEntity livingentity) {
            livingentity.removeEffectNoUpdate(p_105062_.effect());
        }
    }

    @Override
    public void handlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket p_248731_) {
        PacketUtils.ensureRunningOnSameThread(p_248731_, this, this.minecraft.packetProcessor());

        for (UUID uuid : p_248731_.profileIds()) {
            this.minecraft.getPlayerSocialManager().removePlayer(uuid);
            PlayerInfo playerinfo = this.playerInfoMap.remove(uuid);
            if (playerinfo != null) {
                this.listedPlayers.remove(playerinfo);
            }
        }
    }

    @Override
    public void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket p_250115_) {
        PacketUtils.ensureRunningOnSameThread(p_250115_, this, this.minecraft.packetProcessor());

        for (ClientboundPlayerInfoUpdatePacket.Entry clientboundplayerinfoupdatepacket$entry : p_250115_.newEntries()) {
            PlayerInfo playerinfo = new PlayerInfo(Objects.requireNonNull(clientboundplayerinfoupdatepacket$entry.profile()), this.enforcesSecureChat());
            if (this.playerInfoMap.putIfAbsent(clientboundplayerinfoupdatepacket$entry.profileId(), playerinfo) == null) {
                this.minecraft.getPlayerSocialManager().addPlayer(playerinfo);
            }
        }

        for (ClientboundPlayerInfoUpdatePacket.Entry clientboundplayerinfoupdatepacket$entry1 : p_250115_.entries()) {
            PlayerInfo playerinfo1 = this.playerInfoMap.get(clientboundplayerinfoupdatepacket$entry1.profileId());
            if (playerinfo1 == null) {
                LOGGER.warn("Ignoring player info update for unknown player {} ({})", clientboundplayerinfoupdatepacket$entry1.profileId(), p_250115_.actions());
            } else {
                for (ClientboundPlayerInfoUpdatePacket.Action clientboundplayerinfoupdatepacket$action : p_250115_.actions()) {
                    this.applyPlayerInfoUpdate(clientboundplayerinfoupdatepacket$action, clientboundplayerinfoupdatepacket$entry1, playerinfo1);
                }
            }
        }
    }

    private void applyPlayerInfoUpdate(
        ClientboundPlayerInfoUpdatePacket.Action p_248954_, ClientboundPlayerInfoUpdatePacket.Entry p_251310_, PlayerInfo p_251146_
    ) {
        switch (p_248954_) {
            case INITIALIZE_CHAT:
                this.initializeChatSession(p_251310_, p_251146_);
                break;
            case UPDATE_GAME_MODE:
                if (p_251146_.getGameMode() != p_251310_.gameMode()
                    && this.minecraft.player != null
                    && this.minecraft.player.getUUID().equals(p_251310_.profileId())) {
                    this.minecraft.player.onGameModeChanged(p_251310_.gameMode());
                }

                p_251146_.setGameMode(p_251310_.gameMode());
                break;
            case UPDATE_LISTED:
                if (p_251310_.listed()) {
                    this.listedPlayers.add(p_251146_);
                } else {
                    this.listedPlayers.remove(p_251146_);
                }
                break;
            case UPDATE_LATENCY:
                p_251146_.setLatency(p_251310_.latency());
                break;
            case UPDATE_DISPLAY_NAME:
                p_251146_.setTabListDisplayName(p_251310_.displayName());
                break;
            case UPDATE_HAT:
                p_251146_.setShowHat(p_251310_.showHat());
                break;
            case UPDATE_LIST_ORDER:
                p_251146_.setTabListOrder(p_251310_.listOrder());
        }
    }

    private void initializeChatSession(ClientboundPlayerInfoUpdatePacket.Entry p_248806_, PlayerInfo p_251136_) {
        GameProfile gameprofile = p_251136_.getProfile();
        SignatureValidator signaturevalidator = this.minecraft.services().profileKeySignatureValidator();
        if (signaturevalidator == null) {
            LOGGER.warn("Ignoring chat session from {} due to missing Services public key", gameprofile.name());
            p_251136_.clearChatSession(this.enforcesSecureChat());
        } else {
            RemoteChatSession.Data remotechatsession$data = p_248806_.chatSession();
            if (remotechatsession$data != null) {
                try {
                    RemoteChatSession remotechatsession = remotechatsession$data.validate(gameprofile, signaturevalidator);
                    p_251136_.setChatSession(remotechatsession);
                } catch (ProfilePublicKey.ValidationException profilepublickey$validationexception) {
                    LOGGER.error("Failed to validate profile key for player: '{}'", gameprofile.name(), profilepublickey$validationexception);
                    p_251136_.clearChatSession(this.enforcesSecureChat());
                }
            } else {
                p_251136_.clearChatSession(this.enforcesSecureChat());
            }
        }
    }

    private boolean enforcesSecureChat() {
        return this.minecraft.services().canValidateProfileKeys() && this.serverEnforcesSecureChat;
    }

    @Override
    public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket p_105048_) {
        PacketUtils.ensureRunningOnSameThread(p_105048_, this, this.minecraft.packetProcessor());
        Player player = this.minecraft.player;
        player.getAbilities().flying = p_105048_.isFlying();
        player.getAbilities().instabuild = p_105048_.canInstabuild();
        player.getAbilities().invulnerable = p_105048_.isInvulnerable();
        player.getAbilities().mayfly = p_105048_.canFly();
        player.getAbilities().setFlyingSpeed(p_105048_.getFlyingSpeed());
        player.getAbilities().setWalkingSpeed(p_105048_.getWalkingSpeed());
    }

    @Override
    public void handleSoundEvent(ClientboundSoundPacket p_105114_) {
        PacketUtils.ensureRunningOnSameThread(p_105114_, this, this.minecraft.packetProcessor());
        this.minecraft
            .level
            .playSeededSound(
                this.minecraft.player,
                p_105114_.getX(),
                p_105114_.getY(),
                p_105114_.getZ(),
                p_105114_.getSound(),
                p_105114_.getSource(),
                p_105114_.getVolume(),
                p_105114_.getPitch(),
                p_105114_.getSeed()
            );
    }

    @Override
    public void handleSoundEntityEvent(ClientboundSoundEntityPacket p_105112_) {
        PacketUtils.ensureRunningOnSameThread(p_105112_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_105112_.getId());
        if (entity != null) {
            this.minecraft
                .level
                .playSeededSound(
                    this.minecraft.player,
                    entity,
                    p_105112_.getSound(),
                    p_105112_.getSource(),
                    p_105112_.getVolume(),
                    p_105112_.getPitch(),
                    p_105112_.getSeed()
                );
        }
    }

    @Override
    public void handleBossUpdate(ClientboundBossEventPacket p_104982_) {
        PacketUtils.ensureRunningOnSameThread(p_104982_, this, this.minecraft.packetProcessor());
        this.minecraft.gui.getBossOverlay().update(p_104982_);
    }

    @Override
    public void handleItemCooldown(ClientboundCooldownPacket p_105002_) {
        PacketUtils.ensureRunningOnSameThread(p_105002_, this, this.minecraft.packetProcessor());
        if (p_105002_.duration() == 0) {
            this.minecraft.player.getCooldowns().removeCooldown(p_105002_.cooldownGroup());
        } else {
            this.minecraft.player.getCooldowns().addCooldown(p_105002_.cooldownGroup(), p_105002_.duration());
        }
    }

    @Override
    public void handleMoveVehicle(ClientboundMoveVehiclePacket p_105038_) {
        PacketUtils.ensureRunningOnSameThread(p_105038_, this, this.minecraft.packetProcessor());
        Entity entity = this.minecraft.player.getRootVehicle();
        if (entity != this.minecraft.player && entity.isLocalInstanceAuthoritative()) {
            Vec3 vec3 = p_105038_.position();
            Vec3 vec31;
            if (entity.isInterpolating()) {
                vec31 = entity.getInterpolation().position();
            } else {
                vec31 = entity.position();
            }

            if (vec3.distanceTo(vec31) > 1.0E-5F) {
                if (entity.isInterpolating()) {
                    entity.getInterpolation().cancel();
                }

                entity.absSnapTo(vec3.x(), vec3.y(), vec3.z(), p_105038_.yRot(), p_105038_.xRot());
            }

            this.connection.send(ServerboundMoveVehiclePacket.fromEntity(entity));
        }
    }

    @Override
    public void handleOpenBook(ClientboundOpenBookPacket p_105040_) {
        PacketUtils.ensureRunningOnSameThread(p_105040_, this, this.minecraft.packetProcessor());
        ItemStack itemstack = this.minecraft.player.getItemInHand(p_105040_.getHand());
        BookViewScreen.BookAccess bookviewscreen$bookaccess = BookViewScreen.BookAccess.fromItem(itemstack);
        if (bookviewscreen$bookaccess != null) {
            this.minecraft.setScreen(new BookViewScreen(bookviewscreen$bookaccess));
        }
    }

    @Override
    public void handleCustomPayload(CustomPacketPayload p_295851_) {
        this.handleUnknownCustomPayload(p_295851_);
    }

    private void handleUnknownCustomPayload(CustomPacketPayload p_294389_) {
        LOGGER.warn("Unknown custom packet payload: {}", p_294389_.type().id());
    }

    @Override
    public void handleAddObjective(ClientboundSetObjectivePacket p_105100_) {
        PacketUtils.ensureRunningOnSameThread(p_105100_, this, this.minecraft.packetProcessor());
        String s = p_105100_.getObjectiveName();
        if (p_105100_.getMethod() == 0) {
            this.scoreboard
                .addObjective(
                    s, ObjectiveCriteria.DUMMY, p_105100_.getDisplayName(), p_105100_.getRenderType(), false, p_105100_.getNumberFormat().orElse(null)
                );
        } else {
            Objective objective = this.scoreboard.getObjective(s);
            if (objective != null) {
                if (p_105100_.getMethod() == 1) {
                    this.scoreboard.removeObjective(objective);
                } else if (p_105100_.getMethod() == 2) {
                    objective.setRenderType(p_105100_.getRenderType());
                    objective.setDisplayName(p_105100_.getDisplayName());
                    objective.setNumberFormat(p_105100_.getNumberFormat().orElse(null));
                }
            }
        }
    }

    @Override
    public void handleSetScore(ClientboundSetScorePacket p_105106_) {
        PacketUtils.ensureRunningOnSameThread(p_105106_, this, this.minecraft.packetProcessor());
        String s = p_105106_.objectiveName();
        ScoreHolder scoreholder = ScoreHolder.forNameOnly(p_105106_.owner());
        Objective objective = this.scoreboard.getObjective(s);
        if (objective != null) {
            ScoreAccess scoreaccess = this.scoreboard.getOrCreatePlayerScore(scoreholder, objective, true);
            scoreaccess.set(p_105106_.score());
            scoreaccess.display(p_105106_.display().orElse(null));
            scoreaccess.numberFormatOverride(p_105106_.numberFormat().orElse(null));
        } else {
            LOGGER.warn("Received packet for unknown scoreboard objective: {}", s);
        }
    }

    @Override
    public void handleResetScore(ClientboundResetScorePacket p_313768_) {
        PacketUtils.ensureRunningOnSameThread(p_313768_, this, this.minecraft.packetProcessor());
        String s = p_313768_.objectiveName();
        ScoreHolder scoreholder = ScoreHolder.forNameOnly(p_313768_.owner());
        if (s == null) {
            this.scoreboard.resetAllPlayerScores(scoreholder);
        } else {
            Objective objective = this.scoreboard.getObjective(s);
            if (objective != null) {
                this.scoreboard.resetSinglePlayerScore(scoreholder, objective);
            } else {
                LOGGER.warn("Received packet for unknown scoreboard objective: {}", s);
            }
        }
    }

    @Override
    public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket p_105086_) {
        PacketUtils.ensureRunningOnSameThread(p_105086_, this, this.minecraft.packetProcessor());
        String s = p_105086_.getObjectiveName();
        Objective objective = s == null ? null : this.scoreboard.getObjective(s);
        this.scoreboard.setDisplayObjective(p_105086_.getSlot(), objective);
    }

    @Override
    public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket p_105104_) {
        PacketUtils.ensureRunningOnSameThread(p_105104_, this, this.minecraft.packetProcessor());
        ClientboundSetPlayerTeamPacket.Action clientboundsetplayerteampacket$action = p_105104_.getTeamAction();
        PlayerTeam playerteam;
        if (clientboundsetplayerteampacket$action == ClientboundSetPlayerTeamPacket.Action.ADD) {
            playerteam = this.scoreboard.addPlayerTeam(p_105104_.getName());
        } else {
            playerteam = this.scoreboard.getPlayerTeam(p_105104_.getName());
            if (playerteam == null) {
                LOGGER.warn(
                    "Received packet for unknown team {}: team action: {}, player action: {}",
                    p_105104_.getName(),
                    p_105104_.getTeamAction(),
                    p_105104_.getPlayerAction()
                );
                return;
            }
        }

        Optional<ClientboundSetPlayerTeamPacket.Parameters> optional = p_105104_.getParameters();
        optional.ifPresent(p_400869_ -> {
            playerteam.setDisplayName(p_400869_.getDisplayName());
            playerteam.setColor(p_400869_.getColor());
            playerteam.unpackOptions(p_400869_.getOptions());
            playerteam.setNameTagVisibility(p_400869_.getNametagVisibility());
            playerteam.setCollisionRule(p_400869_.getCollisionRule());
            playerteam.setPlayerPrefix(p_400869_.getPlayerPrefix());
            playerteam.setPlayerSuffix(p_400869_.getPlayerSuffix());
        });
        ClientboundSetPlayerTeamPacket.Action clientboundsetplayerteampacket$action1 = p_105104_.getPlayerAction();
        if (clientboundsetplayerteampacket$action1 == ClientboundSetPlayerTeamPacket.Action.ADD) {
            for (String s : p_105104_.getPlayers()) {
                this.scoreboard.addPlayerToTeam(s, playerteam);
            }
        } else if (clientboundsetplayerteampacket$action1 == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
            for (String s1 : p_105104_.getPlayers()) {
                this.scoreboard.removePlayerFromTeam(s1, playerteam);
            }
        }

        if (clientboundsetplayerteampacket$action == ClientboundSetPlayerTeamPacket.Action.REMOVE) {
            this.scoreboard.removePlayerTeam(playerteam);
        }
    }

    @Override
    public void handleParticleEvent(ClientboundLevelParticlesPacket p_105026_) {
        PacketUtils.ensureRunningOnSameThread(p_105026_, this, this.minecraft.packetProcessor());
        if (p_105026_.getCount() == 0) {
            double d0 = p_105026_.getMaxSpeed() * p_105026_.getXDist();
            double d2 = p_105026_.getMaxSpeed() * p_105026_.getYDist();
            double d4 = p_105026_.getMaxSpeed() * p_105026_.getZDist();

            try {
                this.level
                    .addParticle(
                        p_105026_.getParticle(),
                        p_105026_.isOverrideLimiter(),
                        p_105026_.alwaysShow(),
                        p_105026_.getX(),
                        p_105026_.getY(),
                        p_105026_.getZ(),
                        d0,
                        d2,
                        d4
                    );
            } catch (Throwable throwable1) {
                LOGGER.warn("Could not spawn particle effect {}", p_105026_.getParticle());
            }
        } else {
            for (int i = 0; i < p_105026_.getCount(); i++) {
                double d1 = this.random.nextGaussian() * p_105026_.getXDist();
                double d3 = this.random.nextGaussian() * p_105026_.getYDist();
                double d5 = this.random.nextGaussian() * p_105026_.getZDist();
                double d6 = this.random.nextGaussian() * p_105026_.getMaxSpeed();
                double d7 = this.random.nextGaussian() * p_105026_.getMaxSpeed();
                double d8 = this.random.nextGaussian() * p_105026_.getMaxSpeed();

                try {
                    this.level
                        .addParticle(
                            p_105026_.getParticle(),
                            p_105026_.isOverrideLimiter(),
                            p_105026_.alwaysShow(),
                            p_105026_.getX() + d1,
                            p_105026_.getY() + d3,
                            p_105026_.getZ() + d5,
                            d6,
                            d7,
                            d8
                        );
                } catch (Throwable throwable) {
                    LOGGER.warn("Could not spawn particle effect {}", p_105026_.getParticle());
                    return;
                }
            }
        }
    }

    @Override
    public void handleUpdateAttributes(ClientboundUpdateAttributesPacket p_105128_) {
        PacketUtils.ensureRunningOnSameThread(p_105128_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_105128_.getEntityId());
        if (entity != null) {
            if (!(entity instanceof LivingEntity)) {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
            } else {
                AttributeMap attributemap = ((LivingEntity)entity).getAttributes();

                for (ClientboundUpdateAttributesPacket.AttributeSnapshot clientboundupdateattributespacket$attributesnapshot : p_105128_.getValues()) {
                    AttributeInstance attributeinstance = attributemap.getInstance(clientboundupdateattributespacket$attributesnapshot.attribute());
                    if (attributeinstance == null) {
                        LOGGER.warn(
                            "Entity {} does not have attribute {}", entity, clientboundupdateattributespacket$attributesnapshot.attribute().getRegisteredName()
                        );
                    } else {
                        attributeinstance.setBaseValue(clientboundupdateattributespacket$attributesnapshot.base());
                        attributeinstance.removeModifiers();

                        for (AttributeModifier attributemodifier : clientboundupdateattributespacket$attributesnapshot.modifiers()) {
                            attributeinstance.addTransientModifier(attributemodifier);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket p_105046_) {
        PacketUtils.ensureRunningOnSameThread(p_105046_, this, this.minecraft.packetProcessor());
        AbstractContainerMenu abstractcontainermenu = this.minecraft.player.containerMenu;
        if (abstractcontainermenu.containerId == p_105046_.containerId()) {
            if (this.minecraft.screen instanceof RecipeUpdateListener recipeupdatelistener) {
                recipeupdatelistener.fillGhostRecipe(p_105046_.recipeDisplay());
            }
        }
    }

    @Override
    public void handleLightUpdatePacket(ClientboundLightUpdatePacket p_194243_) {
        PacketUtils.ensureRunningOnSameThread(p_194243_, this, this.minecraft.packetProcessor());
        int i = p_194243_.getX();
        int j = p_194243_.getZ();
        ClientboundLightUpdatePacketData clientboundlightupdatepacketdata = p_194243_.getLightData();
        this.level.queueLightUpdate(() -> this.applyLightData(i, j, clientboundlightupdatepacketdata, true));
    }

    private void applyLightData(int p_194249_, int p_194250_, ClientboundLightUpdatePacketData p_194251_, boolean p_363269_) {
        LevelLightEngine levellightengine = this.level.getChunkSource().getLightEngine();
        BitSet bitset = p_194251_.getSkyYMask();
        BitSet bitset1 = p_194251_.getEmptySkyYMask();
        Iterator<byte[]> iterator = p_194251_.getSkyUpdates().iterator();
        this.readSectionList(p_194249_, p_194250_, levellightengine, LightLayer.SKY, bitset, bitset1, iterator, p_363269_);
        BitSet bitset2 = p_194251_.getBlockYMask();
        BitSet bitset3 = p_194251_.getEmptyBlockYMask();
        Iterator<byte[]> iterator1 = p_194251_.getBlockUpdates().iterator();
        this.readSectionList(p_194249_, p_194250_, levellightengine, LightLayer.BLOCK, bitset2, bitset3, iterator1, p_363269_);
        levellightengine.setLightEnabled(new ChunkPos(p_194249_, p_194250_), true);
    }

    @Override
    public void handleMerchantOffers(ClientboundMerchantOffersPacket p_105034_) {
        PacketUtils.ensureRunningOnSameThread(p_105034_, this, this.minecraft.packetProcessor());
        AbstractContainerMenu abstractcontainermenu = this.minecraft.player.containerMenu;
        if (p_105034_.getContainerId() == abstractcontainermenu.containerId && abstractcontainermenu instanceof MerchantMenu merchantmenu) {
            merchantmenu.setOffers(p_105034_.getOffers());
            merchantmenu.setXp(p_105034_.getVillagerXp());
            merchantmenu.setMerchantLevel(p_105034_.getVillagerLevel());
            merchantmenu.setShowProgressBar(p_105034_.showProgress());
            merchantmenu.setCanRestock(p_105034_.canRestock());
        }
    }

    @Override
    public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket p_105082_) {
        PacketUtils.ensureRunningOnSameThread(p_105082_, this, this.minecraft.packetProcessor());
        this.serverChunkRadius = p_105082_.getRadius();
        this.minecraft.options.setServerRenderDistance(this.serverChunkRadius);
        this.level.getChunkSource().updateViewRadius(p_105082_.getRadius());
    }

    @Override
    public void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket p_194245_) {
        PacketUtils.ensureRunningOnSameThread(p_194245_, this, this.minecraft.packetProcessor());
        this.serverSimulationDistance = p_194245_.simulationDistance();
        this.level.setServerSimulationDistance(this.serverSimulationDistance);
    }

    @Override
    public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket p_105080_) {
        PacketUtils.ensureRunningOnSameThread(p_105080_, this, this.minecraft.packetProcessor());
        this.level.getChunkSource().updateViewCenter(p_105080_.getX(), p_105080_.getZ());
    }

    @Override
    public void handleBlockChangedAck(ClientboundBlockChangedAckPacket p_233698_) {
        PacketUtils.ensureRunningOnSameThread(p_233698_, this, this.minecraft.packetProcessor());
        this.level.handleBlockChangedAck(p_233698_.sequence());
    }

    @Override
    public void handleBundlePacket(ClientboundBundlePacket p_265195_) {
        PacketUtils.ensureRunningOnSameThread(p_265195_, this, this.minecraft.packetProcessor());

        for (Packet<? super ClientGamePacketListener> packet : p_265195_.subPackets()) {
            packet.handle(this);
        }
    }

    @Override
    public void handleProjectilePowerPacket(ClientboundProjectilePowerPacket p_339600_) {
        PacketUtils.ensureRunningOnSameThread(p_339600_, this, this.minecraft.packetProcessor());
        if (this.level.getEntity(p_339600_.getId()) instanceof AbstractHurtingProjectile abstracthurtingprojectile) {
            abstracthurtingprojectile.accelerationPower = p_339600_.getAccelerationPower();
        }
    }

    @Override
    public void handleChunkBatchStart(ClientboundChunkBatchStartPacket p_295704_) {
        this.chunkBatchSizeCalculator.onBatchStart();
    }

    @Override
    public void handleChunkBatchFinished(ClientboundChunkBatchFinishedPacket p_295731_) {
        this.chunkBatchSizeCalculator.onBatchFinished(p_295731_.batchSize());
        this.send(new ServerboundChunkBatchReceivedPacket(this.chunkBatchSizeCalculator.getDesiredChunksPerTick()));
    }

    @Override
    public void handleDebugSample(ClientboundDebugSamplePacket p_324125_) {
        this.minecraft.getDebugOverlay().logRemoteSample(p_324125_.sample(), p_324125_.debugSampleType());
    }

    @Override
    public void handlePongResponse(ClientboundPongResponsePacket p_320651_) {
        this.pingDebugMonitor.onPongReceived(p_320651_);
    }

    @Override
    public void handleTestInstanceBlockStatus(ClientboundTestInstanceBlockStatus p_397430_) {
        PacketUtils.ensureRunningOnSameThread(p_397430_, this, this.minecraft.packetProcessor());
        if (this.minecraft.screen instanceof TestInstanceBlockEditScreen testinstanceblockeditscreen) {
            testinstanceblockeditscreen.setStatus(p_397430_.status(), p_397430_.size());
        }
    }

    @Override
    public void handleWaypoint(ClientboundTrackedWaypointPacket p_416702_) {
        PacketUtils.ensureRunningOnSameThread(p_416702_, this, this.minecraft.packetProcessor());
        p_416702_.apply(this.waypointManager);
    }

    @Override
    public void handleDebugChunkValue(ClientboundDebugChunkValuePacket p_449745_) {
        PacketUtils.ensureRunningOnSameThread(p_449745_, this, this.minecraft.packetProcessor());
        this.debugSubscriber.updateChunk(this.level.getGameTime(), p_449745_.chunkPos(), p_449745_.update());
    }

    @Override
    public void handleDebugBlockValue(ClientboundDebugBlockValuePacket p_449709_) {
        PacketUtils.ensureRunningOnSameThread(p_449709_, this, this.minecraft.packetProcessor());
        this.debugSubscriber.updateBlock(this.level.getGameTime(), p_449709_.blockPos(), p_449709_.update());
    }

    @Override
    public void handleDebugEntityValue(ClientboundDebugEntityValuePacket p_449627_) {
        PacketUtils.ensureRunningOnSameThread(p_449627_, this, this.minecraft.packetProcessor());
        Entity entity = this.level.getEntity(p_449627_.entityId());
        if (entity != null) {
            this.debugSubscriber.updateEntity(this.level.getGameTime(), entity, p_449627_.update());
        }
    }

    @Override
    public void handleDebugEvent(ClientboundDebugEventPacket p_449397_) {
        PacketUtils.ensureRunningOnSameThread(p_449397_, this, this.minecraft.packetProcessor());
        this.debugSubscriber.pushEvent(this.level.getGameTime(), p_449397_.event());
    }

    @Override
    public void handleGameTestHighlightPos(ClientboundGameTestHighlightPosPacket p_449067_) {
        PacketUtils.ensureRunningOnSameThread(p_449067_, this, this.minecraft.packetProcessor());
        this.minecraft.levelRenderer.gameTestBlockHighlightRenderer.highlightPos(p_449067_.absolutePos(), p_449067_.relativePos());
    }

    private void readSectionList(
        int p_171735_,
        int p_171736_,
        LevelLightEngine p_171737_,
        LightLayer p_171738_,
        BitSet p_171739_,
        BitSet p_171740_,
        Iterator<byte[]> p_171741_,
        boolean p_361314_
    ) {
        for (int i = 0; i < p_171737_.getLightSectionCount(); i++) {
            int j = p_171737_.getMinLightSection() + i;
            boolean flag = p_171739_.get(i);
            boolean flag1 = p_171740_.get(i);
            if (flag || flag1) {
                p_171737_.queueSectionData(
                    p_171738_, SectionPos.of(p_171735_, j, p_171736_), flag ? new DataLayer((byte[])p_171741_.next().clone()) : new DataLayer()
                );
                if (p_361314_) {
                    this.level.setSectionDirtyWithNeighbors(p_171735_, j, p_171736_);
                }
            }
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected() && !this.closed;
    }

    public Collection<PlayerInfo> getListedOnlinePlayers() {
        return this.listedPlayers;
    }

    public Collection<PlayerInfo> getOnlinePlayers() {
        return this.playerInfoMap.values();
    }

    public Collection<UUID> getOnlinePlayerIds() {
        return this.playerInfoMap.keySet();
    }

    public @Nullable PlayerInfo getPlayerInfo(UUID p_104950_) {
        return this.playerInfoMap.get(p_104950_);
    }

    public @Nullable PlayerInfo getPlayerInfo(String p_104939_) {
        for (PlayerInfo playerinfo : this.playerInfoMap.values()) {
            if (playerinfo.getProfile().name().equals(p_104939_)) {
                return playerinfo;
            }
        }

        return null;
    }

    public Map<UUID, PlayerInfo> getSeenPlayers() {
        return this.seenPlayers;
    }

    public @Nullable PlayerInfo getPlayerInfoIgnoreCase(String p_439465_) {
        for (PlayerInfo playerinfo : this.playerInfoMap.values()) {
            if (playerinfo.getProfile().name().equalsIgnoreCase(p_439465_)) {
                return playerinfo;
            }
        }

        return null;
    }

    public GameProfile getLocalGameProfile() {
        return this.localGameProfile;
    }

    public ClientAdvancements getAdvancements() {
        return this.advancements;
    }

    public CommandDispatcher<ClientSuggestionProvider> getCommands() {
        return this.commands;
    }

    public ClientLevel getLevel() {
        return this.level;
    }

    public DebugQueryHandler getDebugQueryHandler() {
        return this.debugQueryHandler;
    }

    public UUID getId() {
        return this.id;
    }

    public Set<ResourceKey<Level>> levels() {
        return this.levels;
    }

    public RegistryAccess.Frozen registryAccess() {
        return this.registryAccess;
    }

    public void markMessageAsProcessed(MessageSignature p_405173_, boolean p_242455_) {
        if (this.lastSeenMessages.addPending(p_405173_, p_242455_) && this.lastSeenMessages.offset() > 64) {
            this.sendChatAcknowledgement();
        }
    }

    private void sendChatAcknowledgement() {
        int i = this.lastSeenMessages.getAndClearOffset();
        if (i > 0) {
            this.send(new ServerboundChatAckPacket(i));
        }
    }

    public void sendChat(String p_249888_) {
        p_249888_ = net.neoforged.neoforge.client.ClientHooks.onClientSendMessage(p_249888_);
        if (p_249888_.isEmpty()) return;
        Instant instant = Instant.now();
        long i = Crypt.SaltSupplier.getLong();
        LastSeenMessagesTracker.Update lastseenmessagestracker$update = this.lastSeenMessages.generateAndApplyUpdate();
        MessageSignature messagesignature = this.signedMessageEncoder
            .pack(new SignedMessageBody(p_249888_, instant, i, lastseenmessagestracker$update.lastSeen()));
        this.send(new ServerboundChatPacket(p_249888_, instant, i, messagesignature, lastseenmessagestracker$update.update()));
    }

    public void sendCommand(String p_250092_) {
        if (net.neoforged.neoforge.client.ClientCommandHandler.runCommand(p_250092_)) return;
        SignableCommand<ClientSuggestionProvider> signablecommand = SignableCommand.of(this.commands.parse(p_250092_, this.suggestionsProvider));
        if (signablecommand.arguments().isEmpty()) {
            this.send(new ServerboundChatCommandPacket(p_250092_));
        } else {
            Instant instant = Instant.now();
            long i = Crypt.SaltSupplier.getLong();
            LastSeenMessagesTracker.Update lastseenmessagestracker$update = this.lastSeenMessages.generateAndApplyUpdate();
            ArgumentSignatures argumentsignatures = ArgumentSignatures.signCommand(signablecommand, p_247875_ -> {
                SignedMessageBody signedmessagebody = new SignedMessageBody(p_247875_, instant, i, lastseenmessagestracker$update.lastSeen());
                return this.signedMessageEncoder.pack(signedmessagebody);
            });
            this.send(new ServerboundChatCommandSignedPacket(p_250092_, instant, i, argumentsignatures, lastseenmessagestracker$update.update()));
        }
    }

    public void sendUnattendedCommand(String p_425988_, @Nullable Screen p_427225_) {
        // Neo: Dispatch client commands for text component click actions.
        if (net.neoforged.neoforge.client.ClientCommandHandler.runCommand(p_425988_)) return;
        switch (this.verifyCommand(p_425988_)) {
            case NO_ISSUES:
                this.send(new ServerboundChatCommandPacket(p_425988_));
                this.minecraft.setScreen(p_427225_);
                break;
            case PARSE_ERRORS:
                this.openCommandSendConfirmationWindow(p_425988_, "multiplayer.confirm_command.parse_errors", p_427225_);
                break;
            case SIGNATURE_REQUIRED:
                this.openSignedCommandSendConfirmationWindow(p_425988_, "multiplayer.confirm_command.signature_required", p_427225_);
                break;
            case PERMISSIONS_REQUIRED:
                this.openCommandSendConfirmationWindow(p_425988_, "multiplayer.confirm_command.permissions_required", p_427225_);
        }
    }

    private ClientPacketListener.CommandCheckResult verifyCommand(String p_426209_) {
        ParseResults<ClientSuggestionProvider> parseresults = this.commands.parse(p_426209_, this.suggestionsProvider);
        if (!isValidCommand(parseresults)) {
            return ClientPacketListener.CommandCheckResult.PARSE_ERRORS;
        } else if (SignableCommand.hasSignableArguments(parseresults)) {
            return ClientPacketListener.CommandCheckResult.SIGNATURE_REQUIRED;
        } else {
            ParseResults<ClientSuggestionProvider> parseresults1 = this.commands.parse(p_426209_, this.restrictedSuggestionsProvider);
            return !isValidCommand(parseresults1)
                ? ClientPacketListener.CommandCheckResult.PERMISSIONS_REQUIRED
                : ClientPacketListener.CommandCheckResult.NO_ISSUES;
        }
    }

    private static boolean isValidCommand(ParseResults<?> p_425800_) {
        return !p_425800_.getReader().canRead() && p_425800_.getExceptions().isEmpty() && p_425800_.getContext().getLastChild().getCommand() != null;
    }

    private void openSendConfirmationWindow(String p_437232_, String p_437279_, Component p_437192_, Runnable p_437237_) {
        Screen screen = this.minecraft.screen;
        this.minecraft
            .setScreen(
                new ConfirmScreen(
                    p_437167_ -> {
                        if (p_437167_) {
                            p_437237_.run();
                        } else {
                            this.minecraft.setScreen(screen);
                        }
                    },
                    COMMAND_SEND_CONFIRM_TITLE,
                    Component.translatable(p_437279_, Component.literal(p_437232_).withStyle(ChatFormatting.YELLOW)),
                    p_437192_,
                    screen != null ? CommonComponents.GUI_BACK : CommonComponents.GUI_CANCEL
                )
            );
    }

    private void openCommandSendConfirmationWindow(String p_426004_, String p_425994_, @Nullable Screen p_427498_) {
        this.openSendConfirmationWindow(p_426004_, p_425994_, BUTTON_RUN_COMMAND, () -> {
            this.send(new ServerboundChatCommandPacket(p_426004_));
            this.minecraft.setScreen(p_427498_);
        });
    }

    private void openSignedCommandSendConfirmationWindow(String p_437304_, String p_437330_, @Nullable Screen p_437401_) {
        boolean flag = p_437401_ == null && this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer());
        this.openSendConfirmationWindow(p_437304_, p_437330_, flag ? BUTTON_SUGGEST_COMMAND : CommonComponents.GUI_COPY_TO_CLIPBOARD, () -> {
            if (flag) {
                this.minecraft.openChatScreen(ChatComponent.ChatMethod.COMMAND);
                if (this.minecraft.screen instanceof ChatScreen chatscreen) {
                    chatscreen.insertText(p_437304_, false);
                }
            } else {
                this.minecraft.keyboardHandler.setClipboard("/" + p_437304_);
                this.minecraft.setScreen(p_437401_);
            }
        });
    }

    public void broadcastClientInformation(ClientInformation p_360411_) {
        if (!p_360411_.equals(this.remoteClientInformation)) {
            this.send(new ServerboundClientInformationPacket(p_360411_));
            this.remoteClientInformation = p_360411_;
        }
    }

    @Override
    public void tick() {
        if (this.chatSession != null && this.minecraft.getProfileKeyPairManager().shouldRefreshKeyPair()) {
            this.prepareKeyPair();
        }

        if (this.keyPairFuture != null && this.keyPairFuture.isDone()) {
            this.keyPairFuture.join().ifPresent(this::setKeyPair);
            this.keyPairFuture = null;
        }

        this.sendDeferredPackets();
        if (this.minecraft.getDebugOverlay().showNetworkCharts()) {
            this.pingDebugMonitor.tick();
        }

        if (this.level != null) {
            this.debugSubscriber.tick(this.level.getGameTime());
        }

        this.telemetryManager.tick();
        if (this.levelLoadTracker != null) {
            this.levelLoadTracker.tickClientLoad();
            if (this.levelLoadTracker.isLevelReady()) {
                this.notifyPlayerLoaded();
                this.levelLoadTracker = null;
            }
        }
    }

    private void notifyPlayerLoaded() {
        if (!this.hasClientLoaded()) {
            this.connection.send(new ServerboundPlayerLoadedPacket());
            this.setClientLoaded(true);
        }
    }

    public void prepareKeyPair() {
        this.keyPairFuture = this.minecraft.getProfileKeyPairManager().prepareKeyPair();
    }

    private void setKeyPair(ProfileKeyPair p_261475_) {
        if (this.minecraft.isLocalPlayer(this.localGameProfile.id())) {
            if (this.chatSession == null || !this.chatSession.keyPair().equals(p_261475_)) {
                this.chatSession = LocalChatSession.create(p_261475_);
                this.signedMessageEncoder = this.chatSession.createMessageEncoder(this.localGameProfile.id());
                this.send(new ServerboundChatSessionUpdatePacket(this.chatSession.asRemote().asData()));
            }
        }
    }

    @Override
    protected DialogConnectionAccess createDialogAccess() {
        return new ClientCommonPacketListenerImpl.CommonDialogAccess() {
            @Override
            public void runCommand(String p_427295_, @Nullable Screen p_427359_) {
                ClientPacketListener.this.sendUnattendedCommand(p_427295_, p_427359_);
            }
        };
    }

    public @Nullable ServerData getServerData() {
        return this.serverData;
    }

    public FeatureFlagSet enabledFeatures() {
        return this.enabledFeatures;
    }

    public boolean isFeatureEnabled(FeatureFlagSet p_250605_) {
        return p_250605_.isSubsetOf(this.enabledFeatures());
    }

    public Scoreboard scoreboard() {
        return this.scoreboard;
    }

    public net.neoforged.neoforge.network.connection.ConnectionType getConnectionType() {
        return this.connectionType;
    }

    public PotionBrewing potionBrewing() {
        return this.potionBrewing;
    }

    public FuelValues fuelValues() {
        return this.fuelValues;
    }

    public void updateSearchTrees() {
        this.searchTrees.rebuildAfterLanguageChange();
    }

    public SessionSearchTrees searchTrees() {
        return this.searchTrees;
    }

    public void registerForCleaning(CacheSlot<?, ?> p_400302_) {
        this.cacheSlots.add(new WeakReference<>(p_400302_));
    }

    public HashedPatchMap.HashGenerator decoratedHashOpsGenenerator() {
        return this.decoratedHashOpsGenerator;
    }

    public ClientWaypointManager getWaypointManager() {
        return this.waypointManager;
    }

    public DebugValueAccess createDebugValueAccess() {
        return this.debugSubscriber.createDebugValueAccess(this.level);
    }

    public boolean hasClientLoaded() {
        return this.clientLoaded;
    }

    private void setClientLoaded(boolean p_479100_) {
        this.clientLoaded = p_479100_;
    }

    @OnlyIn(Dist.CLIENT)
    static enum CommandCheckResult {
        NO_ISSUES,
        PARSE_ERRORS,
        SIGNATURE_REQUIRED,
        PERMISSIONS_REQUIRED;
    }
}
