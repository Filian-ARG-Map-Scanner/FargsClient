package dev.bibbythe.fargsclient;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class Config {

    public String clientId = UUID.randomUUID().toString();
    public String host = "fargs.bibbythe.dev";
    public int port = 1752;
    public String virtualHost = "fargs";
    public String directExchangeName = "client-direct";
    public String fanoutExchangeName = "client-fanout";
    public String username = "fargsclient";
    public String password = "fargsclient";

    public static Config loadConfig(Path configDir) {
        try {
            LoaderOptions loaderOptions = new LoaderOptions();
            TagInspector taginspector =
                    tag -> tag.getClassName().equals(Config.class.getName());
            loaderOptions.setTagInspector(taginspector);
            Yaml yaml = new Yaml(new Constructor(Config.class, loaderOptions));
            Files.createDirectories(configDir);
            if (Files.notExists(configDir.resolve("config.yml"))) {
                return new Config().saveConfig(configDir);
            }
            try (InputStream in = Files.newInputStream(configDir.resolve("config.yml"))) {
                return yaml.load(in);
            }
        } catch (IOException e) {
            FargsClient.LOGGER.error("Failed to load config", e);
        }
        return null;
    }

    public Config saveConfig(Path configDir) {
        try {
            Representer customRepresenter = new Representer(new DumperOptions());
            customRepresenter.addClassTag(Config.class, Tag.MAP);
            Yaml yaml = new Yaml(new Constructor(Config.class, new LoaderOptions()),
                    customRepresenter, new DumperOptions());
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
