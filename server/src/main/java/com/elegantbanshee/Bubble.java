package com.elegantbanshee;

import com.elegantbanshee.data.Constants;
import com.elegantbanshee.util.GoogleStorage;
import com.elegantbanshee.util.Logger;
import com.elegantbanshee.util.RedisUtil;

import java.util.logging.Level;

import static spark.Spark.*;

public class Bubble {
	
	public static void main(String[] args) {
		// Init logger
        Logger.setLevel(Level.parse("VERBOSE"));
        Logger.info("Starting Bubble %s", Constants.VERSION);
        // Parse port
        int port = 5000;
        String portString = System.getenv("PORT");
        try {
            if (portString != null && !portString.isEmpty())
                port = Integer.parseInt(portString);
        }
        catch (NumberFormatException e) {
            Logger.warn("Failed to parse PORT env var: %s", portString);
        }
        // Set values
        port(port);
        staticFiles.location("/static/");
        staticFiles.expireTime(60 * 60); // One Week cache
        // Web
        BubbleServer.getGeneric("/", "index.hbs");
        BubbleServer.getGeneric("/edit", "edit.hbs");
        BubbleServer.getGeneric("/upload", "upload.hbs");
        BubbleServer.putImage("/api/image/upload");
        BubbleServer.putImageBubble("/api/image/upload_bubble");
        BubbleServer.getRawImage("/api/image/raw/:id");
        BubbleServer.getTopImages("/api/image/top");
        BubbleServer.getImagePage("/:id", "image.hbs");
        // init
        GoogleStorage.init();
        RedisUtil.init();
	}
	
	/**
     * Get an environment variable or log and die
     * @param name env var
     */
    private static String getenv(String name) {
        String env = System.getenv(name);
        if (env == null || env.isEmpty()) {
            Logger.warn("Missing required environment variable: %s", name);
            System.exit(1);
        }
        return env;
    }
}
