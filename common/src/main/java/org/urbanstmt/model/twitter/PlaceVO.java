package org.urbanstmt.model.twitter;


public  class PlaceVO
{
	protected String pl_country;
	protected String pl_country_code;
	protected String pl_id;
	protected String pl_full_name;
	protected String pl_name;
	protected String pl_type;
	protected String pl_url;
	protected String pl_st_add;
	
	protected float pl_bb_lon_1;
	protected float pl_bb_lat_1;
	
	protected float pl_bb_lon_2;
	protected float pl_bb_lat_2;
	
	protected float pl_bb_lon_3;
	protected float pl_bb_lat_3;

	protected float pl_bb_lon_4;
	protected float pl_bb_lat_4;
	
	protected String pl_bb_type;
	
	

	public String getPl_bb_type() {
		return pl_bb_type;
	}
	public void setPl_bb_type(String pl_bb_type) {
		this.pl_bb_type = pl_bb_type;
	}
	public String getPl_st_add() {
		return pl_st_add;
	}
	public void setPl_st_add(String pl_st_add) {
		this.pl_st_add = pl_st_add;
	}
	public float getPl_bb_lon_1() {
		return pl_bb_lon_1;
	}
	public void setPl_bb_lon_1(float pl_bb_lon_1) {
		this.pl_bb_lon_1 = pl_bb_lon_1;
	}
	public float getPl_bb_lat_1() {
		return pl_bb_lat_1;
	}
	public void setPl_bb_lat_1(float pl_bb_lat_1) {
		this.pl_bb_lat_1 = pl_bb_lat_1;
	}
	public float getPl_bb_lon_2() {
		return pl_bb_lon_2;
	}
	public void setPl_bb_lon_2(float pl_bb_lon_2) {
		this.pl_bb_lon_2 = pl_bb_lon_2;
	}
	public float getPl_bb_lat_2() {
		return pl_bb_lat_2;
	}
	public void setPl_bb_lat_2(float pl_bb_lat_2) {
		this.pl_bb_lat_2 = pl_bb_lat_2;
	}
	public float getPl_bb_lon_3() {
		return pl_bb_lon_3;
	}
	public void setPl_bb_lon_3(float pl_bb_lon_3) {
		this.pl_bb_lon_3 = pl_bb_lon_3;
	}
	public float getPl_bb_lat_3() {
		return pl_bb_lat_3;
	}
	public void setPl_bb_lat_3(float pl_bb_lat_3) {
		this.pl_bb_lat_3 = pl_bb_lat_3;
	}
	public float getPl_bb_lon_4() {
		return pl_bb_lon_4;
	}
	public void setPl_bb_lon_4(float pl_bb_lon_4) {
		this.pl_bb_lon_4 = pl_bb_lon_4;
	}
	public float getPl_bb_lat_4() {
		return pl_bb_lat_4;
	}
	public void setPl_bb_lat_4(float pl_bb_lat_4) {
		this.pl_bb_lat_4 = pl_bb_lat_4;
	}
	public String getPl_country() {
		return pl_country;
	}
	public void setPl_country(String country) {
		this.pl_country = country;
	}
	public String getPl_country_code() {
		return pl_country_code;
	}
	public void setPl_country_code(String country_code) {
		this.pl_country_code = country_code;
	}
	public String getPl_id() {
		return pl_id;
	}
	public void setPl_id(String pl_id) {
		this.pl_id = pl_id;
	}
	public String getPl_full_name() {
		return pl_full_name;
	}
	public void setPl_full_name(String full_name) {
		this.pl_full_name = full_name;
	}
	public String getPl_name() {
		return pl_name;
	}
	public void setPl_name(String pl_name) {
		this.pl_name = pl_name;
	}
	public String getPl_type() {
		return pl_type;
	}
	public void setPl_type(String pl_type) {
		this.pl_type = pl_type;
	}
	public String getPl_url() {
		return pl_url;
	}
	public void setPl_url(String pl_url) {
		this.pl_url = pl_url;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(pl_bb_lat_1);
		result = prime * result + Float.floatToIntBits(pl_bb_lat_2);
		result = prime * result + Float.floatToIntBits(pl_bb_lat_3);
		result = prime * result + Float.floatToIntBits(pl_bb_lat_4);
		result = prime * result + Float.floatToIntBits(pl_bb_lon_1);
		result = prime * result + Float.floatToIntBits(pl_bb_lon_2);
		result = prime * result + Float.floatToIntBits(pl_bb_lon_3);
		result = prime * result + Float.floatToIntBits(pl_bb_lon_4);
		result = prime * result
				+ ((pl_bb_type == null) ? 0 : pl_bb_type.hashCode());
		result = prime * result
				+ ((pl_country == null) ? 0 : pl_country.hashCode());
		result = prime * result
				+ ((pl_country_code == null) ? 0 : pl_country_code.hashCode());
		result = prime * result
				+ ((pl_full_name == null) ? 0 : pl_full_name.hashCode());
		result = prime * result + ((pl_id == null) ? 0 : pl_id.hashCode());
		result = prime * result + ((pl_name == null) ? 0 : pl_name.hashCode());
		result = prime * result
				+ ((pl_st_add == null) ? 0 : pl_st_add.hashCode());
		result = prime * result + ((pl_type == null) ? 0 : pl_type.hashCode());
		result = prime * result + ((pl_url == null) ? 0 : pl_url.hashCode());
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
		PlaceVO other = (PlaceVO) obj;
		if (Float.floatToIntBits(pl_bb_lat_1) != Float
				.floatToIntBits(other.pl_bb_lat_1))
			return false;
		if (Float.floatToIntBits(pl_bb_lat_2) != Float
				.floatToIntBits(other.pl_bb_lat_2))
			return false;
		if (Float.floatToIntBits(pl_bb_lat_3) != Float
				.floatToIntBits(other.pl_bb_lat_3))
			return false;
		if (Float.floatToIntBits(pl_bb_lat_4) != Float
				.floatToIntBits(other.pl_bb_lat_4))
			return false;
		if (Float.floatToIntBits(pl_bb_lon_1) != Float
				.floatToIntBits(other.pl_bb_lon_1))
			return false;
		if (Float.floatToIntBits(pl_bb_lon_2) != Float
				.floatToIntBits(other.pl_bb_lon_2))
			return false;
		if (Float.floatToIntBits(pl_bb_lon_3) != Float
				.floatToIntBits(other.pl_bb_lon_3))
			return false;
		if (Float.floatToIntBits(pl_bb_lon_4) != Float
				.floatToIntBits(other.pl_bb_lon_4))
			return false;
		if (pl_bb_type == null) {
			if (other.pl_bb_type != null)
				return false;
		} else if (!pl_bb_type.equals(other.pl_bb_type))
			return false;
		if (pl_country == null) {
			if (other.pl_country != null)
				return false;
		} else if (!pl_country.equals(other.pl_country))
			return false;
		if (pl_country_code == null) {
			if (other.pl_country_code != null)
				return false;
		} else if (!pl_country_code.equals(other.pl_country_code))
			return false;
		if (pl_full_name == null) {
			if (other.pl_full_name != null)
				return false;
		} else if (!pl_full_name.equals(other.pl_full_name))
			return false;
		if (pl_id == null) {
			if (other.pl_id != null)
				return false;
		} else if (!pl_id.equals(other.pl_id))
			return false;
		if (pl_name == null) {
			if (other.pl_name != null)
				return false;
		} else if (!pl_name.equals(other.pl_name))
			return false;
		if (pl_st_add == null) {
			if (other.pl_st_add != null)
				return false;
		} else if (!pl_st_add.equals(other.pl_st_add))
			return false;
		if (pl_type == null) {
			if (other.pl_type != null)
				return false;
		} else if (!pl_type.equals(other.pl_type))
			return false;
		if (pl_url == null) {
			if (other.pl_url != null)
				return false;
		} else if (!pl_url.equals(other.pl_url))
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PlaceVO [pl_country=").append(pl_country)
				.append(", pl_country_code=").append(pl_country_code)
				.append(", pl_id=").append(pl_id).append(", pl_full_name=")
				.append(pl_full_name).append(", pl_name=").append(pl_name)
				.append(", pl_type=").append(pl_type).append(", pl_url=")
				.append(pl_url).append(", pl_st_add=").append(pl_st_add)
				.append(", pl_bb_lon_1=").append(pl_bb_lon_1)
				.append(", pl_bb_lat_1=").append(pl_bb_lat_1)
				.append(", pl_bb_lon_2=").append(pl_bb_lon_2)
				.append(", pl_bb_lat_2=").append(pl_bb_lat_2)
				.append(", pl_bb_lon_3=").append(pl_bb_lon_3)
				.append(", pl_bb_lat_3=").append(pl_bb_lat_3)
				.append(", pl_bb_lon_4=").append(pl_bb_lon_4)
				.append(", pl_bb_lat_4=").append(pl_bb_lat_4)
				.append(", pl_bb_type=").append(pl_bb_type).append("]");
		return builder.toString();
	}
	
	
	
}

