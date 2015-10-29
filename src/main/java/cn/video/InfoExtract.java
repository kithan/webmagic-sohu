package cn.video;

import us.codecraft.webmagic.Page;

public interface InfoExtract {

	public void extractMovie(Page page);
	public void extractTv(Page page);
	public void extractVariety(Page page);
	public void extractAnim(Page page);
}
