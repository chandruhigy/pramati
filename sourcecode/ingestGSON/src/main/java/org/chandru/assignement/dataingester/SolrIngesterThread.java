package org.chandru.assignement.dataingester;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.chandru.assignement.datacache.DataQueueStore;
import org.chandru.assignement.datahandler.HotelsTO;

/**
 * Callable thread to perform ingestion of data to solr from cache 
 * @author S.chandru
 * @throws Exception
 */

public class SolrIngesterThread implements Callable<Boolean> {
	
	private static final Log log = LogFactory.getLog(SolrIngesterThread.class);

	private SolrClient solrClient = null;
	
	private DataQueueStore dataQueueStore = null;
	
	private String baseurl = null;
	
	public SolrIngesterThread(String baseurl) {
		this.baseurl = baseurl;
	}
	
	public Boolean call() throws Exception {
		// TODO Auto-generated method stub
		SolrClientHandler solrClientHandler = SolrClientHandler.getInstance();
		solrClient = solrClientHandler.createSolrClient(baseurl);
		dataQueueStore = DataQueueStore.getInstance();
		ingestDataFromJson();
		return true;
	}
	
	/**
	 * Method called from a Callable thread to perform ingestion of data to solr from cache 
	 * @author S.chandru
	 * @throws Exception
	 */

	private void ingestDataFromJson() throws Exception {
		// TODO Auto-generated method stub
		log.info("Entering into ingestDataFromJson");
		int documentCount=1;
		int count=0;
		HotelsTO hotelTo = null;
		while(true) {
			try {
				hotelTo = dataQueueStore.get();
				if(hotelTo!=null) {
					log.debug("Got hotelTO from cache :"+hotelTo.toString());
					SolrInputDocument solrInputDocument = new SolrInputDocument();
					solrInputDocument.addField("hotelname", hotelTo.getHotelname());
					log.info("hotelname is :"+hotelTo.getHotelname());
					solrInputDocument.addField("hotelid", documentCount);
					solrInputDocument.addField("address", hotelTo.getAddress());
					solrInputDocument.addField("emailid", hotelTo.getEmailid());
					solrInputDocument.addField("fax", hotelTo.getFax());
					solrInputDocument.addField("phone", hotelTo.getPhone());
					solrInputDocument.addField("website", hotelTo.getWebsite());
					solrInputDocument.addField("state", hotelTo.getState());
					solrInputDocument.addField("rooms", Integer.parseInt(hotelTo.getRooms()== null?"0":hotelTo.getRooms()));
					solrInputDocument.addField("type", hotelTo.getType());
					executeWithRety(solrClient,solrInputDocument);
					documentCount++;
					count=0;
				}else {
					if(count==5) {
						break;
					}
					count++;
					Thread.sleep(1000);
				}
			}catch(Exception e) {
				log.error("Error in indexing document "+ hotelTo.getHotelname());
			}
		}
		
		log.info("Count is :"+count);
		log.info("documentCount  is :"+documentCount);
		solrClient.commit();
	}
	
	private static void executeWithRety(SolrClient solrClient, SolrInputDocument solrInputDocument) throws Exception {
		boolean isError = false;
		int count = 0;
		Exception exp = null;
		do{
			try{
				count++;
				solrClient.add(solrInputDocument);
				break;
			}catch(Exception e){
				exp = e;
				isError = true;
				Thread.sleep(1000*count);
			}
			if(isError && count > 10){
				throw new Exception(exp);
			}
		}while(isError);
	}
	
}
