package asw01cs;


import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
//This code uses the Fluent API

public class SimpleFluentClient {

	private static String URI = "http://localhost:8080/waslab01_ss/";

	public final static void main(String[] args) throws Exception {
    	
    	/* Insert code for Task #4 here */
		
		System.out.println(Request.Post("http://localhost:8080/waslab01_ss/")
	    .bodyForm(Form.form().add("author",  "Pradas").add("tweet_text",  "cuentame lo k ase").build())
	    .addHeader ("Accept", "text/plain").execute().returnContent());
    	
		System.out.println(Request.Get(URI).addHeader ("Accept", "text/plain").execute().returnContent());
		
		String[] lastTweet = Request.Get(URI).addHeader ("Accept", "text/plain").execute().returnContent().toString().split("\r\n|\r|\n", 2);
		
    	/* Insert code for Task #5 here */
		
		System.out.println(Request.Post("http://localhost:8080/waslab01_ss/")
			    .bodyForm(Form.form().add("id_tweet",  lastTweet[0]).add("author",  "Pradas").add("tweet_text",  "a").build())
			    .addHeader ("Accept", "text/plain").execute().returnContent());
    }
}

