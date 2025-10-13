package com.hollingsworth.arsnouveau.common.mob_jar;

import com.hollingsworth.arsnouveau.api.mob_jar.JarBehavior;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ElderGuardianBehavior extends JarBehavior<ElderGuardian> {
    @Override
    public void tick(MobJarTile tile) {
        if (!isPowered(tile)) return;
        if (tile.getLevel().getGameTime() % 1200 == 0) {
            applyMiningFatigue(tile);
        }
    }

    @Override
    public void onRedstonePower(MobJarTile tile) {
        applyMiningFatigue(tile);
    }

    public void applyMiningFatigue(MobJarTile tile) {
        if (tile.getLevel() instanceof ServerLevel serverLevel) {
            MobEffectInstance effect = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 6000, 2);
            List<ServerPlayer> list = MobEffectUtil.addEffectToPlayersAround(serverLevel, entityFromJar(tile), Vec3.atCenterOf(tile.getBlockPos()), 50.0D, effect, 1200);
            list.forEach((player) -> {
                player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, 1.0F));
            });
        }
    }
}
