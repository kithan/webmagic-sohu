package cn.video;

/*
 * 文 件 名:  MemcachedService.java
 * 描    述:  <描述>
 */
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.video.po.Program;
import cn.video.po.ProgramSub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.danga.MemCached.MemCachedClient;

public class MemcachedService {

	private static final Logger log = LoggerFactory
			.getLogger(MemcachedService.class);

	MemCachedClient memCachedClient;

	public MemCachedClient getMemCachedClient() {
		return this.memCachedClient;
	}

	public void setMemCachedClient(MemCachedClient memCachedClient) {
		this.memCachedClient = memCachedClient;
		if (this.memCachedClient != null) {
			this.memCachedClient.setPrimitiveAsString(true);
		}
	}

	public void setCached(String key, Object value) {
		memCachedClient.set(key, value);
	}

	public Object getCached(String key) {
		return this.memCachedClient.get(key);
	}

	public void clearCached() {
		this.memCachedClient.flushAll();
	}

	public void deleteCached(String key) {
		this.memCachedClient.delete(key);
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	public Set filterCachedKey(String keyStart) {
		Set<String> keys = new HashSet<String>();
		try {
			int limit = 0;
			Map<String, Integer> dumps = new HashMap<String, Integer>();

			Map slabs = memCachedClient.statsItems();
			if (slabs != null && slabs.keySet() != null) {
				Iterator itemsItr = slabs.keySet().iterator();
				while (itemsItr.hasNext()) {
					String server = itemsItr.next().toString();
					Map itemNames = (Map) slabs.get(server);
					Iterator itemNameItr = itemNames.keySet().iterator();
					while (itemNameItr.hasNext()) {
						String itemName = itemNameItr.next().toString();
						// itemAtt[0]=itemname
						// itemAtt[1]=number
						// itemAtt[2]=field
						String[] itemAtt = itemName.split(":");

						if (itemAtt[2].startsWith("number"))
							dumps.put(itemAtt[1], Integer.parseInt(itemAtt[1]));
					}
				}
				if (!dumps.values().isEmpty()) {
					Iterator<Integer> dumpIter = dumps.values().iterator();
					while (dumpIter.hasNext()) {
						int dump = dumpIter.next();

						Map cacheDump = memCachedClient.statsCacheDump(dump,
								limit);
						Iterator entryIter = cacheDump.values().iterator();
						while (entryIter.hasNext()) {
							Map items = (Map) entryIter.next();
							Iterator ks = items.keySet().iterator();
							while (ks.hasNext()) {
								String k = URLDecoder
										.decode((String) ks.next());
								if (k.startsWith(keyStart)
										&& this.memCachedClient.keyExists(k)) {
									keys.add(k);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.debug(e.toString());
		}
		return keys;
	}

	public boolean add(String key, Object value) {
		return this.memCachedClient.add(key, value);
	}

	public boolean replace(String key, Object value) {
		return this.memCachedClient.replace(key, value);
	}

	public boolean replace(String key, Object value, long time) {
		return this.memCachedClient.replace(key, value, new Date(time));
	}

	public Map<String, Object> getMulti(String[] key) {
		return this.memCachedClient.getMulti(key);
	}

	public boolean storeCounter(String key, long count) {
		return this.memCachedClient.storeCounter(key, count);
	}

	public long getCounter(String key) {
		return this.memCachedClient.getCounter(key);
	}

	public void deleteCachedMulti(Object[] keys) {
		for (Object key : keys) {
			this.deleteCached(key.toString());
		}
	}

	@SuppressWarnings("rawtypes")
	public void deleteCachedBykeyStart(String keyStart) {
		Set keySet = filterCachedKey(keyStart);
		this.deleteCachedMulti(keySet.toArray());
	}

	public long addOrDecr(String key, long decr) {
		return this.memCachedClient.addOrDecr(key, decr);
	}

	public long addOrIncr(String key, long incr) {
		return this.memCachedClient.addOrIncr(key, incr);
	}

	public long decr(String key, long decr) {
		return this.memCachedClient.decr(key, decr);
	}

	public long incr(String key, long incr) {
		return this.memCachedClient.incr(key, incr);
	}

	/**
	 * 合并缓存节目信息
	 * 
	 * @param subkeyStart
	 *            格式:sohu_variety_sub_1_*
	 * @param detailKeyStart
	 *            格式:sohu_variety_detail_*
	 * @param savePath
	 */
	public void combineCacheInfo(String subkeyStart, String detailKeyStart,
			String savePath) {
		log.info("spider completed start to write cache program");
		@SuppressWarnings("unchecked")
		Set<String> keys = filterCachedKey(subkeyStart);
		Map<String, ArrayList<ProgramSub>> subListMap = new HashMap<String, ArrayList<ProgramSub>>();
		for (String key : keys) {
			String albumId = key.split("_")[3];
			String subListJson = (String) getCached(key);
			ArrayList<ProgramSub> subList = (ArrayList<ProgramSub>) JSON
					.parseArray(subListJson, ProgramSub.class);
			if (subListMap.containsKey(albumId)) {
				ArrayList<ProgramSub> list = subListMap.get(albumId);
				list.addAll(subList);
				subListMap.put(albumId, list);
			} else {
				subListMap.put(albumId, subList);
			}
		}
		log.info("need save program size:" + subListMap.size());
		if (subListMap.size() > 0) {
			PrintWriter pt = null;
			try {
				pt = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(savePath, true), "UTF-8"));
				int index = 0;
				for (String key : subListMap.keySet()) {
					String programJson = (String) getCached(detailKeyStart
							+ key);
					Program program = JSON.parseObject(programJson,
							Program.class);
					ArrayList<ProgramSub> subs = subListMap.get(key);
					program.setProgramSub(subs);
					program.setTotalSets(subs.size());
					JSONObject obj = new JSONObject();
					obj.put("program", JSON.toJSON(program));
					pt.println(obj.toString());
					if (index == 10) {
						pt.flush();
						index = 0;
					}
				}
				pt.flush();
				log.info("write cache program completed and  clear cache");
				deleteCachedBykeyStart(subkeyStart);
				deleteCachedBykeyStart(detailKeyStart);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				pt.close();
			}
		}
	}
}
