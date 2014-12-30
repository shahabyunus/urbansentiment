package org.urbanstmt.model.twitter;

public class CoordinatesVO
{
	protected float lon;
	protected float lat;
	protected String c_type;
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
	public String getC_type() {
		return c_type;
	}
	public void setC_type(String c_type) {
		this.c_type = c_type;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((c_type == null) ? 0 : c_type.hashCode());
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
		CoordinatesVO other = (CoordinatesVO) obj;
		if (c_type == null) {
			if (other.c_type != null)
				return false;
		} else if (!c_type.equals(other.c_type))
			return false;
		if (Float.floatToIntBits(lat) != Float.floatToIntBits(other.lat))
			return false;
		if (Float.floatToIntBits(lon) != Float.floatToIntBits(other.lon))
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CoordinatesVO [lon=").append(lon).append(", lat=")
				.append(lat).append(", c_type=").append(c_type).append("]");
		return builder.toString();
	}
	
	
}
