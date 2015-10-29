package cn.video.po;

import java.util.ArrayList;

public class Program {

	public Program() {
		// TODO Auto-generated constructor stub
	}
	private String ptype;
	private String alias = "";
	private String name = "";
	private String shootYear = "";
	private String website = "";
	private String pcUrl = "";
	private String intro = "";
	private String channelName = "";
	private ArrayList<Param> area = new ArrayList<Param>();
	private ArrayList<Param> author = new ArrayList<Param>();
	private ArrayList<Param> ctype = new ArrayList<Param>();
	private ArrayList<Param> star = new ArrayList<Param>();
	private ArrayList<Param> director = new ArrayList<Param>();
	private ArrayList<Param> writer = new ArrayList<Param>();
	private ArrayList<Poster> poster = new ArrayList<Poster>();
	private float doubanPoint;
	private float point;
	private int totalSets;
	private long playTimes;
	private String getTime;
	private boolean urlValid = true;

	private String mainId="";
	private ArrayList<ProgramSub> programSub = new ArrayList<>();

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShootYear() {
		return shootYear;
	}

	public void setShootYear(String shootYear) {
		this.shootYear = shootYear;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPcUrl() {
		return pcUrl;
	}

	public void setPcUrl(String pcUrl) {
		this.pcUrl = pcUrl;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public ArrayList<Param> getArea() {
		return area;
	}

	public void setArea(ArrayList<Param> area) {
		this.area = area;
	}

	public ArrayList<Param> getAuthor() {
		return author;
	}

	public void setAuthor(ArrayList<Param> author) {
		this.author = author;
	}

	public ArrayList<Param> getCtype() {
		return ctype;
	}

	public void setCtype(ArrayList<Param> ctype) {
		this.ctype = ctype;
	}

	public ArrayList<Param> getStar() {
		return star;
	}

	public void setStar(ArrayList<Param> star) {
		this.star = star;
	}

	public ArrayList<Param> getDirector() {
		return director;
	}

	public void setDirector(ArrayList<Param> director) {
		this.director = director;
	}

	public ArrayList<Param> getWriter() {
		return writer;
	}

	public void setWriter(ArrayList<Param> writer) {
		this.writer = writer;
	}

	public ArrayList<Poster> getPoster() {
		return poster;
	}

	public void setPoster(ArrayList<Poster> poster) {
		this.poster = poster;
	}

	public float getDoubanPoint() {
		return doubanPoint;
	}

	public void setDoubanPoint(float doubanPoint) {
		this.doubanPoint = doubanPoint;
	}

	public float getPoint() {
		return point;
	}

	public void setPoint(float point) {
		this.point = point;
	}

	public int getTotalSets() {
		return totalSets;
	}

	public void setTotalSets(int totalSets) {
		this.totalSets = totalSets;
	}

	public long getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(long playTimes) {
		this.playTimes = playTimes;
	}

	public String getGetTime() {
		return getTime;
	}

	public void setGetTime(String getTime) {
		this.getTime = getTime;
	}

	public boolean isUrlValid() {
		return urlValid;
	}

	public void setUrlValid(boolean urlValid) {
		this.urlValid = urlValid;
	}

	public ArrayList<ProgramSub> getProgramSub() {
		return programSub;
	}

	public void setProgramSub(ArrayList<ProgramSub> programSub) {
		this.programSub = programSub;
	}

	public String getMainId() {
		return mainId;
	}

	public void setMainId(String mainId) {
		this.mainId = mainId;
	}

}
