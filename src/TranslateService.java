

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * This class defines an easy access to the Google Translate API. The access
 * is done through their web service. 
 * 
 * Documentation on the Google Translate API can be found:
 *  https://cloud.google.com/translate/docs
 * 
 * The specifics about the WebService can be found:
 * https://cloud.google.com/translate/v2/getting_started
 * 
 * The resulting JSON object is being parsed using a simple JSON parser.
 * 
 * Notice that in most cases you don't want to use the translation methods from the
 * main thread of your application. Doing so will cause your GUI to halt until the 
 * web service returns the valid answer. Doing the translation on a separate Thread 
 * is strongly advised. 
 * 
 * You will need to include the dependencies: 
 *    - commons-logging-1.1.1.jar
 *    - httpclient-4.0.1.jar
 *    - httpcore-4.0.1.jar
 *    - json-simple-1.1.1.jar
 * 
 * @author Diego J. Rivera-Gutierrez
 */
public class TranslateService {
        /**
         * Set the value of this constant to your Google Translate API key.
         */
        public static final String KEY = "AIzaSyAXu383yOYwXkjSsCTFScBqF4_ii_JnNUI";
        /**
         * Base URL of the web service we are using.
         */
        public static final String BASE_URL = "https://www.googleapis.com/language/translate/v2";
        
        /**
         * HttpClient used to access the web service
         */
        private HttpClient client;
        /**
         * Stores the last URL that was retrieved. In some cases for debugging you might want to 
         * access this and check if the JSON you are getting from the web service is well formed.
         */
        private String lastURL;
        /**
         * Parser for the JSON results.
         */
        private JSONParser parser;
        public String obtainedSource;

        /**
         * Constructor of the class
         */
        public TranslateService() {
            client = new DefaultHttpClient();
            parser = new JSONParser();
        }
        
        /**
         * First method used to translate. This method returns a parsed JSON object.
         * If you want to perform validation of the returned data, you will need to 
         * use this method and validate the returned JSON object before interpreting.
         * 
         * 
         * @param text Text to be translated
         * @param target target Language (see Language.java for accepted values)
         * @param source source Language (see Language.java, if you null the API will attempt detection of the language)
         * @return Parsed JSONObject.
         * @throws UnsupportedEncodingException
         * @throws IOException
         * @throws ParseException 
         */
        public JSONObject translateToJSON(String text, String target, String source) throws UnsupportedEncodingException, IOException, ParseException {
            String url = BASE_URL + "?key=" + KEY;
            
            List<NameValuePair> urlParameters = new ArrayList<>();
            
            urlParameters.add(new BasicNameValuePair("target", target));
            if(source != null)
                urlParameters.add(new BasicNameValuePair("source", source));
 
            urlParameters.add(new BasicNameValuePair("q", text));
          
            
            
            for (NameValuePair urlParameter : urlParameters) {
                url += "&" + urlParameter.getName() + "=" + URLEncoder.encode(urlParameter.getValue(), "utf-8");
            }
            
            
            
            HttpGet request = new HttpGet(url);
            //useful for debugging purposes
            lastURL = url;
            
             HttpResponse response = client.execute(request);
             
             
 
            BufferedReader rd = new BufferedReader(
                       new InputStreamReader(response.getEntity().getContent(),"utf-8"));
 
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
		result.append(line);
                //---------------------------------------------MyCode-------------------------------
                //System.out.println(line);
                int len = line.length();
               // System.out.println(len);
                if(len>=30){
                    String subs = line;
                    if(subs.contains("detectedSourceLanguage")){
                        int i =subs.indexOf(": ");
                        //System.out.println("The value of i is: "+ i);
                       obtainedSource = subs.substring(i+3,len-1);
                        //HomeScreen.fromTranslateService(obtainedSource,line);
                    }
                }
            }
            
            
            return (JSONObject)parser.parse(result.toString());
		
        }
        
        /**
         * This method can be used for translation too. It assumes that the service is returning
         * a valid translation and no errors, and it will return the string with the translated text.
         * 
         * @param text Text to be translated
         * @param target target Language (see Language.java for accepted values)
         * @param source source Language (see Language.java, if you null the API will attempt detection of the language)
         * @return translated text
         * @throws UnsupportedEncodingException
         * @throws IOException 
         */
        public String translate(String text, String target, String source) throws  UnsupportedEncodingException, IOException {
            try{
                JSONObject json = translateToJSON(text, target, source);
                //{"data":{"translations":[{"translatedText":"hi"}]}}
                JSONObject data = (JSONObject)json.get("data");
                JSONArray translations = (JSONArray)data.get("translations");
                JSONObject first = (JSONObject)translations.get(0);
                
                
                return first.get("translatedText").toString();
                
            }catch(ParseException e) {
                e.printStackTrace();
                return "[ERROR] Couldn't parse.";

            }
            
            
        }
        
        
        
        public String trans(String text, String target, String source) throws  UnsupportedEncodingException, IOException {
            try{
                JSONObject json = translateToJSON(text, target,null);
                //{"data":{"translations":[{"translatedText":"hi"}]}}
                JSONObject data = (JSONObject)json.get("data");
                JSONArray translations = (JSONArray)data.get("translations");
                JSONObject first = (JSONObject)translations.get(0);
                
                
                return first.get("translatedText").toString();
                
            }catch(ParseException e) {
                e.printStackTrace();
                return "[ERROR] Couldn't parse.";

            }
            
            
        }
        
        
        /**
         * Accessor method to the last URL that was tried to be retrieved by the class
         * @return Last URL that was attempted.
         */
        public String getLastURL() {
            return lastURL;
        }
        
    
    
    
}
    
