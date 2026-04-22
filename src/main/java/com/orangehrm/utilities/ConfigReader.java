package com.orangehrm.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Classe utilitaire responsable de la lecture du fichier config.properties.
 * Centralise toutes les configurations du framework.
 */
public class ConfigReader {

    private static Properties prop;

    public static Properties initProp() {
        try {
            FileInputStream fis = new FileInputStream(
                    System.getProperty("user.dir") +
                            "/src/main/resources/config.properties"
            );

            prop = new Properties();
            prop.load(fis);

        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger config.properties", e);
        }
        return prop;
    }


    /**
     * Récupère une valeur depuis le fichier de configuration.
     *
     * @param key clé (ex: browser, url)
     * @return valeur associée
     */
    public static String get(String key) {
        return prop.getProperty(key);
    }
}