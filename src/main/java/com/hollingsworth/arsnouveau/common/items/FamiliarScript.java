package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;

public class FamiliarScript extends ModItem {
    public AbstractFamiliarHolder familiar;

    public FamiliarScript(AbstractFamiliarHolder familiar) {
        super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, familiar.getRegistryName())));
        this.familiar = familiar;
    }

    public FamiliarScript(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        if (worldIn.isClientSide() || handIn != InteractionHand.MAIN_HAND)
            return super.use(worldIn, playerIn, handIn);

        IPlayerCap familiarCap = CapabilityRegistry.getPlayerDataCap(playerIn);
        if (familiarCap != null) {
            if (familiarCap.ownsFamiliar(familiar)) {
                playerIn.displayClientMessage(Component.translatable("ars_nouveau.familiar.owned"), false);
                return super.use(worldIn, playerIn, handIn);
            }
            familiarCap.unlockFamiliar(familiar);
            CapabilityRegistry.EventHandler.syncPlayerCap(playerIn);
            playerIn.displayClientMessage(Component.translatable("ars_nouveau.familiar.unlocked"), false);
            if (!playerIn.hasInfiniteMaterials()) {
                playerIn.getItemInHand(handIn).shrink(1);
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return Component.translatable("ars_nouveau.bound_script", familiar.getLangName().getString());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        tooltip2.accept(Component.translatable("ars_nouveau.familiar.script"));
    }
}
