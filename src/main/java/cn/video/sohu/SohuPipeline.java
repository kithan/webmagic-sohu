package cn.video.sohu;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import cn.video.MemcachedService;
import cn.video.po.Program;
import cn.video.po.ProgramSub;

import com.alibaba.fastjson.JSON;

@Service
public class SohuPipeline implements Pipeline {

	@Resource
	MemcachedService memcachedService;

	@Override
	public synchronized void process(ResultItems resultItems, Task task) {
		Program program = resultItems.get("program");
		if (program != null) {
			memcachedService.setCached(SohuCrawler.detailCacheKeyStart
					+ program.getMainId(), JSON.toJSONString(program));
		}
		List<ProgramSub> subList = resultItems.get("subList");
		if (subList == null) {
			subList = new ArrayList<ProgramSub>();
		}
		String id = resultItems.get("mainId");
		memcachedService.setCached(SohuCrawler.subCacheKeyStart + id,
				JSON.toJSONString(subList));

	}
}
