package cn.video.sohu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.Selectable;
import cn.video.InfoExtract;
import cn.video.po.Param;
import cn.video.po.Poster;
import cn.video.po.Program;
import cn.video.po.ProgramSub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service
public class SohuInfoExtract implements InfoExtract {
	private static final String SUB_VARIETY_PRE = "http://tv.sohu.com/item/VideoServlet?source=sohu&id=";

	public void extractVariety(Page page) {
		Program program = getProgram(page, "电视剧");
		page.putField("program", program);
		if (page.getRequest().getUrl().contains("videoServlet")) {
			processExtraSub(page);
		} else {
			List<String> years = page.getHtml()
					.xpath("//li[@class='v-year']/a/em/text()").all();
			String pid = page.getHtml()
					.xpath("//em[@class='vBox-warn']/@data-plid").get();
			if (years == null || years.size() == 0) {
				years = new ArrayList<String>();
				years.add(String.valueOf(Calendar.getInstance().get(
						Calendar.YEAR)));
			}
			if (years != null && StringUtils.isNotEmpty(pid)) {
				for (String year : years) {
					StringBuffer url = new StringBuffer(SUB_VARIETY_PRE);
					url.append(pid).append("&year=").append(year)
							.append("&month=0&page=1");
					Request subReq = new Request(url.toString());
					subReq.putExtra("mainId", program.getMainId());
					page.addTargetRequest(subReq);
				}
			}
		}

	}

	public void extractTv(Page page) {
		Program program = getProgram(page, "电视剧");
		page.putField("program", program);
	}

	public void extractAnim(Page page) {
		Program program = new Program();
		program.setWebsite("搜狐");
		program.setPtype("动漫");
		String poster = (String) page.getRequest().getExtra("poster");
		ArrayList<Poster> posterList = new ArrayList<>();
		posterList.add(new Poster(poster));
		program.setPoster(posterList);
		program.setMainId(page.getRequest().getUrl()
				.split("http://tv.sohu.com/")[1]);
		Selectable infoSelectable = page.getHtml()
				.css("div.blockRA.bord.clear");
		String title = infoSelectable.xpath("//h2/span/text()").get();
		program.setName(title.trim());
		String area = (String) page.getRequest().getExtra("areaOrActor");
		ArrayList<Param> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(area)) {
			list.add(new Param(area.trim()));
		}
		program.setArea(list);
		String point = infoSelectable.xpath("//div[@class='mark']/allText()")
				.get();
		if (StringUtils.isNotEmpty(point)) {
			program.setPoint(Float.parseFloat(point));
		}
		String intro = page.getHtml().xpath("//div[@class='d1']/text()").get();
		program.setIntro(intro);
		List<String> infos = infoSelectable.xpath("//div/p/allText()").all();
		for (String info : infos) {
			if (info.contains("类型")) {
				program.setCtype(getParams(info));
			} else if (info.contains("导演")) {
				program.setDirector(getParams(info));
			} else if (info.contains("评分")) {
				program.setPoint(Float.parseFloat(info.split("：")[1].split(" ")[0]));
			} else if (info.contains("年份")) {
				program.setShootYear(StringUtils.substringBetween(info, "年份：",
						" ").trim());
			}
		}
		page.putField("program", program);

		List<String> subUrls = page.getHtml().css("div.pp.similarLists")
				.xpath("//ul/li/a/@href").all();
		if (subUrls != null) {
			List<String> subNames = page.getHtml().css("div.pp.similarLists")
					.xpath("//ul/li/a/img/@alt").all();
			ArrayList<ProgramSub> subList = new ArrayList<>();
			List<String> subPosters = page.getHtml().css("div.pp.similarLists")
					.xpath("//ul/li/a/img/@src").all();
			for (int index = 0; index < subUrls.size(); index++) {
				String subTitle = subNames.get(index);
				ProgramSub sub = new ProgramSub();
				String num = StringUtils.substringBetween(subTitle, "第", "集");
				sub.setSetName(subTitle);
				if (StringUtils.isNotEmpty(num)) {
					sub.setSetNumber(num);
				} else {
					sub.setSetNumber(subTitle);
				}
				sub.setWebUrl(subUrls.get(index));
				sub.setPoster(subPosters.get(index));
				subList.add(sub);
			}
			page.putField("mainId", program.getMainId());
			page.putField("subList", subList);
		}

	}

	public void extractMovie(Page page) {
		Program program = getProgram(page, "电影");
		String playUrl = page.getHtml()
				.xpath("//a[@class='btn-playFea']/@href").get();
		if (StringUtils.isNotEmpty(playUrl)) {
			ArrayList<ProgramSub> subList = new ArrayList<>();
			ProgramSub sub = new ProgramSub();
			sub.setSetNumber("1");
			sub.setSetName(program.getName());
			sub.setWebUrl(playUrl);
			sub.setPoster(program.getPoster().get(0).getUrl());
			subList.add(sub);
			page.putField("mainId", program.getMainId());
			page.putField("subList", subList);
		}
		page.putField("program", program);
	}

	private void processExtraSub(Page page) {
		String resp = page.getRawText();
		String jsonResult = resp.substring(resp.indexOf("{"));
		JSONObject obj = JSON.parseObject(jsonResult);
		JSONArray subArr = obj.getJSONArray("videos");
		if (subArr != null) {
			int size = subArr.size();
			ArrayList<ProgramSub> subList = new ArrayList<>();
			for (int index = 0; index < size; index++) {
				JSONObject videoObj = subArr.getJSONObject(index);
				ProgramSub sub = new ProgramSub();
				sub.setPoster(videoObj.getString("pic8"));
				sub.setSetName(videoObj.getString("title"));
				sub.setSetNumber(videoObj.getString("showDate"));
				sub.setWebUrl(videoObj.getString("url"));
				sub.setSetIntro(videoObj.getString("videoDesc"));
				String guest = videoObj.getString("guest");
				if (StringUtils.isNotEmpty(guest)) {
					sub.setStars(guest.replace(";", ","));
				}
				subList.add(sub);
			}
			page.putField("subList", subList);
		}
		String mainId = (String) page.getRequest().getExtra("mainId");
		page.putField("mainId", mainId);
	}

	private ArrayList<Param> getParams(String param) {
		ArrayList<Param> list = new ArrayList<>();
		if (StringUtils.isNotEmpty(param)) {
			String[] arr = param.split("：")[1].split("/");
			for (String str : arr) {
				list.add(new Param(str.trim()));
			}
		}
		return list;
	}

	private Program getProgram(Page page, String ptype) {
		Program program = new Program();
		program.setWebsite("搜狐");
		program.setPtype(ptype);
		program.setPcUrl(page.getRequest().getUrl());
		String mainId = StringUtils.substringBetween(
				page.getRequest().getUrl(), "item/", ".html");
		program.setMainId(mainId);
		String title = page.getHtml().xpath("//h2/allText()").get().split("：")[1];
		program.setName(title);
		String poster = (String) page.getRequest().getExtra("poster");
		ArrayList<Poster> posterList = new ArrayList<>();
		posterList.add(new Poster(poster));
		program.setPoster(posterList);
		List<String> infos = page
				.getHtml()
				.xpath("//div[@class='infoR' OR @class='drama-infoR' OR @class='movie-infoR']/ul/li/allText()")
				.all();
		for (String info : infos) {
			if (info.contains("上映")) {
				program.setShootYear(info.split("：")[1]);
			} else if (info.contains("地区")) {
				program.setArea(getParams(info));
			} else if (info.contains("类型")) {
				program.setCtype(getParams(info));
			} else if (info.contains("导演")) {
				program.setDirector(getParams(info));
			} else if (info.contains("主演") || info.contains("主持人")) {
				program.setStar(getParams(info));
			} else if (info.contains("评分")) {
				program.setPoint(Float.parseFloat(info.split("：")[1].split(" ")[0]));
			}
		}
		String intro = page.getHtml().css("div.mod.plot")
				.xpath("//p/span[@class='full_intro']/text()").get();
		if (StringUtils.isEmpty(intro)) {
			intro = page.getHtml().xpath("//span[@class='full_intro']/text()")
					.get();
		}
		program.setIntro(intro.trim());
		List<String> subTitles = page.getHtml().css("div.mod.general")
				.xpath("//ul/li/div/a/@title").all();
		if (subTitles != null) {
			ArrayList<ProgramSub> subList = new ArrayList<>();
			List<String> subUrls = page.getHtml().css("div.mod.general")
					.xpath("//ul/li/div/a/@href").all();
			List<String> subPosters = page.getHtml().css("div.mod.general")
					.xpath("//ul/li/div/a/img/@src").all();
			for (int index = 0; index < subTitles.size(); index++) {
				String subTitle = subTitles.get(index);
				ProgramSub sub = new ProgramSub();
				String num = StringUtils.substringBetween(subTitle, "第", "集");
				sub.setSetName(subTitle);
				if (StringUtils.isNotEmpty(num)) {
					sub.setSetNumber(num);
				} else {
					sub.setSetNumber(subTitle);
				}
				sub.setWebUrl(subUrls.get(index));
				sub.setPoster(subPosters.get(index));
				subList.add(sub);
			}
			page.putField("subList", subList);
		}
		return program;
	}

}
