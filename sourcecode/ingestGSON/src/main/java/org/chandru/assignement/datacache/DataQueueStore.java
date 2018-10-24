package org.chandru.assignement.datacache;

import java.util.concurrent.ArrayBlockingQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chandru.assignement.datahandler.HotelsTO;

/**
 * Blocking queue used as a cache
 * @author S.chandru
 */

public class DataQueueStore {
	
	private static final Log log = LogFactory.getLog(DataQueueStore.class);

	private static int capacity = Integer.parseInt(System.getProperty("cacheSize", "100"));

	private static BlockingQueue<HotelsTO> blockingqueue = new ArrayBlockingQueue<HotelsTO>(capacity);

	private static DataQueueStore DataQueueStore = null;

	private DataQueueStore() {
		
	}

	public static DataQueueStore getInstance() {
		if(DataQueueStore == null) {
			DataQueueStore = new DataQueueStore();
		}
		return DataQueueStore;
	}
	
	public void put(HotelsTO hotelTO) throws Exception {
		if(hotelTO!=null) {
			blockingqueue.offer(hotelTO, 10, TimeUnit.SECONDS);
			System.out.println("Queue size now : "+blockingqueue.size());
		}
	}
	
	
	public HotelsTO get() throws Exception{
		System.out.println("Waiting to consume");
		HotelsTO hotelTO = blockingqueue.poll(10, TimeUnit.SECONDS);
		System.out.println("Queue size now : "+blockingqueue.size());
		return hotelTO;
	}
	
	public int getSize() {
		return blockingqueue.size();
	}

}
