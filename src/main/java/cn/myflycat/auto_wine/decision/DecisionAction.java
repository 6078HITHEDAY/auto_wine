package cn.myflycat.auto_wine.decision;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** An immutable atomic action produced by a {@link DecisionPlanner}. */
public class DecisionAction {

    private final DecisionActionType type;
    private final Map<String, String> params;

    private DecisionAction(DecisionActionType type, Map<String, String> params) {
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.params = Collections.unmodifiableMap(new LinkedHashMap<>(params == null ? Collections.emptyMap() : params));
    }

    public static DecisionAction of(DecisionActionType type) {
        return new DecisionAction(type, Collections.emptyMap());
    }

    public static DecisionAction of(DecisionActionType type, Map<String, String> params) {
        return new DecisionAction(type, params);
    }

    public DecisionActionType getType() {
        return type;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public String getParam(String key, String defaultValue) {
        return params.getOrDefault(key, defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecisionAction other)) return false;
        return type == other.type && Objects.equals(params, other.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, params);
    }

    @Override
    public String toString() {
        return "DecisionAction{type=" + type + ", params=" + params + '}';
    }
}
