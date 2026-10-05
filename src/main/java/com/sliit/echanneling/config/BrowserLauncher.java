package com.sliit.echanneling.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.List;

@Component
@Slf4j
public class BrowserLauncher implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${app.open-browser-on-start:true}")
    private boolean openBrowserOnStart;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!openBrowserOnStart) {
            log.info("Browser launch on startup is disabled (app.open-browser-on-start=false).");
            return;
        }

        int port = 8080;
        if (event.getApplicationContext() instanceof WebServerApplicationContext webServerContext) {
            try {
                if (webServerContext.getWebServer() != null) {
                    port = webServerContext.getWebServer().getPort();
                }
            } catch (Exception ex) {
                log.debug("Could not determine port from WebServer: {}", ex.getMessage());
            }
        } else {
            String portStr = event.getApplicationContext().getEnvironment().getProperty("server.port", "8080");
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException ignored) {
            }
        }

        // Port -1 indicates a non-listening test/mock environment
        if (port <= 0) {
            log.debug("Embedded server not active (port={}), skipping browser launch.", port);
            return;
        }

        String url = "http://localhost:" + port;
        launchBrowser(url);
    }

    private void launchBrowser(String url) {
        log.info("Opening browser for {}...", url);

        // 1. Prioritize Google Chrome
        List<String> knownChromePaths = List.of(
                "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
                "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
                System.getenv("LOCALAPPDATA") != null ? System.getenv("LOCALAPPDATA") + "\\Google\\Chrome\\Application\\chrome.exe" : ""
        );

        for (String chromePath : knownChromePaths) {
            if (!chromePath.isBlank()) {
                File chromeBinary = new File(chromePath);
                if (chromeBinary.exists()) {
                    try {
                        new ProcessBuilder(chromeBinary.getAbsolutePath(), url).start();
                        log.info("Successfully opened Google Chrome: {}", chromePath);
                        return;
                    } catch (Exception ex) {
                        log.warn("Failed launching Chrome via {}: {}", chromePath, ex.getMessage());
                    }
                }
            }
        }

        // 2. Microsoft Edge fallback
        List<String> knownEdgePaths = List.of(
                "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
                "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe"
        );

        for (String edgePath : knownEdgePaths) {
            File edgeBinary = new File(edgePath);
            if (edgeBinary.exists()) {
                try {
                    new ProcessBuilder(edgeBinary.getAbsolutePath(), url).start();
                    log.info("Successfully opened Microsoft Edge via binary: {}", edgePath);
                    return;
                } catch (Exception ex) {
                    log.warn("Failed launching Edge via {}: {}", edgePath, ex.getMessage());
                }
            }
        }

        // 3. Fallback: Java Desktop API
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                log.info("Successfully opened browser via Java Desktop API.");
                return;
            } catch (Exception ex) {
                log.warn("Failed opening browser via Desktop API: {}", ex.getMessage());
            }
        }

        // 4. Fallback: Standard Windows default browser
        try {
            new ProcessBuilder("cmd", "/c", "start", "", url).start();
            log.info("Opened application via default browser command.");
        } catch (Exception ex) {
            log.warn("Could not open browser automatically. Please open {} manually in your browser.", url);
        }
    }
}
