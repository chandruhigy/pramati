package org.chandru.assignement.dataingester;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.params.ModifiableSolrParams;

/**
 * SolrClient creator class
 * @author S.chandru
 */

public class SolrClientHandler {
	
	private static final Log log = LogFactory.getLog(SolrClientHandler.class);
	
	private static SolrClientHandler clientHandler = null;
	
	private static HttpSolrClient solrClient = null;

	private SolrClientHandler() {
		
	}
	
	public static SolrClientHandler getInstance() {
		if(clientHandler == null) {
			clientHandler = new SolrClientHandler();
		}
		return clientHandler;
		
	}
	
	public SolrClient createSolrClient(String baseurl) throws Exception {
		log.info("Creating solrclinet with baseurl :"+baseurl);
		if(solrClient == null) {
			ModifiableSolrParams config = new ModifiableSolrParams();
			config.add(HttpClientUtil.PROP_MAX_CONNECTIONS, "1000");
			config.add(HttpClientUtil.PROP_MAX_CONNECTIONS_PER_HOST, "200");
			config.add(HttpClientUtil.PROP_CONNECTION_TIMEOUT, "240000");
			config.add(HttpClientUtil.PROP_SO_TIMEOUT, "2400000");
			solrClient = new  HttpSolrClient.Builder().withInvariantParams(config).withBaseSolrUrl(baseurl).build();
		}
		return solrClient;
		
	}
	
	
	public void closeServerConnection() throws Exception {
		log.info("closing solrclinet connetion");
		if(solrClient!=null) {
			solrClient.close();
		}
	}
}
