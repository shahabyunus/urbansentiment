package org.urbanstmt.parser.twitter;

//import org.json.simple.JSONObject;
import org.urbanstmt.exception.TweetStatusParseException;
import org.urbanstmt.model.twitter.CoordinatesVO;
import org.urbanstmt.model.twitter.PlaceVO;
import org.urbanstmt.model.twitter.TweetVO;

import twitter4j.GeoLocation;
import twitter4j.Logger;
import twitter4j.Place;
import twitter4j.Status;

public class JsonTweetStatusParser implements TweetStatusParser {

	private static final Logger LOG = Logger
			.getLogger(JsonTweetStatusParser.class);

	@Override
	public TweetVO parse(Status status) throws TweetStatusParseException {
		TweetVO tweet = new TweetVO();

		tweet.setId(status.getId());
		tweet.setIn_r_status_id(status.getInReplyToStatusId());
		tweet.setIn_r_user_id(status.getInReplyToUserId());
		tweet.setLang(status.getLang());
		tweet.setCreated_at(status.getCreatedAt().getTime());
		tweet.setText(status.getText());
		tweet.setSource(status.getSource());
		tweet.setFavorited(status.isFavorited());
		tweet.setFavorite_count(status.getFavoriteCount());
		tweet.setRetweeted(status.isRetweeted());
		tweet.setRetweet_count(status.getRetweetCount());
		tweet.setSensitive(status.isPossiblySensitive());
		tweet.setTxn_time(System.currentTimeMillis());

		GeoLocation loc = status.getGeoLocation();
		if (loc != null) {
			CoordinatesVO coord = new CoordinatesVO();
			coord.setLat(new Double(loc.getLatitude()).floatValue());
			coord.setLon(new Double(loc.getLongitude()).floatValue());
			coord.setC_type("Point");
			tweet.setCoords(coord);
		}

		Place p = status.getPlace();
		if (p != null) {
			PlaceVO pvo = new PlaceVO();

			pvo.setPl_st_add(p.getStreetAddress());
			pvo.setPl_country(p.getCountry());
			pvo.setPl_country_code(p.getCountryCode());
			pvo.setPl_name(p.getName());
			pvo.setPl_full_name(p.getFullName());
			pvo.setPl_type(p.getPlaceType());
			pvo.setPl_url(p.getURL());

			GeoLocation[][] geoLocs = p.getBoundingBoxCoordinates();
			if (geoLocs != null) {

				pvo.setPl_bb_type(p.getBoundingBoxType());
				if (geoLocs.length > 0) {
					pvo.setPl_bb_lon_1(new Double(geoLocs[0][0].getLongitude())
							.floatValue());
					pvo.setPl_bb_lat_1(new Double(geoLocs[0][0].getLatitude())
							.floatValue());
				}

				if (geoLocs.length > 1) {
					pvo.setPl_bb_lon_1(new Double(geoLocs[1][0].getLongitude())
							.floatValue());
					pvo.setPl_bb_lat_1(new Double(geoLocs[1][0].getLatitude())
							.floatValue());
				}

				if (geoLocs.length > 2) {
					pvo.setPl_bb_lon_1(new Double(geoLocs[2][0].getLongitude())
							.floatValue());
					pvo.setPl_bb_lat_1(new Double(geoLocs[2][0].getLatitude())
							.floatValue());
				}

				if (geoLocs.length > 3) {
					pvo.setPl_bb_lon_1(new Double(geoLocs[3][0].getLongitude())
							.floatValue());
					pvo.setPl_bb_lat_1(new Double(geoLocs[3][0].getLatitude())
							.floatValue());
				}
			}

			tweet.setPlace(pvo);
		}

		return tweet;
	}

}
