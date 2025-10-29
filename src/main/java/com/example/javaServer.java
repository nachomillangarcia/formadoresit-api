package com.example;

import static spark.Spark.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class javaServer {
    private static final Logger logger = LoggerFactory.getLogger(javaServer.class);

    public static void main(String[] args) {

        String logLevelEnv = System.getenv("LOG_LEVEL");
        Level logLevel = Level.DEBUG; // Default to DEBUG
        if (logLevelEnv != null) {
            try {
                logLevel = Level.valueOf(logLevelEnv.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid LOG_LEVEL '{}', using default DEBUG.", logLevelEnv);
            }
        }

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("com.example").setLevel(logLevel);

        int apiPort = 8080;
        String portEnv = System.getenv("API_PORT");
        if (portEnv != null) {
            try {
                apiPort = Integer.parseInt(portEnv);
            } catch (NumberFormatException e) {
                logger.warn("Invalid API_PORT value '{}', using default 8080.", portEnv);
            }
        }
        port(apiPort);

        before((req, res) -> {
            logger.info("Received {} request for {} from {}",
                    req.requestMethod(),
                    req.uri(),
                    req.ip());
        });

        get("/", (req, res) -> "Hello from Java inside Docker! 1");
        get("/health", (req, res) -> "OK");
        get("/home", (req, res) -> {
            try {
                String content = Files.readString(Paths.get("/config/message.txt"), StandardCharsets.UTF_8);
                return content;
            } catch (Exception e) {
                logger.error("Failed to read /config/message.txt", e);
                res.status(500);
                return "Error reading message file.";
            }
        });
    }
}
