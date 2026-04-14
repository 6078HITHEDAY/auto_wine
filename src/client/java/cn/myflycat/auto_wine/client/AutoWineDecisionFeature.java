package cn.myflycat.auto_wine.client;

import cn.myflycat.auto_wine.AutoWineFramework;
import cn.myflycat.auto_wine.client.decision.DecisionRuntime;
import cn.myflycat.auto_wine.decision.DefaultDecisionPlanner;
import cn.myflycat.auto_wine.feature.AutoWineFeature;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

/**
 * Client-side feature that wires the {@link DecisionRuntime} into the
 * Fabric client-tick lifecycle.
 *
 * <p>On initialisation, a runtime is created with a {@link DefaultDecisionPlanner}
 * and no execution layer (starts in {@link cn.myflycat.auto_wine.decision.DecisionState#DEGRADED}).
 * When a Baritone-backed {@link cn.myflycat.auto_wine.decision.ExecutionLayer} is
 * available it can be supplied via a dedicated constructor or setter.
 */
public final class AutoWineDecisionFeature implements AutoWineFeature {

    public static final String FEATURE_ID = "decision";

    private static final AutoWineDecisionFeature INSTANCE = new AutoWineDecisionFeature();

    private volatile boolean active;
    private volatile boolean listenerRegistered;
    private volatile DecisionRuntime runtime;

    private AutoWineDecisionFeature() {
    }

    public static AutoWineDecisionFeature instance() {
        return INSTANCE;
    }

    @Override
    public String id() {
        return FEATURE_ID;
    }

    @Override
    public void initialize() {
        active = true;
        // No Baritone execution layer yet – starts in DEGRADED mode automatically.
        runtime = new DecisionRuntime(new DefaultDecisionPlanner(), null);

        if (!listenerRegistered) {
            ClientTickEvents.END_CLIENT_TICK.register(AutoWineDecisionFeature::onClientTick);
            listenerRegistered = true;
        }

        AutoWineFramework.LOGGER.info(
                "Decision feature initialized (execution mode: {})", runtime.getState());
    }

    @Override
    public void shutdown() {
        active = false;
        DecisionRuntime r = runtime;
        if (r != null) {
            r.stop();
        }
        runtime = null;
        AutoWineFramework.LOGGER.info("Decision feature stopped");
    }

    /** Returns the current {@link DecisionRuntime}, or {@code null} when not active. */
    public static DecisionRuntime getRuntime() {
        return INSTANCE.runtime;
    }

    private static void onClientTick(MinecraftClient client) {
        if (!INSTANCE.active) {
            return;
        }
        DecisionRuntime r = INSTANCE.runtime;
        if (r != null) {
            r.tick(client);
        }
    }
}
