package com.orangehrm.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe utilitaire centralisant la création des instances Logger.
 * <p>
 * Objectif en framework Selenium entreprise :
 * - standardiser la récupération des loggers
 * - éviter de répéter LogManager.getLogger(...) partout
 * - conserver une convention unique dans tout le projet
 * - faciliter une future évolution (wrapper custom, enrichissement MDC, etc.)
 * <p>
 * Utilisation typique :
 * <p>
 * private static final Logger log =
 * LoggerManager.getLogger(LoginPage.class);
 * <p>
 * log.info("Login started");
 * log.error("Element not found");
 */
public class LoggerManager {

    /**
     * Retourne une instance Logger associée à la classe transmise.
     * <p>
     * Pourquoi passer la classe en paramètre ?
     * - permet d’identifier automatiquement la source du log
     * - le nom de la classe apparaîtra dans les fichiers logs
     * - facilite le debugging sur gros frameworks Selenium
     * <p>
     * Exemple :
     * LoggerManager.getLogger(LoginPage.class)
     * <p>
     * Produira des logs du type :
     * 2026-04-17 INFO LoginPage - Login successful
     *
     * @param clazz classe appelante qui demande un logger
     * @return instance Logger liée à cette classe
     */
    public static Logger getLogger(Class<?> clazz) {

        // Retourne un logger nommé selon la classe reçue.
        // Bonne pratique entreprise :
        // toujours utiliser le logger contextualisé par classe.
        return LogManager.getLogger(clazz);
    }
}