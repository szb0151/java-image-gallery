package edu.au.cc.gallery.ui;

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
      res.redirect("/admin");
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
    model.put("onNo", "/admin");
    return new HandlebarsTemplateEngine()
        .render(new ModelAndView(model, "confirm.hbs"));
  }

  private String deleteUserExec(Request req, Response res) {
    try {
      getUserDAO().deleteUser(req.params(":username"));
      res.redirect("/admin");
      return "";
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  public void addRoutes() {
    get("/admin", (req,res) -> getUsers(req, res));
    get("/admin/addUser", (req, res) -> addUser(req, res));
    post("/admin/addUserExec", (req, res) -> addUserExec(req, res));
    // get("/admin/editUser/:username", (req, res) -> editUserPage(req, res));
    // post("/admin/editUser/:username", (req, res) -> editUser(req, res));
    get("/admin/deleteUser/:username", (req, res) -> deleteUser(req, res));
    get("/admin/deleteUserExec/:username", (req, res) -> deleteUserExec(req, res));
  }

}
