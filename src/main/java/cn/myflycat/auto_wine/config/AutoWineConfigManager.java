package cn.myflycat.auto_wine.config;

import cn.myflycat.auto_wine.AutoWineFramework;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class AutoWineConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(AutoWineFramework.MOD_ID);
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.json");

    private static AutoWineConfig config = new AutoWineConfig();
    private static boolean initialized;

    private AutoWineConfigManager() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        reloadInternal();
        initialized = true;
    }

    public static synchronized void reload() {
        reloadInternal();
    }

    public static synchronized AutoWineConfig getConfig() {
        ensureInitialized();
        return config;
    }

    public static synchronized boolean isDebugEnabled() {
        return getConfig().isDebugEnabled();
    }

    public static synchronized void setDebugEnabled(boolean debugEnabled) {
        getConfig().setDebugEnabled(debugEnabled);
        save();
    }

    public static synchronized boolean isFeatureEnabled(String featureId, boolean defaultValue) {
        return getConfig().isFeatureEnabled(featureId, defaultValue);
    }

    public static synchronized void setFeatureEnabled(String featureId, boolean enabled) {
        getConfig().setFeatureEnabled(featureId, enabled);
        save();
    }

    public static synchronized void removeFeatureEnabled(String featureId) {
        getConfig().removeFeatureEnabled(featureId);
        save();
    }

    public static synchronized Path getConfigFile() {
        return CONFIG_FILE;
    }

    public static synchronized void save() {
        ensureDirectory();

        try {
            Path tempFile = CONFIG_DIR.resolve("config.json.tmp");
            Files.writeString(tempFile, GSON.toJson(config), StandardCharsets.UTF_8);
            try {
                Files.move(tempFile, CONFIG_FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (UnsupportedOperationException exception) {
                Files.move(tempFile, CONFIG_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            AutoWineFramework.LOGGER.error("Failed to save config to {}", CONFIG_FILE, exception);
        }
    }

    private static void reloadInternal() {
        ensureDirectory();

        if (!Files.exists(CONFIG_FILE)) {
            config = new AutoWineConfig();
            save();
            return;
        }

        try {
            String json = Files.readString(CONFIG_FILE, StandardCharsets.UTF_8);
            AutoWineConfig loaded = GSON.fromJson(json, AutoWineConfig.class);
            config = loaded == null ? new AutoWineConfig() : loaded;
            config.normalize();
        } catch (IOException | JsonParseException exception) {
            AutoWineFramework.LOGGER.warn("Failed to load config from {}, using defaults", CONFIG_FILE, exception);
            backupCorruptFile();
            config = new AutoWineConfig();
            save();
        }
    }

    private static void backupCorruptFile() {
        if (!Files.exists(CONFIG_FILE)) {
            return;
        }

        Path backupFile = CONFIG_DIR.resolve("config.broken.json");
        try {
            Files.move(CONFIG_FILE, backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            AutoWineFramework.LOGGER.warn("Failed to backup broken config file {}", CONFIG_FILE, exception);
        }
    }

    private static void ensureDirectory() {
        try {
            Files.createDirectories(CONFIG_DIR);
        } catch (IOException exception) {
            AutoWineFramework.LOGGER.error("Failed to create config directory {}", CONFIG_DIR, exception);
        }
    }

    private static void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }
}

