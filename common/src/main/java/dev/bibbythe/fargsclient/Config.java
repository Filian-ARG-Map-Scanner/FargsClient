package dev.bibbythe.fargsclient;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class Config {

    public UUID clientId = UUID.randomUUID();
    public String host = "fargs.bibbythe.dev";
    public int port = 1752;
    public String virtualHost = "fargs";
    public String directExchangeName = "client-direct";
    public String fanoutExchangeName = "client-fanout";
    public String username = "fargsclient";
    public String password = "fargsclient";

    public static Config loadConfig(Path configDir) {
        try {
            Yaml yaml = new Yaml();
            Files.createDirectories(configDir);
            if (Files.notExists(configDir.resolve("config.yml"))) {
                return new Config().saveConfig(configDir);
            }
            return yaml.loadAs(Files.newInputStream(configDir.resolve("config.yml")), Config.class);
        } catch (IOException e) {
            FargsClient.LOGGER.error("Failed to load config", e);
        }
        return null;
    }

    public Config saveConfig(Path configDir) {
        try {
            Yaml yaml = new Yaml();
            Files.createDirectories(configDir);
            yaml.dump(this, Files.newBufferedWriter(
                    configDir.resolve("config.yml"),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            ));
            return this;
        } catch (IOException e) {
            FargsClient.LOGGER.error("Failed to load config", e);
        }
        return null;
    }

}
