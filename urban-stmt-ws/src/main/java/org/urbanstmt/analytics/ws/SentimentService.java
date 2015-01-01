package org.urbanstmt.analytics.ws;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanstmt.analytics.exception.AnalyticsServiceBadRequestException;
import org.urbanstmt.analytics.model.TermCountRow;
import org.urbanstmt.analytics.model.TermCountRow.LonLatPair;
import org.urbanstmt.analytics.store.AnalyticsStore;
import org.urbanstmt.analytics.store.impl.HBaseAnalyticsStore;

@Path("/getdata")
public class SentimentService {

	private static final Logger LOG = LoggerFactory
			.getLogger(SentimentService.class.getName());
	private final AnalyticsStore store = new HBaseAnalyticsStore();

	@SuppressWarnings("unchecked")
	@GET
	@Path("{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAnalyticData(@PathParam("type") String type,
			@QueryParam("stime") String stime,
			@QueryParam("etime") String etime, @QueryParam("bbox") String bbox) {

		JSONObject responseParent = new JSONObject();
		JSONArray response = new JSONArray();
		try {
			float[] lonLat = new float[8];

			if (bbox != null) {

				String[] lonLatValues = bbox.split(",");
				if (lonLatValues.length < 8) {
					throw new AnalyticsServiceBadRequestException(
							"Not all 4 pairs (8 values) of lon/lat values provided. 4 set of lon/lat pairs in a comma separated list must be passed in. We got="
									+ lonLatValues.length + " values.");
				}

				try {

					lonLat[0] = Float.parseFloat(lonLatValues[0]);
					lonLat[1] = Float.parseFloat(lonLatValues[1]);
					lonLat[2] = Float.parseFloat(lonLatValues[2]);
					lonLat[3] = Float.parseFloat(lonLatValues[3]);
					lonLat[4] = Float.parseFloat(lonLatValues[4]);
					lonLat[5] = Float.parseFloat(lonLatValues[5]);
					lonLat[6] = Float.parseFloat(lonLatValues[6]);
					lonLat[7] = Float.parseFloat(lonLatValues[7]);

					LOG.info("The requested coords="
							+ StringUtils.join(lonLatValues));
				} catch (NumberFormatException nfe) {
					throw new AnalyticsServiceBadRequestException(
							"Invalid float lon/lat values for bounding box provided. What we got="
									+ lonLatValues);
				}
			} else {
				throw new AnalyticsServiceBadRequestException(
						"No bounding box values provided. 4 set of lon/lat pairs in a comma separated list must be passed in.");
			}

			List<TermCountRow> rows = store.getTermCountResults(stime, etime);
			LOG.info("Number of records read=" + rows.size());
			if (rows != null) {
				for (TermCountRow r : rows) {
					List<LonLatPair> lls = r.getLonLats();
					if (lls != null) {
						populateResponses(response, lonLat, r, lls);
					}
				}
			}
		} catch (Exception ex) {
			LOG.error("There was an error", ex);
			if (ex instanceof AnalyticsServiceBadRequestException) {
				return Response.status(Status.BAD_REQUEST)
						.entity(ex.getMessage()).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ex.getMessage()).build();
		}

		JSONObject req = new JSONObject();
		req.put("type", type);
		req.put("bbox", bbox);
		req.put("stime", stime);
		req.put("etime", etime);
		responseParent.put("request", req);
		responseParent.put("response", response);

		return Response.ok(responseParent.toString()).build();
	}

	@SuppressWarnings("unchecked")
	private void populateResponses(JSONArray response, float[] lonLat,
			TermCountRow r, List<LonLatPair> lls) {
		for (LonLatPair ll : lls) {
			float lo = ll.getLon();
			float la = ll.getLat();
			if ((Float.compare(lo, lonLat[0]) <= 0 && Float.compare(la,
					lonLat[1]) <= 0)
					&& (Float.compare(lo, lonLat[2]) >= 0 && Float.compare(la,
							lonLat[3]) <= 0)
					&& (Float.compare(lo, lonLat[4]) <= 0 && Float.compare(la,
							lonLat[5]) >= 0)
					&& (Float.compare(lo, lonLat[6]) >= 0 && Float.compare(la,
							lonLat[7]) >= 0)) {
				JSONObject res = new JSONObject();
				res.put("hour", r.getHour());
				res.put("score", r.getScore());
				response.add(res);
				break;
			}
		}
	}

}