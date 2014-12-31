package org.urbanstmt.analytics.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.urbanstmt.analytics.store.AnalyticsStore;
import org.urbanstmt.analytics.store.impl.HBaseAnalyticsStore;


@Path("/getdata")
public class SentimentService {


  private final static AnalyticsStore store = new HBaseAnalyticsStore();

  @SuppressWarnings("unchecked")
	@GET
	@Path("{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAnalyticData(@PathParam("type") String type,
			@QueryParam("stime") String stime,
			@QueryParam("etime") String etime, @QueryParam("bbox") String bbox) {
		JSONObject json = new JSONObject();
		json.put("type", type);
		json.put("bbox", bbox);
		json.put("stime", stime);
		json.put("etime", etime);
		
		return Response.ok(json.toString()).build();
	}

} 