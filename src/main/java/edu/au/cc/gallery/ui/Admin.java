package edu.au.cc.gallery.ui;


import spark.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import static spark.Spark.*;
import spark.Request;
import spark.Response;

import spark.ModelAndView;

import edu.au.cc.gallery.data.Postgres;
import edu.au.cc.gallery.data.UserDAO;
import edu.au.cc.gallery.data.User;

import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Admin {

  private static UserDAO getUserDAO() throws Exception {
    return Postgres.getUserDAO();
  }

  private static String getUsers(Request req, Response res) {
    try {
      Map<String, Object> model = new HashMap<>();
      model.put("users", getUserDAO().getUsers());
      return new HandlebarsTemplateEngine()
          .render(new ModelAndView(model, "admin.hbs"));
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }

  }

  private String addUser(Request req, Response res) {
    Map<String, Object> model = new HashMap<>();
    model.put("title", "Add User");
    return new HandlebarsTemplateEngine()
        .render(new ModelAndView(model, "addUser.hbs"));
  }

  private String addUserExec(Request req, Response res) {
    try {
      getUserDAO().addUser(new User(req.queryParams("username"),
                                    req.queryParams("password"),
                                    req.queryParams("fullName")));
      res.redirect("/admin/users");
      return "";
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  private String editUser(Request req, Response res) {
    Map<String, Object> model = new HashMap<>();
    model.put("title", "Edit User");
    model.put("username", req.params(":username"));
    return new HandlebarsTemplateEngine()
        .render(new ModelAndView(model, "editUser.hbs"));
  }

  private String editUserExec(Request req, Response res) {
    try {
      String username = req.params(":username");
      String password = "";
      String fullName = "";

      for (User u : getUserDAO().getUsers()) {
        if (u.getUsername().equals(username)) {
          password = req.queryParams("password").isEmpty() ?
                     u.getPassword() : req.queryParams("password");
          u.setPassword(password);
          fullName = req.queryParams("fullName").isEmpty() ?
                     u.getFullName() : req.queryParams("fullName");
          u.setFullName(fullName);
        }
      }

      getUserDAO().editUser(password, fullName, username);
      res.redirect("/admin/users");
      return "";
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  private String deleteUser(Request req, Response res) {
    Map<String, Object> model = new HashMap<>();
    model.put("title", "Delete User");
    model.put("message", "Are you sure you want to delete this user?");
    model.put("onYes", "/admin/deleteUserExec/" + req.params(":username"));
    model.put("onNo", "/admin/users");
    return new HandlebarsTemplateEngine()
        .render(new ModelAndView(model, "confirm.hbs"));
  }

  private String deleteUserExec(Request req, Response res) {
    try {
      getUserDAO().deleteUser(req.params(":username"));
      res.redirect("/admin/users");
      return "";
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  private String login(Request req, Response res) {
    Map<String, Object> model = new HashMap<>();
    return new HandlebarsTemplateEngine()
        .render(new ModelAndView(model, "login.hbs"));
  }

  private String loginPost(Request req, Response res) {
    try {
      String username = req.queryParams("username");
      User u = getUserDAO().getUserByUsername(username);
      if (u == null || !u.getPassword().equals(req.queryParams("password"))) {
        req.session().attribute("user", username);
        res.redirect("/login");
        return "";
      }
      req.session().attribute("user", username);
      res.redirect("/");
      return "";
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  private boolean isAdmin(String username) {
    return username != null && username.equals("fred");
  }

  private void checkAdmin(Request req, Response res) {
    if (!isAdmin(req.session().attribute("user"))) {
      res.redirect("/login");
      halt();
    }
  }

  public String mainMenu(Request req, Response resp) {
		Map<String, Object> model = new HashMap<String, Object>();
    return new HandlebarsTemplateEngine()
               .render(new ModelAndView(model, "mainMenu.hbs"));
	}

  public String uploadImage(Request req, Response resp) {
		Map<String, Object> model = new HashMap<String, Object>();
    return new HandlebarsTemplateEngine()
               .render(new ModelAndView(model, "uploadImage.hbs"));
	}

  public String uploadImagePost(Request req, Response resp) throws IOException {
    File uploadDir = new File("upload");
    uploadDir.mkdir(); // create the upload directory if it doesn't exist

    staticFiles.externalLocation("upload");

    Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
    req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

    try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form

      Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
      logInfo(req, tempFile);
      res.redirect("/")
      return "";

    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
	}

  // methods used for logging
  private static void logInfo(Request req, Path tempFile) throws IOException, ServletException {
    try {
      System.out.println("Uploaded file '" + getFileName(req.raw().getPart("uploaded_file")) + "' saved as '"
                        + tempFile.toAbsolutePath() + "'");
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }

  }

  private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

  public void addRoutes() {
    get("/", (req, res) -> mainMenu(req, res));
    get("/login", (req, res) -> login(req, res));
    post("/login", (req, res) -> loginPost(req, res));
    before("/admin/*", (req, res) -> checkAdmin(req, res));
    get("/admin/users", (req, res) -> getUsers(req, res));
    get("/admin/addUser", (req, res) -> addUser(req, res));
    post("/admin/addUserExec", (req, res) -> addUserExec(req, res));
    get("/admin/editUser/:username", (req, res) -> editUser(req, res));
    post("/admin/editUserExec/:username", (req, res) -> editUserExec(req, res));
    get("/admin/deleteUser/:username", (req, res) -> deleteUser(req, res));
    get("/admin/deleteUserExec/:username", (req, res) -> deleteUserExec(req, res));
    get("/admin/uploadImage", (req, res) -> uploadImage(req, res));
    post("/admin/uploadImage", (req, res) -> uploadImagePost(req, res));
  }

}
