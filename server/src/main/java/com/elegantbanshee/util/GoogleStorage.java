package com.elegantbanshee.util;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.common.base.Charsets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class GoogleStorage {
    private static final String BUCKET_ID = "bubble_com";
    public static final String RAW_URL = "https://storage.googleapis.com/bubble_com/raw/%s";
    public static Storage storage;
    public static Bucket bucket;

    public static String uploadRaw(byte[] body) {
        if (!ImageUtil.isImage(body))
            return null;
        if (bucket == null) {
            Logger.debug("Missing Google bucket");
            return null;
        }
        String id = UUID.randomUUID().toString();
        bucket.create(String.format("raw/%s", id), new ByteArrayInputStream(body));
        return id;
    }

    public static void init() {
        String googleStorageCredentials = System.getenv("GOOGLE_STORAGE_CREDENTIALS");
        Credentials credentials;
        try {
            credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(googleStorageCredentials.getBytes(Charsets.UTF_8)));
        }
        catch (IOException e) {
            Logger.exception(e);
            byte[] bytes = Base64.getDecoder()
                    .decode(System.getenv("GOOGLE_STORAGE_CREDENTIALS")
                            .getBytes(Charsets.UTF_8));
            try {
                credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(bytes));
            }
            catch (IOException ex) {
                ex.printStackTrace();
                storage = null;
                bucket = null;
                return;
            }
        }
        if (credentials == null) {
            Logger.warn("Invalid Google Storage credentials");
            storage = null;
            bucket = null;
            return;
        }
        storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
        Bucket bucket = null;
        try {
            bucket = storage.get(BUCKET_ID);
        }
        catch (StorageException e) {
            Logger.exception(e);
        }
        GoogleStorage.bucket = bucket;
    }

    public static String uploadBubble(byte[] body) {
        if (!ImageUtil.isImage(body))
            return null;
        if (bucket == null) {
            Logger.debug("Missing Google bucket");
            return null;
        }
        String id = UUID.randomUUID().toString();
        bucket.create(String.format("bubble/%s", id), new ByteArrayInputStream(body));
        return id;
    }
}
