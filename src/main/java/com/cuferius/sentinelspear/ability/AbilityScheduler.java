package com.cuferius.sentinelspear.ability;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

/** Minimal delayed-task scheduler ticked on the server, used to stagger the Ultra Sweep wind-up. */
@EventBusSubscriber
public class AbilityScheduler {

    private record Task(ServerLevel level, long dueTick, Runnable runnable) {}

    private static final List<Task> tasks = new ArrayList<>();

    public static void schedule(ServerLevel level, int delayTicks, Runnable runnable) {
        tasks.add(new Task(level, level.getGameTime() + delayTicks, runnable));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (tasks.isEmpty()) return;
        List<Task> due = new ArrayList<>();
        tasks.removeIf(task -> {
            if (task.dueTick() <= task.level().getGameTime()) {
                due.add(task);
                return true;
            }
            return false;
        });
        for (Task task : due) {
            task.runnable().run();
        }
    }
}
