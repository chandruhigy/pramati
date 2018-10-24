package org.chandru.assignement.datahandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chandru.assignement.datacache.DataQueueStore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class ReadInputJson {
	
	private static final Log log = LogFactory.getLog(ReadInputJson.class);

	private File inputJson = null;
	
	private static final List<String> headerList = Arrays.asList(System.getProperty("headerList", new String("hotelname,address,state,phone,fax,emailid,website,type,rooms")).split(","));

	public ReadInputJson(File file) {

		this.inputJson = file;
	}

	/**
	 * Method to read the json as stream , convert the json arry objects into POJO and load into cache
	 * @author S.chandru
	 * @throws Exception
	 */
	
	public void readAndIngest() throws Exception {
		log.info("Entering into readAndIngest method");
		FileInputStream fileInputStream = new FileInputStream(inputJson);
		JsonReader reader = new JsonReader(new InputStreamReader(fileInputStream, "UTF-8"));
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("fields")) {
				reader.skipValue();
			}
			if (name.equals("data")) {
				handleJsonArray(reader);
			}

			log.info("Json key is : "+name);
		}
		reader.close();
	}

	private static void handleJsonArray(JsonReader reader) throws Exception {
		List<String> list = new LinkedList<String>();
		while (true) {
			JsonToken token = reader.peek();
			if (token.equals(JsonToken.END_ARRAY)) {
				HotelsTO hotelhto = getHotelTo(list);
				if(hotelhto!=null) {
					log.info("Putting hotel values into cache :"+hotelhto.toString());
					DataQueueStore.getInstance().put(hotelhto);
					list.clear();
				}
				reader.endArray();
			} else if (token.equals(JsonToken.BEGIN_ARRAY)) {
				reader.beginArray();
			} else if (token.equals(JsonToken.END_OBJECT)) {
				break;
			} else {
				if (!token.equals(JsonToken.NULL)) {
					list.add(reader.nextString());
				} else {
					reader.nextNull();
					list.add(null);
				}
			}
		}

	}

	private static HotelsTO getHotelTo(List<String> list)  throws Exception{
		// TODO Auto-generated method stub
		HotelsTO hoteTO = null;
		if(!list.isEmpty()) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < list.size(); i++) {
				if (StringUtils.isNotBlank(list.get(i))) {
					map.put(headerList.get(i).trim(), list.get(i).trim());
				} else {
					map.put(headerList.get(i).trim(), null);
				}
			}
			ObjectMapper mapper = new ObjectMapper();
			hoteTO = mapper.convertValue(map, HotelsTO.class);
		}
		return hoteTO;
	}

	//validationpurpose
	/*public static void main(String args[]) {
		String filename = "D:\\study\\test.json";
		File file = new File(filename);
		new ReadInputJson(file).readAndIngest();

	}*/

}
