package com.hollingsworth.arsnouveau.client.particle;


import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;

public class ParticleSpawner
{
    private static Minecraft mc = Minecraft.getInstance();
//
//    public static Particle spawnParticle(EobEnumParticleTypes type, double par2, double par4, double par6, double par8, double par10, double par12)
//    {
//        if (mc != null && mc.getRenderViewEntity() != null && mc.gameRenderer != null)
//        {
//            int var14 = mc.gameSettings.particles.getId();
//
//            if (var14 == 1 && mc.world.rand.nextInt(3) == 0)
//            {
//                var14 = 2;
//            }
//
//            double var15 = mc.getRenderViewEntity().getPosX() - par2;
//            double var17 = mc.getRenderViewEntity().getPosY() - par4;
//            double var19 = mc.getRenderViewEntity().getPosZ() - par6;
//            Particle var21 = null;
//            double var22 = 16.0D;
//
//            if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22)
//            {
//                return null;
//            }
//            else if (var14 > 1)
//            {
//                return null;
//            }
//            else
//            {
////                if (type == EobEnumParticleTypes.FLOWER)
////                {
////                    var21 = new ParticlePetal(mc.world, par2, par4, par6, par8, par10, par12);
////                }
//
//                mc.particles.addEffect(var21);
//                return var21;
//            }
//        }
//        return null;
//    }
}
