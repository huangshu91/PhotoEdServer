package com.photoed.server;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
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
    
    if (requestType.equals("createclassgroup")) {
      createClassGroup(req, resp);
    }
    
    if (requestType.equals("createpicture")) {
      try {
        createPicture(req, resp);
      } catch (FileUploadException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    if (requestType.equals("createcomment")) {
      createComment(req, resp);
    }
    
    if (requestType.equals("fetchgroup")) {
      fetchGroup(req, resp);
    }
    
    if (requestType.equals("fetchthumbs")) {
      fetchThumbs(req, resp);
    }
    
    if (requestType.equals("fetchcomment")) {
      
      
      
    }
    
    // need to write a proper xml (or find one) builder
    if (id != null) {
      ret += "<id>" + id + "</id>";
    }
    ret += "<extra>testing sup</extra>";

    if (stringdata != null) {
      ret += "<stringdata>" + stringdata + "</stringdata>";
    }
    // get request type
    // transType = req.getParameter("requestType");
    resp.getWriter().write(ret);

  }
  
  // ONLY USE GROUP FOR FIRST VERSION
  public void createClassGroup (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String groupName = req.getParameter("groupname");
    String className = req.getParameter("classname");
    String type;
    if (groupName == null) type = "Class"; 
    else type = "Group";
    
    Entity newGroup = new Entity(type);
    
    newGroup.setProperty("members", "");
    
    if (type.equals("class")) newGroup.setProperty("name", className);
    else newGroup.setProperty("name", groupName);
    
    datastore.put(newGroup);
  }
  
  public void createComment (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String className = req.getParameter("classname");
    String groupName = req.getParameter("groupname");
    String imageName = req.getParameter("imagename");
    String comment = req.getParameter("comment");
    
    Entity newComment = new Entity("Comment");
    
    newComment.setProperty("class", className);
    newComment.setProperty("group", groupName);
    newComment.setProperty("imagename", imageName);
    newComment.setProperty("comment", comment);
    
    datastore.put(newComment);
  }
  
  public void createPicture(HttpServletRequest req, HttpServletResponse resp) throws IOException, FileUploadException {
    // Need to double check that the this is the correct encoding for the image
    byte[] imgStream = req.getParameter("image").getBytes("UTF-16");
    
    ImagesService imagesService = ImagesServiceFactory.getImagesService();

    Image oldImage = ImagesServiceFactory.makeImage(imgStream);
    // This does not change resolution, just makes it a MAX of 500 width, 500 height
    Transform resize = ImagesServiceFactory.makeResize(500, 500);

    Image newImage = imagesService.applyTransform(resize, oldImage);

    byte[] newImageData = newImage.getImageData();
    
    String className = req.getParameter("classname");
    String groupName = req.getParameter("groupname");
    String imageName = req.getParameter("imagename");
    
    Entity newPicture = new Entity("Picture");
    newPicture.setProperty("class", className);
    newPicture.setProperty("group", groupName);
    newPicture.setProperty("imagename", imageName);
    newPicture.setProperty("image", newImageData);
    
    datastore.put(newPicture);
  }
  
  // ONLY GROUPS IN FIRST VERSION, DO NOT USE CLASS
  public void fetchGroup (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Query que = new Query("Group");
    Iterable<Entity> results = datastore.prepare(que).asIterable();
    
    
  }
  
  public void fetchThumbs (HttpServletRequest req, HttpServletResponse resp) throws IOException {
  
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
