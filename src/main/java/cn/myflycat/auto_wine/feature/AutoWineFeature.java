package cn.myflycat.auto_wine.feature;

public interface AutoWineFeature {

    String id();

    default boolean enabledByDefault() {
        return true;
    }

    default void preInitialize() {
    }

    default void initialize() {
    }

    default void shutdown() {
    }
}

