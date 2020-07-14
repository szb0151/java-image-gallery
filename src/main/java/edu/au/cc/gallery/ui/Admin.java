package edu.au.cc.gallery.ui;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;

import java.io.File;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

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

  public static UserDAO getUserDAO() throws Exception {
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
    model.put("username", req.params(":username"));
    return new HandlebarsTemplateEngine()
        .render(new ModelAndView(model, "login.hbs"));
  }

  private String loginExec(Request req, Response res) {
    try {
      String username = req.queryParams("username");
      User u = getUserDAO().getUserByUsername(username);
      if (u == null || !u.getPassword().equals(req.queryParams("password"))) {
        req.session().attribute("username", username);
        res.redirect("/login");
        return "";
      }
      req.session().attribute("username", username);
      res.redirect("/");
      return "";

    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  private boolean isAdmin(String username) {
    return username != null && (username.equals("dongji") || username.equals("fred"));
  }

  private void checkAdmin(Request req, Response res) {
    if (!isAdmin(req.session().attribute("username"))) {
      res.redirect("/login");
      halt();
    }
  }

  private String mainMenu(Request req, Response res) {
		Map<String, Object> model = new HashMap<String, Object>();
    model.put("title", "Main Menu");
    model.put("username", req.session().attribute("username"));
    return new HandlebarsTemplateEngine()
               .render(new ModelAndView(model, "mainMenu.hbs"));
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
    return "";
  }

  public void addRoutes() {
    get("/", (req, res) -> mainMenu(req, res));
    get("/login", (req, res) -> login(req, res));
    post("/loginExec", (req, res) -> loginExec(req, res));
    //before("/admin/*", (req, res) -> checkAdmin(req, res));
    get("/admin/users", (req, res) -> getUsers(req, res));
    get("/admin/addUser", (req, res) -> addUser(req, res));
    post("/admin/addUserExec", (req, res) -> addUserExec(req, res));
    get("/admin/editUser/:username", (req, res) -> editUser(req, res));
    post("/admin/editUserExec/:username", (req, res) -> editUserExec(req, res));
    get("/admin/deleteUser/:username", (req, res) -> deleteUser(req, res));
    get("/admin/deleteUserExec/:username", (req, res) -> deleteUserExec(req, res));
  }

}
