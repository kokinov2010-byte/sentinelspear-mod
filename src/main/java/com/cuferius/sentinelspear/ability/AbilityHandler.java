package com.cuferius.sentinelspear.ability;

import com.cuferius.sentinelspear.item.ModItems;
import com.cuferius.sentinelspear.item.SentinelChainSpearItem;
import com.cuferius.sentinelspear.network.UltraSweepCuePacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Server-authoritative logic for both Sentinel Chain Spear abilities.
 * Cooldowns are tracked here (server tick count) so they can never be bypassed by the client.
 */
public class AbilityHandler {

    // playerUUID -> game time (ticks) when the ability is usable again
    private static final Map<UUID, Long> chainThrowCooldowns = new HashMap<>();
    private static final Map<UUID, Long> ultraSweepCooldowns = new HashMap<>();

    private static boolean isWieldingSpear(Player player) {
        return player.getMainHandItem().getItem() instanceof SentinelChainSpearItem;
    }

    public static long getChainThrowRemainingTicks(ServerPlayer player) {
        long now = player.level().getGameTime();
        return Math.max(0, chainThrowCooldowns.getOrDefault(player.getUUID(), 0L) - now);
    }

    public static long getUltraSweepRemainingTicks(ServerPlayer player) {
        long now = player.level().getGameTime();
        return Math.max(0, ultraSweepCooldowns.getOrDefault(player.getUUID(), 0L) - now);
    }

    // ---------------------------------------------------------------
    // ABILITY 1: Chain Throw & Pull
    // ---------------------------------------------------------------
    public static void handleChainThrow(ServerPlayer player) {
        if (player == null || !isWieldingSpear(player)) return;

        long now = player.level().getGameTime();
        if (now < chainThrowCooldowns.getOrDefault(player.getUUID(), 0L)) {
            return; // still on cooldown
        }

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 reachEnd = eyePos.add(lookVec.scale(SentinelChainSpearItem.ABILITY_RANGE));

        // Entity raytrace
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(SentinelChainSpearItem.ABILITY_RANGE)).inflate(1.0D);
        EntityHitResult entityHit = findClosestEntityHit(player, eyePos, reachEnd, searchBox);

        // Block raytrace
        HitResult blockHit = level.clip(new net.minecraft.world.level.ClipContext(
                eyePos, reachEnd,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                player));

        Vec3 targetPoint = null;
        LivingEntity hitLiving = null;

        if (entityHit != null) {
            double entityDist = eyePos.distanceToSqr(entityHit.getLocation());
            double blockDist = blockHit != null ? eyePos.distanceToSqr(blockHit.getLocation()) : Double.MAX_VALUE;
            if (entityDist <= blockDist) {
                targetPoint = entityHit.getLocation();
                if (entityHit.getEntity() instanceof LivingEntity living) {
                    hitLiving = living;
                }
            }
        }
        if (targetPoint == null && blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
            targetPoint = blockHit.getLocation();
        }

        if (targetPoint == null) {
            // Nothing hit within range - ability whiffs, short feedback, cooldown still applies.
            level.playSound(null, player.blockPosition(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.PLAYERS, 1.0F, 0.6F);
            chainThrowCooldowns.put(player.getUUID(), now + SentinelChainSpearItem.CHAIN_THROW_COOLDOWN_TICKS);
            return;
        }

        chainThrowCooldowns.put(player.getUUID(), now + SentinelChainSpearItem.CHAIN_THROW_COOLDOWN_TICKS);

        // Damage the hit entity
        if (hitLiving != null && hitLiving != player) {
            DamageSource damageSource = player.damageSources().playerAttack(player);
            hitLiving.hurt(damageSource, SentinelChainSpearItem.BASE_ATTACK_DAMAGE);
            // Small knockback toward player's direction to sell the "yank" impact on the target too.
            hitLiving.knockback(0.4D, -lookVec.x, -lookVec.z);
        }

        // Pull the player rapidly toward the target point (grappling hook behaviour).
        Vec3 toTarget = targetPoint.subtract(player.position());
        double distance = toTarget.length();
        if (distance > 1.0D) {
            Vec3 pullMotion = toTarget.normalize().scale(Math.min(distance, SentinelChainSpearItem.ABILITY_RANGE) * 0.18D + 0.6D);
            player.setDeltaMovement(pullMotion);
            player.hurtMarked = true; // force velocity sync to client
        }

        // Chain-link visual: spawn particles along the line from player to target each tick-ish (one burst here).
        spawnChainParticles(level, player.position().add(0, player.getBbHeight() * 0.5, 0), targetPoint);

        level.playSound(null, player.blockPosition(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.2F, 0.7F);
        level.playSound(null, player.blockPosition(), SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 1.5F, 0.5F);
    }

    private static EntityHitResult findClosestEntityHit(ServerPlayer player, Vec3 start, Vec3 end, AABB searchBox) {
        Entity hitEntity = null;
        Vec3 hitLocation = null;
        double closestDistSqr = Double.MAX_VALUE;

        List<Entity> candidates = player.level().getEntities(player, searchBox,
                e -> e instanceof LivingEntity && e.isAlive() && e != player);

        for (Entity candidate : candidates) {
            AABB box = candidate.getBoundingBox().inflate(0.3D);
            var clip = box.clip(start, end);
            if (clip.isPresent()) {
                double distSqr = start.distanceToSqr(clip.get());
                if (distSqr < closestDistSqr) {
                    closestDistSqr = distSqr;
                    hitEntity = candidate;
                    hitLocation = clip.get();
                }
            }
        }

        if (hitEntity == null) return null;
        return new EntityHitResult(hitEntity, hitLocation);
    }

    private static void spawnChainParticles(ServerLevel level, Vec3 from, Vec3 to) {
        Vec3 diff = to.subtract(from);
        int steps = (int) Math.max(4, diff.length() * 3);
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            Vec3 p = from.add(diff.scale(t));
            level.sendParticles(ParticleTypes.CRIMSON_SPORE, p.x, p.y, p.z, 1, 0, 0, 0, 0.0);
            if (i % 2 == 0) {
                level.sendParticles(ParticleTypes.SMOKE, p.x, p.y, p.z, 1, 0.01, 0.01, 0.01, 0.0);
            }
        }
    }

    // ---------------------------------------------------------------
    // ABILITY 2: Ultra Sweep Attack
    // ---------------------------------------------------------------

    /** Called when the server receives the client's request to start the Ultra Sweep. Handles the cue + delay. */
    public static void handleUltraSweepRequest(ServerPlayer player) {
        if (player == null || !isWieldingSpear(player)) return;

        long now = player.level().getGameTime();
        if (now < ultraSweepCooldowns.getOrDefault(player.getUUID(), 0L)) {
            return;
        }
        // Reserve the cooldown immediately so double-presses can't queue two sweeps.
        ultraSweepCooldowns.put(player.getUUID(), now + SentinelChainSpearItem.ULTRA_SWEEP_COOLDOWN_TICKS);

        ServerLevel level = player.serverLevel();

        // Tell the attacker's client to force third person + show the title text immediately.
        PacketDistributor.sendToPlayer(player, new UltraSweepCuePacket(player.getId(), true));
        // Tell nearby players too, so they also see the title cue (per spec: "display... on the center of the screen").
        for (ServerPlayer nearby : level.getPlayers(p -> p != player && p.distanceToSqr(player) < 64 * 64)) {
            PacketDistributor.sendToPlayer(nearby, new UltraSweepCuePacket(player.getId(), false));
        }

        // Delay the actual strike by a short wind-up (10 ticks = 0.5s) so the title can be read
        // before the spin connects, matching "Immediately after the text appears, the player performs..."
        AbilityScheduler.schedule(level, 10, () -> executeUltraSweep(player));
    }

    private static void executeUltraSweep(ServerPlayer player) {
        if (player == null || !player.isAlive()) return;
        ServerLevel level = player.serverLevel();

        Vec3 center = player.position();
        AABB sweepBox = new AABB(
                center.x - SentinelChainSpearItem.SWEEP_RADIUS, center.y - 2, center.z - SentinelChainSpearItem.SWEEP_RADIUS,
                center.x + SentinelChainSpearItem.SWEEP_RADIUS, center.y + 2, center.z + SentinelChainSpearItem.SWEEP_RADIUS);

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, sweepBox,
                e -> e != player && e.isAlive() && e.distanceToSqr(player) <= SentinelChainSpearItem.SWEEP_RADIUS * SentinelChainSpearItem.SWEEP_RADIUS);

        DamageSource damageSource = player.damageSources().playerAttack(player);

        for (LivingEntity target : targets) {
            target.hurt(damageSource, SentinelChainSpearItem.BASE_ATTACK_DAMAGE);
            Vec3 knockDir = target.position().subtract(center).normalize();
            if (knockDir.lengthSqr() == 0) knockDir = new Vec3(1, 0, 0);
            target.knockback(2.2D, -knockDir.x, -knockDir.z); // extreme knockback
            target.setDeltaMovement(target.getDeltaMovement().add(0, 0.35D, 0));
            target.hurtMarked = true;
        }

        spawnSweepParticles(level, center);
        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 2.0F, 0.6F);
        level.playSound(null, player.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0F, 0.5F);
        level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 0.6F, 1.6F);
    }

    private static void spawnSweepParticles(ServerLevel level, Vec3 center) {
        int rings = 3;
        for (int r = 0; r < rings; r++) {
            double radius = (r + 1) * (SentinelChainSpearItem.SWEEP_RADIUS / rings);
            int points = 48;
            for (int i = 0; i < points; i++) {
                double angle = (2 * Math.PI / points) * i;
                double x = center.x + Math.cos(angle) * radius;
                double z = center.z + Math.sin(angle) * radius;
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, x, center.y + 0.1, z, 1, 0, 0, 0, 0.0);
                level.sendParticles(ParticleTypes.LAVA, x, center.y + 0.3, z, 1, 0.0, 0.02, 0.0, 0.01);
                level.sendParticles(ParticleTypes.CRIMSON_SPORE, x, center.y + 0.5, z, 1, 0.1, 0.1, 0.1, 0.02);
            }
        }
        level.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 1, center.z, 1, 0, 0, 0, 0.0);
    }
}
