package edu.au.cc.gallery.ui;



// import static spark.Spark.*;
// import spark.Response;
//
// import spark.ModelAndView;
// 
// import edu.au.cc.gallery.data.Postgres;
// import edu.au.cc.gallery.data.UserDAO;
// import edu.au.cc.gallery.data.User;
//
// import spark.template.handlebars.HandlebarsTemplateEngine;


public class App {

  public static void main(String[] args) throws Exception {
    String portString = System.getenv("JETTY_PORT");
  	if (portString == null || portString.equals("")) {
  		port(5000);
  	} else {
  		port(Integer.parseInt(portString));
  	}

    new Admin().addRoutes();
  }
}
