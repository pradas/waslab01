package asw01cs;


import org.apache.http.client.fluent.Request;
//This code uses the Fluent API

public class SimpleFluentClient {

    public final static void main(String[] args) throws Exception {
    	
    	/* Insert code for Task #4 here */
    	
    	System.out.println(Request.Get("http://localhost:8080/waslab01_ss").execute().returnContent());
    	
    	/* Insert code for Task #5 here */
    }
}

