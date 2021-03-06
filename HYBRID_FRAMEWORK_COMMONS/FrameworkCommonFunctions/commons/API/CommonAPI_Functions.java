
package commons.API;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.http.Header;
import io.restassured.response.Response;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import framework.Business.Layer.ReadJsonFile;

public class CommonAPI_Functions 
{
	public static Properties properties;
	public static String FILENAME="";
	private static ExtentTest logger = null; 
	
	public CommonAPI_Functions(ExtentTest logger) {
		this.logger = logger;
	} 
	
	public static void Get_API_Request(String URI,String Authorization,String methodName) throws Exception
	{
		logger.log(LogStatus.PASS, "Execution for "+methodName+ " has started"); 
		Response API_response = RestAssured.given().config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())).header("Authorization", Authorization).get(URI);					
		 if(API_response.getStatusCode()==200)
		 {
			 logger.log(LogStatus.PASS, "ResponseCode for API is " + API_response.getStatusCode());
		 }
		 else
		 {
			 throw new Exception("ResponseCode for API is " + API_response.getStatusCode());
		 }
		 String APIResponse=API_response.asString();
		 WriteAPI_Response_to_Jsonfile(APIResponse,methodName);	
		 logger.log(LogStatus.INFO, "<object type='application/json' data='"+FILENAME+"' width='100%' height='100%' JSON.stringify(data)></object>");
	}
	public static String POST_API_Request(String URI,String Authorization,Header header_content_type,String request_body, String methodName) throws Exception
	{
			logger.log(LogStatus.PASS, "Execution for "+methodName+ " has started");
			Response API_response = RestAssured.given().config(RestAssured.config().sslConfig(new SSLConfig().relaxedHTTPSValidation())).header("Authorization",Authorization).and().header(header_content_type).body(request_body).post(URI);						
			if(API_response.getStatusCode()==200)
			{
				logger.log(LogStatus.PASS, "ResponseCode for API is " + API_response.getStatusCode());
			}
			else
			{
				throw new Exception("ResponseCode for API is " + API_response.getStatusCode());
			}
			String APIResponse=API_response.asString();
			WriteAPI_Response_to_Jsonfile(APIResponse,methodName);
			logger.log(LogStatus.INFO, "<object type='application/json' data='"+FILENAME+"' width='100%' height='100%' JSON.stringify(data)></object>");
			return API_response.asString();
	}	
	public static void WriteAPI_Response_to_Jsonfile(String API_ResponseData,String methodName) throws Exception
	{
		FILENAME =properties.getProperty("Jsonresponse_filepath")+methodName+".json";
		File file = new File(FILENAME);
		if(file.exists()){
			file.delete();
		}else{
			file.createNewFile();
		}
		FileWriter writer = new FileWriter(FILENAME);
		BufferedWriter buffer = new BufferedWriter(writer);
		buffer.write(API_ResponseData);
		buffer.close();
	}
	public static void loadProperties() throws Exception
	{
		properties= new Properties();
		FileInputStream input = new FileInputStream(new File("C:/MARS_FRAMEWORK/MARS_Automation_Framework_Projects/mars/JCI/Project/MEMS/Configuration/apiconfig.properties"));
		properties.load(input);			
	}
	public static String GetToken(String methodName) throws Exception
	{
		String Access_token="";
		List<String> Access_tokens=ReadJsonFile.readJsonFileDynamic(FILENAME, properties.getProperty("Access_Token_jsonpath"));
		if(Access_tokens.size() > 0)
		{
			Access_token=Access_tokens.get(0);
			if(Access_token!=null && (!Access_token.equals(""))&& (!(Access_token.equals("null"))))
			{
				logger.log(LogStatus.PASS, "Access_token ="+ Access_token);
			}
			else
			{
				logger.log(LogStatus.FAIL, "Access_token is BLANK");
			}
			logger.log(LogStatus.PASS, "Execution for "+methodName+ " has Completed");
		}
		else
		{
			logger.log(LogStatus.FAIL, "Data for Requested API is not found");
		}
		return Access_token;
	}
}