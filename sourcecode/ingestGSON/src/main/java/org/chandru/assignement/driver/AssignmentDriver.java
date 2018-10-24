package org.chandru.assignement.driver;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chandru.assignement.datahandler.ReadInputJson;
import org.chandru.assignement.dataingester.SolrClientHandler;
import org.chandru.assignement.dataingester.SolrIngesterThread;

/**
 * Main method to start the process
 * 
 * @param args
 * @author S.chandru
 */

public class AssignmentDriver {
	
	private static final Log log = LogFactory.getLog(AssignmentDriver.class);
	
	private static ExecutorService executor = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		log.info("Entering into main method");
		String baseurl = System.getProperty("baseUrl","http://127.0.0.1:8983/solr/localhost3");
		String outputDir = System.getProperty("outputDir","C:/test/");
		String inputFile = System.getProperty("inputFile","D:/study/hotels.json");
		log.info("baseurl : "+baseurl);
		log.info("outputDir : "+outputDir);
		log.info("inputFile : "+inputFile);
		try {
			executor = Executors.newFixedThreadPool(1);
			Callable<Boolean> callable = new SolrIngesterThread(baseurl);
			Future<Boolean> future = executor.submit(callable);
			File file = new File(inputFile);
			log.info("Entering into read and ingest json file");
			new ReadInputJson(file).readAndIngest();
			Boolean completed = future.get();
			log.info("completed read and ingest json file : "+completed);
			if(completed) {
				CSVResultDriver csvResultDriver = new CSVResultDriver(outputDir,baseurl);
				csvResultDriver.exportResultsToOutputDir();
			}
			log.info("Utility completed");
		}catch(Exception e) {
			log.error("Error in main method", e);
		}finally {
			try {
				SolrClientHandler.getInstance().closeServerConnection();
			} catch (Exception e) {
				
			}
			executor.shutdown();
			System.exit(0);
		}
	}

}
