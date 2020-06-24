package edu.au.cc.gallery;

import java.util.List;


public interface UserDAO {
  /*
   * @return return the (possibly empty) list of Users
   */

  List<User> getUsers() throws Exception;

  /*
   * @return user with specified username or null if no such users
   */
  User getUserByUsername(String username) throws Exception;
}
