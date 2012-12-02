package com.photoed.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.datastore.Blob;
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

    //String id = req.getParameter("id");
    //String stringdata = req.getParameter("stringdata");
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
    
    if (requestType.equals("fetchclass")) {
      fetchClass(req, resp);
    }
    
    if (requestType.equals("fetchthumbs")) {
      fetchPicture(req, resp);
    }
    
    if (requestType.equals("fetchcomment")) {
      fetchComment(req, resp);
    }
    
    if (requestType.equals("deletepictures")) {
      deletePictures(req, resp);
    }
    
    // get request type
    // transType = req.getParameter("requestType");

  }
  
  public void deletePictures(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Query que = new Query("Picture");
    Iterable<Entity> results = datastore.prepare(que).asIterable();
    Iterator<Entity> it = results.iterator();
    
    while (it.hasNext()) {
      datastore.delete(it.next().getKey());
    }
    
    Query que2 = new Query("Comment");
    Iterable<Entity> results2 = datastore.prepare(que2).asIterable();
    Iterator<Entity> it2 = results2.iterator();
    
    while(it2.hasNext()) {
      datastore.delete(it.next().getKey());
      
    }
  }
  
  // ONLY USE CLASS FOR FIRST VERSION
  public void createClassGroup (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    //String groupName = req.getParameter("groupname");
    String className = req.getParameter("classname");
    
    Query getcom = new Query("Class");
    getcom.addFilter("classname", Query.FilterOperator.EQUAL, className);
    Iterable<Entity> coms = datastore.prepare(getcom).asIterable();
    Iterator<Entity> comit = coms.iterator();
    
    if (comit.hasNext()) {
      return;
    }
    
    String admin = req.getParameter("adminname");
    
    Entity newClass = new Entity("Class");
    
    newClass.setProperty("classname", className);
    newClass.setProperty("admin", admin);
    
    datastore.put(newClass);
  }
  
  public void createComment (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String className = req.getParameter("classname");
    String groupName = req.getParameter("groupname");
    String imageName = req.getParameter("imagename");
    String comment = req.getParameter("comment");
    String user = req.getParameter("user");
    
    Entity newComment = new Entity("Comment");
    
    newComment.setProperty("user", user);
    newComment.setProperty("class", className);
    newComment.setProperty("group", groupName);
    newComment.setProperty("imagename", imageName);
    newComment.setProperty("comment", comment);
    
    datastore.put(newComment);
  }
  
  public void createPicture(HttpServletRequest req, HttpServletResponse resp) throws IOException, FileUploadException {
    Log.i("received create picture request");
    
    byte[] imgStream = req.getParameter("image").getBytes("UTF-8");
    String imagetest = new String(imgStream);
    Blob imgBlob = new Blob(imgStream);

    String image = req.getParameter("image");
    String className = req.getParameter("classname");
    String imageName = req.getParameter("imagename");
    String user = req.getParameter("username");
    Date date = new Date();

    //Log.i(image);
    Log.i("test convert byte[] back to string");
    //Log.i(imagetest);
    Log.i("size of blob: "+imgBlob.toString());
    
    Entity newPicture = new Entity("Picture");
    newPicture.setProperty("classname", className);
    //newPicture.setProperty("group", groupName);
    newPicture.setProperty("username", user);
    newPicture.setProperty("imagename", date);

    newPicture.setProperty("image", imgBlob);
    newPicture.setProperty("date", date);
    
    datastore.put(newPicture);
    
    String ret = "success";
    resp.getWriter().write(ret);
  }
  
  // ONLY GROUPS IN FIRST VERSION, DO NOT USE THIS
  public void fetchClass (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Query que = new Query("Class");
    Iterable<Entity> results = datastore.prepare(que).asIterable();
    Iterator<Entity> it = results.iterator();
    String ret = "";
    
    while (it.hasNext()) {
      Entity temp = it.next();
      ret += "<class>\n";
      ret += "<group>"+temp.getProperty("classname")+"</group>\n";
      ret += "<admin>"+temp.getProperty("admin")+"</admin>\n";
      ret += "</class>\n";
      
    }
    Log.i(ret);
    resp.getWriter().write(ret);
  }
  
  public void fetchThumbs (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    
  }
  
  public void fetchComment (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Log.i("fetchcomment");
    String imageName = req.getParameter("imagename");
    Log.i(imageName);
    Query getcom = new Query("Comment");
    getcom.addFilter("imagename", Query.FilterOperator.EQUAL, imageName);
    Iterable<Entity> coms = datastore.prepare(getcom).asIterable();
    Iterator<Entity> comit = coms.iterator();
    
    String ret = "";
    
    while (comit.hasNext()) {
      Entity temp2 = comit.next();
      ret += "<comment>\n";
      ret += "<text>"+temp2.getProperty("comment")+"</text>\n";
      ret += "<user>"+temp2.getProperty("user")+"</user>\n";
      ret += "</comment>\n";
    }
    
    Log.i(ret);
    resp.getWriter().write(ret);
    
  }
  
  public void fetchPicture (HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Query que = new Query("Picture");
    String className = req.getParameter("classname");
    que.addFilter("classname", Query.FilterOperator.EQUAL ,className);
    Iterable<Entity> results = datastore.prepare(que).asIterable();
    Iterator<Entity> it = results.iterator();
    String ret = "";
    int counter = 0;
    // Eventually only want to fetch pictures in group X with date > Y
    //String groupName = req.getParameter("groupname");
    //String date = req.getParameter("date");
    
    while (it.hasNext()) {
      counter++;
      Entity temp = it.next();
      Blob newblob = (Blob) temp.getProperty("image");
      byte[] img = newblob.getBytes();
      String imagetest = new String(img);
      ret += "<image>\n";
      ret += "<imagename>"+temp.getProperty("imagename")+"</imagename>\n";
      ret += "<time>"+temp.getProperty("date")+"</time>\n";
      ret += "<user>"+temp.getProperty("username")+"</user>\n";
      ret += "<imagedata>"+imagetest+"</imagedata>\n";
      ret += "</image>\n";
    }
    
    //Log.i("query size: "+counter+" ret message: "+ret);
    resp.getWriter().write(ret);
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
