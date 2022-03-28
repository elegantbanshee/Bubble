package com.elegantbanshee;

import com.elegantbanshee.data.Image;
import com.elegantbanshee.util.GoogleStorage;
import com.elegantbanshee.util.ImageUtil;
import com.elegantbanshee.util.PostgresUtil;
import com.elegantbanshee.util.RedisUtil;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.google.gson.Gson;
import net.coobird.thumbnailator.Thumbnailator;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.ModelAndView;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class BubbleServer {
    
    static void getGeneric(String path, String templatePath) {
        get(path, (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, templatePath));
        });
    }

    public static void putImage(String path) {
        put(path, ((request, response) -> {
            byte[] body = request.bodyAsBytes();
            ByteArrayInputStream imageStream = new ByteArrayInputStream(body);
            BufferedImage image = ImageIO.read(imageStream);


            int width;
            int height;
            if (image.getWidth() > image.getHeight()) {
                width = 800;
                height = 800 * image.getHeight() / image.getWidth();
            }
            else {
                width = image.getWidth() * 800 / image.getHeight();
                height = 800;
            }


            BufferedImage resizedImage = Thumbnailator.createThumbnail(image, width, height);
            ByteArrayOutputStream resizedImageByteOutputStream = new ByteArrayOutputStream();
            boolean wrote = ImageIO.write(resizedImage, "png", resizedImageByteOutputStream);


            String id = GoogleStorage.uploadRaw(resizedImageByteOutputStream.toByteArray());
            JSONObject json = new JSONObject();
            json.put("id", id);
            return json.toString();
        }));
    }

    public static void getRawImage(String path) {
        get(path, ((request, response) -> {
            Webb webb = Webb.create();
            webb.setDefaultHeader(Webb.HDR_USER_AGENT, "org.rolando.Bubble/1.0");
            webb.setBaseUri("");

            String id = request.params("id");
            String url = String.format(GoogleStorage.RAW_URL, id);

            Response<byte[]> image = webb.get(url).asBytes();
            return image.getBody();
        }));
    }

    public static void putImageBubble(String path) {
        put(path, ((request, response) -> {
            String body = request.body();
            String base64Image = body.split(",")[1];

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            String id = GoogleStorage.uploadBubble(imageBytes);

            PostgresUtil.pushImage(id);

            JSONObject json = new JSONObject();
            json.put("id", id);
            return json.toString();
        }));
    }

    public static void getImagePage(String path, String templatePath) {
        get(path, (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("id", request.params("id"));
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, templatePath));
        });
    }

    public static void getTopImages(String path) {
        get(path, (request, response) -> {
            List<Image> topImages = PostgresUtil.getImages(
                    request.queryParamOrDefault("page", "1"));
            return new Gson().toJson(topImages);
        });
    }
}
