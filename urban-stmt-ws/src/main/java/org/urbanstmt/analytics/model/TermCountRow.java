package org.urbanstmt.analytics.model;

import java.util.List;

public class TermCountRow {
	private float score;
	private List<LonLatPair> lonLats;
	private String hour;
	private String term;
	
	
	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public List<LonLatPair> getLonLats() {
		return lonLats;
	}

	public void setLonLats(List<LonLatPair> lonLats) {
		this.lonLats = lonLats;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TermCountRow [score=").append(score)
				.append(", lonLats=").append(lonLats).append(", theHour=")
				.append(hour).append(", word=").append(term).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lonLats == null) ? 0 : lonLats.hashCode());
		result = prime * result + Float.floatToIntBits(score);
		result = prime * result + ((hour == null) ? 0 : hour.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TermCountRow other = (TermCountRow) obj;
		if (lonLats == null) {
			if (other.lonLats != null)
				return false;
		} else if (!lonLats.equals(other.lonLats))
			return false;
		if (Float.floatToIntBits(score) != Float.floatToIntBits(other.score))
			return false;
		if (hour == null) {
			if (other.hour != null)
				return false;
		} else if (!hour.equals(other.hour))
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}



	public static class LonLatPair	{
		private float lon;
		private float lat;
		
		public LonLatPair(float lon, float lat) {
			super();
			this.lon = lon;
			this.lat = lat;
		}
		public float getLon() {
			return lon;
		}
		public void setLon(float lon) {
			this.lon = lon;
		}
		public float getLat() {
			return lat;
		}
		public void setLat(float lat) {
			this.lat = lat;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Float.floatToIntBits(lat);
			result = prime * result + Float.floatToIntBits(lon);
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LonLatPair other = (LonLatPair) obj;
			if (Float.floatToIntBits(lat) != Float.floatToIntBits(other.lat))
				return false;
			if (Float.floatToIntBits(lon) != Float.floatToIntBits(other.lon))
				return false;
			return true;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("LonLatPair [lon=").append(lon).append(", lat=")
					.append(lat).append("]");
			return builder.toString();
		}
		
		
	}
}
