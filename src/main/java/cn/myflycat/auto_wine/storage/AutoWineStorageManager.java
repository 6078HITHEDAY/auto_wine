package cn.myflycat.auto_wine.storage;

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

public final class AutoWineStorageManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path STORAGE_DIR = FabricLoader.getInstance().getConfigDir().resolve(AutoWineFramework.MOD_ID);
    private static final Path STORAGE_FILE = STORAGE_DIR.resolve("storage.json");

    private static AutoWineStorage storage = new AutoWineStorage();
    private static boolean initialized;

    private AutoWineStorageManager() {
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

    public static synchronized AutoWineStorage getStorage() {
        ensureInitialized();
        return storage;
    }

    public static synchronized String getValue(String key) {
        return getStorage().getValue(key);
    }

    public static synchronized void setValue(String key, String value) {
        getStorage().setValue(key, value);
        save();
    }

    public static synchronized void removeValue(String key) {
        getStorage().removeValue(key);
        save();
    }

    public static synchronized Path getStorageFile() {
        return STORAGE_FILE;
    }

    public static synchronized void save() {
        ensureDirectory();

        try {
            Path tempFile = STORAGE_DIR.resolve("storage.json.tmp");
            Files.writeString(tempFile, GSON.toJson(storage), StandardCharsets.UTF_8);
            try {
                Files.move(tempFile, STORAGE_FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (UnsupportedOperationException exception) {
                Files.move(tempFile, STORAGE_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            AutoWineFramework.LOGGER.error("Failed to save storage to {}", STORAGE_FILE, exception);
        }
    }

    private static void reloadInternal() {
        ensureDirectory();

        if (!Files.exists(STORAGE_FILE)) {
            storage = new AutoWineStorage();
            save();
            return;
        }

        try {
            String json = Files.readString(STORAGE_FILE, StandardCharsets.UTF_8);
            AutoWineStorage loaded = GSON.fromJson(json, AutoWineStorage.class);
            storage = loaded == null ? new AutoWineStorage() : loaded;
            storage.normalize();
        } catch (IOException | JsonParseException exception) {
            AutoWineFramework.LOGGER.warn("Failed to load storage from {}, using defaults", STORAGE_FILE, exception);
            backupCorruptFile();
            storage = new AutoWineStorage();
            save();
        }
    }

    private static void backupCorruptFile() {
        if (!Files.exists(STORAGE_FILE)) {
            return;
        }

        Path backupFile = STORAGE_DIR.resolve("storage.broken.json");
        try {
            Files.move(STORAGE_FILE, backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            AutoWineFramework.LOGGER.warn("Failed to backup broken storage file {}", STORAGE_FILE, exception);
        }
    }

    private static void ensureDirectory() {
        try {
            Files.createDirectories(STORAGE_DIR);
        } catch (IOException exception) {
            AutoWineFramework.LOGGER.error("Failed to create storage directory {}", STORAGE_DIR, exception);
        }
    }

    private static void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }
}

