package com.hollingsworth.arsnouveau.api.particle;

import net.minecraft.world.phys.Vec3;


public class Quaternion {
    private double x, y, z, w;

    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Quaternion fromYawPitch(double yaw, double pitch) {
        double halfYaw = yaw / 2;
        double halfPitch = pitch / 2;

        double cosYaw = Math.cos(halfYaw);
        double sinYaw = Math.sin(halfYaw);
        double cosPitch = Math.cos(halfPitch);
        double sinPitch = Math.sin(halfPitch);

        return new Quaternion(
                sinYaw * cosPitch,
                -cosYaw * sinPitch,
                sinYaw * sinPitch,
                cosYaw * cosPitch
        );
    }

    public Vec3 rotate(Vec3 vec) {
        double tx = 2 * (y * vec.z - z * vec.y);
        double ty = 2 * (z * vec.x - x * vec.z);
        double tz = 2 * (x * vec.y - y * vec.x);

        double rx = vec.x + w * tx + (y * tz - z * ty);
        double ry = vec.y + w * ty + (z * tx - x * tz);
        double rz = vec.z + w * tz + (x * ty - y * tx);

        return new Vec3(rx, ry, rz);
    }
}
