package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.entity.debug.IDebuggerProvider;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.InteractionHand;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.InteractionResult;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.InteractionResult;

import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.entity.LivingEntity;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.entity.player.Player;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.Item;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.ItemStack;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.item.context.UseOnContext;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.level.Level;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Debug extends ModItem {
    public static final Path DEBUG_LOG = Paths.get("ars_nouveau", "augment_compatibility.csv");

    public Debug() {
        super(ItemsRegistry.newItemProperties());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (!pPlayer.level.isClientSide() && pInteractionTarget instanceof IDebuggerProvider iDebuggerProvider) {
            try {
                // Write the file
                Path path = Paths.get("ars_nouveau", "entity_log_" + System.currentTimeMillis() + ".log");
                File file = path.toFile();
                Files.createDirectories(path.getParent());
                PrintWriter w = new PrintWriter(new FileWriterWithEncoding(file, "UTF-8", false));
                iDebuggerProvider.getDebugger().writeFile(w);
                PortUtil.sendMessage(pPlayer, Component.translatable("arsnouveau.debug.log_created", path.toString()));
                w.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level world, Player playerIn, InteractionHand handIn) {

        return InteractionResult.SUCCESS;
    }
}
