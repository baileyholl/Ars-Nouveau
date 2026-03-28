package com.hollingsworth.arsnouveau.client.renderer.world;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.method.MethodPantomime;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PantomimeRenderer {
    public static void renderOutline(PoseStack poseStack) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ICasterTool)) return;

        var caster = SpellCasterRegistry.from(heldItem);
        if (caster == null)
            return;
        Spell selectedSpell = caster.getSpell();

        if (!(selectedSpell.getCastMethod() instanceof MethodPantomime pantomime)) return;
        SpellStats stats = new SpellStats.Builder()
                .setAugments(selectedSpell.getAugments(0, player))
                .addItemsFromEntity(player)
                .build(MethodPantomime.INSTANCE, null, player.level(), player, SpellContext.fromEntity(selectedSpell, player, heldItem));
        if (!stats.isSensitive()) {
            return;
        }
        BlockPos pos = pantomime.findPosition(player, stats).getBlockPos();
        poseStack.pushPose();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().position();
        poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

        // TODO 1.21.11: RenderType.lines() and LevelRenderer.renderLineBox() removed in 1.21.11
        // Need to port to new line rendering API when available
        // int color = selectedSpell.particleTimeline().get(ParticleTimelineRegistry.PANTOMIME_TIMELINE.get()).onResolvingEffect.particleOptions().colorProp().color().getColor();
        // LevelRenderer.renderLineBox(poseStack, lines, 0, 0, 0, 1, 1, 1, ARGB.red(color) / 255F, ARGB.green(color) / 255F, ARGB.blue(color) / 255F, 1);

        poseStack.popPose();
    }
}
