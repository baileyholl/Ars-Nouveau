package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.event.SummonManager;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSummonerChime;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SummonerBell extends ModItem implements IRadialProvider {

    public SummonerBell() {
        super(new Properties().stacksTo(1).component(DataComponentRegistry.SUMMON_BEHAVIOR.get(), ISummon.SummonBehavior.PASSIVE));
    }

    public static ISummon.SummonBehavior getSummonBehavior(ItemStack itemStack) {
        return itemStack.get(DataComponentRegistry.SUMMON_BEHAVIOR.get());
    }

    @Override
    public void postHurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        if (!(attacker instanceof Player)) {
            return;
        }
        for (var summon : SummonManager.getSummonedEntitiesOrDefault(attacker)) {
            if (!(summon instanceof PathfinderMob summonEntity)) continue;
            if (summonEntity.isAlive()) {
                summonEntity.setTarget(target);
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand usedHand) {
        if (!level.isClientSide) {
            ItemStack itemStack = player.getItemInHand(usedHand);

            // Cycle to the next summon behavior
            ISummon.SummonBehavior summonBehavior = getSummonBehavior(itemStack);

            // Hold Shift to set the behavior on the bell without affecting summons
            if (player.isShiftKeyDown()) {
                itemStack.set(DataComponentRegistry.SUMMON_BEHAVIOR.get(), ISummon.SummonBehavior.fromId(summonBehavior.ordinal() + 1));
                // Notify the player of the new behavior
                player.displayClientMessage(Component.translatable("tooltip.ars_nouveau.summon_behavior", Component.translatable("ars_nouveau.summon." + summonBehavior.getSerializedName())), true);
            }

            for (var summon : SummonManager.getSummonedEntitiesOrDefault(player)) {
                if (!(summon instanceof LivingEntity summonEntity)) continue;
                if (summonEntity.isAlive()) {
                    if (summon.getCurrentBehavior() != summonBehavior)
                        summon.setCurrentBehavior(summonBehavior);
                    if (summonEntity.level().dimension() != level.dimension() || player.isShiftKeyDown()) continue;
                    summonEntity.teleportTo(player.getX(), player.getY(), player.getZ());
                }
            }

            //Play an amethyst chime sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.PLAYERS, 3.0f, 0.7f);
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }

        return super.

                use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext
            context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        if (ArsNouveau.proxy.isClientSide()) {
            tooltip2.add(Component.translatable("ars_nouveau.summon_bell.select", KeyMapping.createNameSupplier(ModKeyBindings.OPEN_RADIAL_HUD.getName()).get()));
            tooltip2.add(Component.translatable("tooltip.ars_nouveau.summon_behavior", Component.translatable("ars_nouveau.summon." + getSummonBehavior(stack).getSerializedName())));
        }
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(getRadialMenuProvider()));
    }

    public RadialMenu<String> getRadialMenuProvider() {
        return new RadialMenu<>((int slot) ->
                Networking.sendToServer(new PacketUpdateSummonerChime(slot)),
                getRadialMenuSlotsForDominion(),
                RenderUtils::drawString,
                0);
    }

    public List<RadialMenuSlot<String>> getRadialMenuSlotsForDominion() {
        List<RadialMenuSlot<String>> radialMenuSlots = new ArrayList<>();
        for (var slot : ISummon.SummonBehavior.values()) {
            radialMenuSlots.add(new RadialMenuSlot<>(Component.translatable("ars_nouveau.summon." + slot.getSerializedName()).getString(), slot.toString()));
        }
        return radialMenuSlots;
    }
}
