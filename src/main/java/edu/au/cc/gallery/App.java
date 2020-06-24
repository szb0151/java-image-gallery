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
    String portString = System.getenv("JETTY_PORT");
  	if (portString == null || portString.equals("")) {
  		port(5000);
  	} else {
  		port(Integer.parseInt(portString));
  	}

    DB db = new DB();

    db.connect();
    db.addRoutes();
  }
}
