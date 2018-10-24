package org.chandru.assignement.driver;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.chandru.assignement.dataingester.SolrClientHandler;

import com.opencsv.CSVWriter;

/** Class to export CSV results.
 * @author S.chandru
 */

public class CSVResultDriver {
	
	private static final Log log = LogFactory.getLog(CSVResultDriver.class);

	private static String outputDir = null;
	
	private static String baseurl = null;
	
	private static SolrClient solrClient = null;
	
	private static  CSVWriter csvWriter = null;
	
	public CSVResultDriver(String outputDir,String baseurl) {
		
		this.outputDir = outputDir;
		this.baseurl = baseurl;
	} 
	
	/**
	 * method where we read the type from solr and export into CSV based on number of rooms.
	 * @author S.chandru
	 * @throws Exception
	 */
	
	public void exportResultsToOutputDir() throws Exception  {
		// TODO Auto-generated method stub
		log.info("Entering into exportResultsToOutputDir method");
		try {
			 File file = new File(outputDir); 
			 FileWriter outputfile = new FileWriter(file.getAbsolutePath().concat(File.separator).concat("output.csv"));
			 csvWriter = new CSVWriter(outputfile);
			 String[] header = { "HotelName", "State", "Type","Rooms" }; 
			 csvWriter.writeNext(header); 
			 solrClient = SolrClientHandler.getInstance().createSolrClient(baseurl);
				ModifiableSolrParams solrMapParams = new ModifiableSolrParams();
				solrMapParams.add("q", "*:*");
				solrMapParams.add("rows", "" + 0);
				solrMapParams.add("facet", "" + true);
				solrMapParams.add("facet.field", "type");
				solrMapParams.add("facet.limit", "500");
				QueryResponse queryResponse = null;
				try {
					queryResponse = executeWithRety(solrClient,solrMapParams);
				} catch (SolrServerException e) {
					throw (e);
				}

				Object facets = queryResponse.getResponse().get("facet_counts");
				Object facetFields = ((NamedList<?>) facets).get("facet_fields");
				NamedList<?> countCustFacet = (NamedList<?>) ((NamedList<?>) facetFields).get("type");
				if (countCustFacet != null || countCustFacet.size() > 0) {
					QueryResponse queryResponse2 = null;
					SolrDocumentList solrDocumentList = null;
					if(countCustFacet!=null){
						for (int i = 0; i < countCustFacet.size(); i++) {
							String custName = countCustFacet.getName(i);
							String query2 = "type:(\""+custName+"\")";
							solrMapParams = new ModifiableSolrParams();
							solrMapParams.add("q", query2);
							solrMapParams.add("fl", "hotelname,state,type,rooms");
							solrMapParams.add("rows", "" + 1);
							solrMapParams.add("sort", "rooms desc");
							try {
								queryResponse2 = executeWithRety(solrClient,solrMapParams);
							}catch(SolrServerException e) {
								throw (e);
							}
							solrDocumentList = queryResponse2.getResults();
							for(SolrDocument solrDoc : solrDocumentList) {
								String hotelname = solrDoc.getFieldValue("hotelname")==null?"NA":(String) solrDoc.getFieldValue("hotelname");
								String state =  solrDoc.getFieldValue("state")==null?"NA":(String) solrDoc.getFieldValue("state");
								String type =  solrDoc.getFieldValue("type")==null?"NA":(String) solrDoc.getFieldValue("type");
								int rooms =   solrDoc.getFieldValue("rooms")==null?0: (int) solrDoc.getFieldValue("rooms");
								String[] val = { hotelname, state, type,rooms+"" }; 
								csvWriter.writeNext(val); 
							}
						}
					}
				 log.info("CSV write is completed");				
			}
		}catch(Exception e) {
			log.error("Error in exportResultsToOutputDir method",e);
			throw e;
		}finally {
			csvWriter.close();
		}
	}
	
	private static QueryResponse executeWithRety(SolrClient solrClient, ModifiableSolrParams solrParam) throws Exception {
		QueryResponse queryResponse = null;
		boolean isError = false;
		int count = 0;
		Exception exp = null;
		do{
			try{
				count++;
				queryResponse = solrClient.query(solrParam, METHOD.POST);
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
		return queryResponse;
		
	}


}
