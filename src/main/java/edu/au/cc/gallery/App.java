package edu.au.cc.gallery;

import edu.au.cc.gallery.tools.Secrets;
import java.util.Scanner;

import java.util.Map;
import java.util.HashMap;

import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;


public class App {

  public static void main(String[] args) throws Exception {

        port(5000);
        UserAdmin db = new UserAdmin();

	db.connect();
        db.addRoutes();
  }
}

