package com.photoed.server;

import java.io.IOException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.photoed.server.util.Log;

@SuppressWarnings("serial")
public class PhotoEdServerServlet extends HttpServlet {

  private String transType;
  private static DatastoreService datastore = null;

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    Log.i("testing timestamp - request received.");

    resp.setContentType("text/xml");

    String id = req.getParameter("id");
    String stringdata = req.getParameter("stringdata");
    String ret = "";
    datastore = DatastoreServiceFactory.getDatastoreService();

    String requestType = req.getParameter("type");
    if (requestType == null) return;

    if (requestType.equals("getgroups")) {
      Query query = new Query("Groups");
      
      
    }
    
    if (requestType.equals("creategroup")) {
      createGroup(req, resp);
    }
    
    if (requestType.equals("postcomment")) {
      
      
      
    }
    
    if (requestType.equals("getcomments")) {
      
      
      
    }
    
    // need to write a proper xml (or find one) builder
    if (id != null) {
      ret += "<id>" + id + "</id>";
    }

    
    
    
    ret += "<extra>testing sup</extra>";

    if (stringdata != null) {
      ret += "<stringdata>" + stringdata + "</stringdata>";
    }

    // parse the request
    // transType = req.getParameter("requestType");

    resp.getWriter().write(ret);

  }
  
  public void createGroup (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    
  }
  
  public void createComment (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    
  }
  
  public void fetchGroup (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    
  }
  
  public void fetchComment (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    
  }
  
  public void fetchPicture (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
    
  }

  /**
   * TODO: eventually want some way to verify the request is from mobile device one way is to have
   * the client (mobile) set some cookie and then checked
   * 
   * @param cookies
   * @return
   */
  /*
   * private boolean mobileCheck(Cookie[] cookies) {
   * 
   * return true; }
   */
}
