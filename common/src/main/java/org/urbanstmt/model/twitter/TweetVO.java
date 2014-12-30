package org.urbanstmt.model.twitter;

public class TweetVO {

	protected long id;
	protected long created_at;
	protected String text;
	protected String source; 
	protected long in_r_status_id;
	protected long in_r_user_id; 
	protected boolean favorited;
	protected boolean retweeted;
	protected boolean retweet;
	protected int favorite_count; 
	protected PlaceVO place; 
	protected int retweet_count;
	protected boolean sensitive; 
	protected String lang; 
	protected CoordinatesVO coords;
	protected long txn_time;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCreated_at() {
		return created_at;
	}
	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public long getIn_r_status_id() {
		return in_r_status_id;
	}
	public void setIn_r_status_id(long in_r_status_id) {
		this.in_r_status_id = in_r_status_id;
	}
	public long getIn_r_user_id() {
		return in_r_user_id;
	}
	public void setIn_r_user_id(long in_r_user_id) {
		this.in_r_user_id = in_r_user_id;
	}
	public boolean getFavorited() {
		return favorited;
	}
	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}
	public boolean getRetweeted() {
		return retweeted;
	}
	public void setRetweeted(boolean retweeted) {
		this.retweeted = retweeted;
	}
	public int getFavorite_count() {
		return favorite_count;
	}
	public void setFavorite_count(int favorite_count) {
		this.favorite_count = favorite_count;
	}
	public PlaceVO getPlace() {
		return place;
	}
	public void setPlace(PlaceVO place) {
		this.place = place;
	}
	public int getRetweet_count() {
		return retweet_count;
	}
	public void setRetweet_count(int retweet_count) {
		this.retweet_count = retweet_count;
	}
	public boolean getSensitive() {
		return sensitive;
	}
	public void setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public CoordinatesVO getCoords() {
		return coords;
	}
	public void setCoords(CoordinatesVO coords) {
		this.coords = coords;
	}
	public boolean getRetweet() {
		return retweet;
	}
	public void setRetweet(boolean retweet) {
		this.retweet = retweet;
	}
	
	public long getTxn_time() {
		return txn_time;
	}
	public void setTxn_time(long txn_time) {
		this.txn_time = txn_time;
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TweetVO [id=").append(id).append(", created_at=")
				.append(created_at).append(", text=").append(text)
				.append(", source=").append(source).append(", in_r_status_id=")
				.append(in_r_status_id).append(", in_r_user_id=")
				.append(in_r_user_id).append(", favorited=").append(favorited)
				.append(", retweeted=").append(retweeted).append(", retweet=")
				.append(retweet).append(", favorite_count=")
				.append(favorite_count).append(", place=").append(place)
				.append(", retweet_count=").append(retweet_count)
				.append(", sensitive=").append(sensitive).append(", lang=")
				.append(lang).append(", coords=").append(coords)
				.append(", txn_time=").append(txn_time).append("]");
		return builder.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coords == null) ? 0 : coords.hashCode());
		result = prime * result + (int) (created_at ^ (created_at >>> 32));
		result = prime * result + favorite_count;
		result = prime * result + (favorited ? 1231 : 1237);
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ (int) (in_r_status_id ^ (in_r_status_id >>> 32));
		result = prime * result + (int) (in_r_user_id ^ (in_r_user_id >>> 32));
		result = prime * result + ((lang == null) ? 0 : lang.hashCode());
		result = prime * result + ((place == null) ? 0 : place.hashCode());
		result = prime * result + (retweet ? 1231 : 1237);
		result = prime * result + retweet_count;
		result = prime * result + (retweeted ? 1231 : 1237);
		result = prime * result + (sensitive ? 1231 : 1237);
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + (int) (txn_time ^ (txn_time >>> 32));
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
		TweetVO other = (TweetVO) obj;
		if (coords == null) {
			if (other.coords != null)
				return false;
		} else if (!coords.equals(other.coords))
			return false;
		if (created_at != other.created_at)
			return false;
		if (favorite_count != other.favorite_count)
			return false;
		if (favorited != other.favorited)
			return false;
		if (id != other.id)
			return false;
		if (in_r_status_id != other.in_r_status_id)
			return false;
		if (in_r_user_id != other.in_r_user_id)
			return false;
		if (lang == null) {
			if (other.lang != null)
				return false;
		} else if (!lang.equals(other.lang))
			return false;
		if (place == null) {
			if (other.place != null)
				return false;
		} else if (!place.equals(other.place))
			return false;
		if (retweet != other.retweet)
			return false;
		if (retweet_count != other.retweet_count)
			return false;
		if (retweeted != other.retweeted)
			return false;
		if (sensitive != other.sensitive)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (txn_time != other.txn_time)
			return false;
		return true;
	}
   
	
	
}
