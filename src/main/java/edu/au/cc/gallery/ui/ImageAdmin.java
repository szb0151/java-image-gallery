package edu.au.cc.gallery.ui;

import edu.au.cc.gallery.data.*;

import static spark.Spark.*;

import spark.Response;
import spark.Request;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import javax.servlet.MultipartConfigElement;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Base64;
import java.io.InputStream;

public class ImageAdmin {

  public static ImageDAO getImageDAO() throws Exception {
    return S3ImageFactory.getImageDAO();
  }

  private static boolean isUser(String username, String currentUser) {
    return username != null && currentUser != null && username.equals(currentUser);
  }

  private static String checkUser(Request req, Response res) {
    try {
      String username = req.params(":username");
      User currentUser = Admin.getUserDAO().getUserByUsername(username);
      if (!isUser(req.session().attribute("username"), username) || currentUser == null) {
        res.redirect("/login");
        halt();
      }

    } catch (Exception e) {
        return "Error: " + e.getMessage();
    }
    return null;
  }

  private static String getUserHome(Request req, Response res) {
    try {
      Map<String, Object> model = new HashMap<>();
      List<Map<String, Object>> images = new ArrayList<>();
      String username = req.params(":username");
      User user = Admin.getUserDAO().getUserByUsername(username);
      model.put("username", user.getUsername());
      List<Image> userImages = getImageDAO().getImages(user);
      if (userImages != null) {
        for (Image i : userImages) {
          Map<String, Object> imageInfo = new HashMap<>();
          imageInfo.put("imagedata", i.getImageData());
          imageInfo.put("uuid", i.getId());
          images.add(imageInfo);
        }
        model.put("images", images);
      }
      return render(model, "userHome.hbs");

    } catch (Exception e) {
        return "Error: " + e.getMessage();
    }
  }

  private static String addImage(Request req, Response res) {
    String username = req.params(":username");
    try {
      req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
      InputStream inputStream = req.raw().getPart("imagedata").getInputStream();
      byte[] imageData = inputStream.readAllBytes();
      String imageDataString = Base64.getEncoder().encodeToString(imageData);
      User user = Admin.getUserDAO().getUserByUsername(username);
      String uuid = UUID.randomUUID().toString();
      Image image = new Image(user, uuid, imageDataString);
      getImageDAO().addImage(user, image);

    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
    res.redirect("/" + username + "/userHome");
    return "";
  }

  private static String deleteImage(Request req, Response res) {
    try {

      String username = req.params(":username");
      String uuid = req.params(":uuid");
      User user = Admin.getUserDAO().getUserByUsername(username);
      System.out.println("***** deleteImage() " + uuid + " *****");
      Image image = getImageDAO().getImage(user, uuid);

      if (image == null) {
        return "";
      }

      getImageDAO().deleteImage(user, image);
      res.redirect("/" + username + "/userHome");

    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }

    return "";
  }

  private static String getImage(Request req, Response res) {

    Map<String, Object> model = new HashMap<>();
    String username = req.params(":username");
    String uuid = req.params(":uuid");
    model.put("username", username);

    try {
      User user = Admin.getUserDAO().getUserByUsername(username);
      Image image = getImageDAO().getImage(user, uuid);
      model.put("image", image.getImageData());
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }

    return render(model, "image.hbs");
  }

  public static String render(Map<String, Object> model, String templatePath) {
    return new HandlebarsTemplateEngine()
              .render(new ModelAndView(model, templatePath));
  }

  public void addRoutes() {
    //before("/:username/*", (req, res) -> checkUser(req, res));
    get("/:username/userHome", (req, res) -> getUserHome(req, res));
    post("/:username/images", (req, res) -> addImage(req, res));
    get("/:username/images/:uuid", (req, res) -> getImage(req, res));
    post("/:username/images/:uuid", (req, res) -> deleteImage(req, res));
  }


}
