package org.urbanstmt.analytics.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.urbanstmt.analytics.store.AnalyticsStore;
import org.urbanstmt.analytics.store.impl.HBaseAnalyticsStore;



@Path("/hello")
public class SentimentService {


  private final static AnalyticsStore store = new HBaseAnalyticsStore();

  // This method is called if HTML is request
  @SuppressWarnings("unchecked")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String sayHtmlHello(@QueryParam("type") String type) {
	  JSONObject json = new JSONObject();
	  json.put("firstname", "Shahab");
	  json.put("lastname", "Yunus");
	  json.put("type", type);
    return json.toString();
  }

} 