package cn.video.sohu;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Spider.Status;
import cn.video.MemcachedService;

/**
 * 
 * 
 * @author hpb
 * 
 */
@Component
public class SohuCrawler {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private SohuPipeline sohuPipeline;
	@Resource
	SohuPageProcessor sohuPageProcessor;
	@Resource
	MemcachedService memcachedService;

	public static final String detailCacheKeyStart = "sohu_all_detail_";
	public static final String subCacheKeyStart = "sohu_all_sub_";
	public static final String programSavePath = "D:/sohu_all.txt";

	public void crawl() throws FileNotFoundException,
			UnsupportedEncodingException {
		final Spider spider = Spider.create(sohuPageProcessor)
				.setExitWhenComplete(true)
				// sohu movie
				// .addUrl("http://so.tv.sohu.com/list_p1100_p2_p3_p4_p5_p6_p7_p8_p9_p10_p11_p125_p13.html")

				// sohu real
				.addUrl("http://so.tv.sohu.com/list_p1107_p2_p3_p4_p5_p6_p7_p8_p9_p101_p11_p12_p13.html")

				// sohu cartoon
				.addUrl("http://so.tv.sohu.com/list_p1115_p2_p3_p4_p5_p6_p7_p8_p9_p101_p11_p12_p13.html")

				// sohu zongyi
				// .addUrl("http://so.tv.sohu.com/list_p1106_p2_p3_p4_p5_p6_p7_p8_p91_p10_p11_p12_p13.html")

				// sohu tv
				// .addUrl("http://so.tv.sohu.com/list_p1101_p20_p3_p40_p5_p6_p73_p80_p9_p101_p11_p12_p13.html")
				.thread(10).addPipeline(sohuPipeline);
		spider.start();

		new Thread() {
			public void run() {
				while (spider.getStatus() == Status.Running) {
					try {
						sleep(5000);
						logger.info("waiting spider completed:thread count="
								+ spider.getThreadAlive());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				memcachedService.combineCacheInfo(subCacheKeyStart,
						detailCacheKeyStart, programSavePath);
			};
		}.start();
	}

	public static void main(String[] args) throws FileNotFoundException,
			UnsupportedEncodingException {

		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"classpath:/spring/applicationContext*.xml");
		final SohuCrawler jobCrawler = applicationContext
				.getBean(SohuCrawler.class);
		jobCrawler.crawl();

	}
}
