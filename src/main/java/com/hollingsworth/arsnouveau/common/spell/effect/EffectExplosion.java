package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.ANExplosion;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectExplosion extends AbstractEffect {

    public EffectExplosion() {
        super(GlyphLib.EffectExplosionID, "Explosion");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult == null)
            return;

        Vector3d vec = safelyGetHitPos(rayTraceResult);

        float intensity = 0.75f + 0.5f*getBuffCount(augments, AugmentAmplify.class) + 1.5f*getBuffCount(augments, AugmentAOE.class);
        int dampen = getBuffCount(augments, AugmentDampen.class);
        intensity -= 0.5 * dampen;
        Explosion.Mode mode = hasBuff(augments, AugmentDampen.class) ? Explosion.Mode.NONE  : Explosion.Mode.DESTROY;
        mode = hasBuff(augments, AugmentExtract.class) ? Explosion.Mode.BREAK : mode;
        explode(world, shooter, null, null, vec.x, vec.y, vec.z, intensity, false, mode, augments);
    }

    public Explosion explode(World world, @Nullable Entity e, @Nullable DamageSource source, @Nullable ExplosionContext context,
                             double x, double y, double z, float radius, boolean p_230546_11_, Explosion.Mode p_230546_12_, List<AbstractAugment> augments) {
        ANExplosion explosion = new ANExplosion(world, e, source, context, x, y, z, radius, p_230546_11_, p_230546_12_, getAmplificationBonus(augments));
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) return explosion;
        explosion.explode();
        explosion.finalizeExplosion(false);
        if (p_230546_12_ == Explosion.Mode.NONE) {
            explosion.clearToBlow();
        }

        for(PlayerEntity serverplayerentity : world.players()) {
            if (serverplayerentity.distanceToSqr(x, y, z) < 4096.0D) {
                ((ServerPlayerEntity)serverplayerentity).connection.send(new SExplosionPacket(x, y, z, radius, explosion.getToBlow(), explosion.getHitPlayers().get(serverplayerentity)));
            }
        }

        return explosion;
    }

    @Override
    public int getManaCost() {
        return 200;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.TNT;
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Causes an explosion at the location. Amplify increases the damage and size by a small amount, while AOE will increase the size of the explosion by a large amount, but not damage.";
    }
}
