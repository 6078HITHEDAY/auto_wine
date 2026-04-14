package cn.myflycat.auto_wine.decision;

import cn.myflycat.auto_wine.AutoWineFramework;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Registry for {@link DecisionGoal} factories, keyed by goal ID.
 *
 * <p>Concrete goal implementations (which may depend on Minecraft client
 * classes) register their factories here; the planner or runtime can then
 * create goal instances by ID without a hard compile-time dependency.
 */
public final class DecisionGoalRegistry {

    /** Factory that creates a new {@link DecisionGoal} instance for a given context. */
    public interface GoalFactory {
        DecisionGoal create(DecisionContext context);
    }

    private static final Map<String, GoalFactory> FACTORIES = new LinkedHashMap<>();

    private DecisionGoalRegistry() {
    }

    public static synchronized void register(String goalId, GoalFactory factory) {
        if (goalId == null || goalId.isBlank()) {
            throw new IllegalArgumentException("goalId must not be blank");
        }
        if (factory == null) {
            throw new IllegalArgumentException("factory must not be null");
        }
        GoalFactory previous = FACTORIES.putIfAbsent(goalId, factory);
        if (previous != null) {
            throw new IllegalStateException("Duplicate goal id: " + goalId);
        }
        AutoWineFramework.LOGGER.debug("Registered decision goal factory: {}", goalId);
    }

    public static synchronized Optional<DecisionGoal> create(String goalId, DecisionContext context) {
        GoalFactory factory = FACTORIES.get(goalId);
        if (factory == null) {
            return Optional.empty();
        }
        return Optional.of(factory.create(context));
    }

    public static synchronized boolean isRegistered(String goalId) {
        return FACTORIES.containsKey(goalId);
    }

    public static synchronized Set<String> registeredGoalIds() {
        return Collections.unmodifiableSet(FACTORIES.keySet());
    }
}
