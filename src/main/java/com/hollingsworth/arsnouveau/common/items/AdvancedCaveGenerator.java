package com.hollingsworth.arsnouveau.common.items;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

public class AdvancedCaveGenerator {
    private final Random random;
    private final PerlinNoise perlinNoise;
    private final double scale;  // Scale factor for noise modulation
    private final Level world;

    public AdvancedCaveGenerator(Level world, long seed, double scale) {
        this.world = world;
        this.random = new Random(seed);
        this.scale = scale;
        this.perlinNoise = new PerlinNoise();
    }

    public void generateCaveNetwork(int startX, int startZ, int verticalStart, int maxDepth, int numberOfBranches) {
        int x = startX;
        int y = verticalStart;
        int z = startZ;

        createCaveSegment(x, y, z, maxDepth, true);

        for (int i = 0; i < numberOfBranches; i++) {
            int branchX = x + random.nextInt(50) - 25;
            int branchY = y - random.nextInt(20);
            int branchZ = z + random.nextInt(50) - 25;
            createCaveSegment(branchX, branchY, branchZ, maxDepth / 2, false);
        }
    }

    private void createCaveSegment(int x, int y, int z, int depth, boolean isMainChannel) {
        int initialY = y; // Save the initial y-coordinate to determine depth difference
        int maxY = y - depth; // Define maximum depth

        for (int d = 0; d < depth; d++) {
            // Calculate depth factor - starts at 1 at the surface, increasing as it goes deeper
            double depthFactor = Math.abs(y - initialY) / (double) depth;
            depthFactor = 1 + (depthFactor * 2); // Start with 1, scale up to 3 towards maximum depth

            // Generate noise to modulate the radius, ensuring the minimum radius is 3 at the surface
            double noiseValue = perlinNoise.noise(x * scale, y * scale, z * scale);
            int radius = (int) ((isMainChannel ? 3 : 2) + ((isMainChannel ? 3 : 2) * noiseValue * depthFactor));

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (distance < radius) {
                            BlockPos pos = new BlockPos(x + dx, y + dy, z + dz);
                            if (world.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                                world.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
            // Move x and z with some randomness and ensure y moves downward
            x += (int)(random.nextInt(5) - 2 + noiseValue * 2);
            z += (int)(random.nextInt(5) - 2 + noiseValue * 2);
            // Gradual descent: only decrease y based on a random threshold influenced by noise
            if (random.nextDouble() < 0.1 + (0.5 * noiseValue)) { // Adjust this probability to control steepness
                y -= 1; // Smaller, more controlled steps downward
                if (y <= maxY) break; // Stop if maximum depth is reached
            }
        }
    }

    public class PerlinNoise {
        private final int[] p; // Doubled permutation to avoid overflow

        public PerlinNoise() {
            p = new int[512];
            int[] permutation = new int[256];
            for (int i = 0; i < 256; i++) {
                permutation[i] = i;  // Initialize the array with numbers 0 to 255
            }
            // Shuffle the array using a random number generator
            Random random = new Random();
            for (int i = 0; i < 256; i++) {
                int j = random.nextInt(256);
                int temp = permutation[i];
                permutation[i] = permutation[j];
                permutation[j] = temp;
            }
            // Duplicate the permutation to avoid modulus 256 operation
            for (int i = 0; i < 256; i++) {
                p[i] = p[i + 256] = permutation[i];
            }
        }

        public double noise(double x, double y, double z) {
            int X = (int) Math.floor(x) & 255,
                    Y = (int) Math.floor(y) & 255,
                    Z = (int) Math.floor(z) & 255;
            x -= Math.floor(x);
            y -= Math.floor(y);
            z -= Math.floor(z);
            double u = fade(x),
                    v = fade(y),
                    w = fade(z);
            int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z,
                    B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z;

            return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z),
                                    grad(p[BA], x - 1, y, z)),
                            lerp(u, grad(p[AB], x, y - 1, z),
                                    grad(p[BB], x - 1, y - 1, z))),
                    lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1),
                                    grad(p[BA + 1], x - 1, y, z - 1)),
                            lerp(u, grad(p[AB + 1], x, y - 1, z - 1),
                                    grad(p[BB + 1], x - 1, y - 1, z - 1))));
        }

        private double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        private double grad(int hash, double x, double y, double z) {
            int h = hash & 15;
            double u = h < 8 ? x : y,
                    v = h < 4 ? y : h == 12 || h == 14 ? x : z;
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }

}

