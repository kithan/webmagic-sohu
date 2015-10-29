package cn.video.po;


public class ProgramSub {

	public ProgramSub() {
		// TODO Auto-generated constructor stub
	}

	private String setName;
	private String setNumber;
	private String stars = "";
	private String setIntro = "";
	private String webUrl;
	private int playLength;
	private boolean urlValid = true;;
	private String poster = "";

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public String getSetNumber() {
		return setNumber;
	}

	public void setSetNumber(String setNumber) {
		this.setNumber = setNumber;
	}

	public String getStars() {
		return stars;
	}

	public void setStars(String stars) {
		this.stars = stars;
	}

	public String getSetIntro() {
		return setIntro;
	}

	public void setSetIntro(String setIntro) {
		this.setIntro = setIntro;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public int getPlayLength() {
		return playLength;
	}

	public void setPlayLength(int playLength) {
		this.playLength = playLength;
	}

	public boolean isUrlValid() {
		return urlValid;
	}

	public void setUrlValid(boolean urlValid) {
		this.urlValid = urlValid;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

}
