package cn.video.sohu;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
public class SohuPageProcessor implements PageProcessor {
	@Resource
	SohuInfoExtract sohuInfoExtract;
	private Site site = Site
			.me()
			.setRetryTimes(3)
			.setSleepTime(1000)
			.setUserAgent(
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");

	@Override
	public void process(Page page) {
		if (page.getRequest().getUrl().startsWith("http://so.tv.sohu.com")) {
			page.setSkip(true);
			List<String> pageList = page.getHtml().css("div.ssPages.area")
					.css("a", "href").all();
			page.addTargetRequests(pageList);
			List<String> playUrls = page.getHtml()
					.xpath("//div[@class='st-pic']/a[@target='_blank']/@href")
					.all();
			List<String> posters = page
					.getHtml()
					.xpath("//div[@class='st-pic']/a[@target='_blank']/img/@src")
					.all();
			String ptype = page.getHtml().xpath("//li[@class='son']/a/text()")
					.get().trim();
			List<String> areaOrActors = page.getHtml()
					.xpath("//p[@class='actor']/allText()").all();
			List<Request> requests = new ArrayList<Request>();
			for (int index = 0; index < playUrls.size(); index++) {
				Request r = new Request(playUrls.get(index));
				r.putExtra("ptype", ptype);
				r.putExtra("poster", posters.get(index));
				try{
					r.putExtra("areaOrActor", areaOrActors.get(index).split("：")[1]);
				}catch (Exception e) {
					// TODO: handle exception
				}
				requests.add(r);
				page.addTargetRequest(r);
			}
		} else {
			String ptype = (String) page.getRequest().getExtra("ptype");
			if (ptype.equals("电影")) {
				sohuInfoExtract.extractMovie(page);
			} else if (ptype.equals("电视剧")) {
				sohuInfoExtract.extractTv(page);
			} else if (ptype.equals("综艺")) {
				sohuInfoExtract.extractVariety(page);
			} else if (ptype.equals("动漫")) {
				sohuInfoExtract.extractAnim(page);
			}
		}

	}

	@Override
	public Site getSite() {
		return site;
	}

}
