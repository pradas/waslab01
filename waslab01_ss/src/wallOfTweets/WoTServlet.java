package wallOfTweets;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class WoTServlet
 */
@WebServlet("/")
public class WoTServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Locale currentLocale = new Locale("en");
	String ENCODING = "ISO-8859-1";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WoTServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String acc = request.getHeader("Accept");
			Vector<Tweet> tweets = Database.getTweets();
			if (acc.equals("text/plain")) printPLAINresult(tweets, request, response);
			else printHTMLresult(tweets, request, response);
		}

		catch (SQLException ex ) {
			throw new ServletException(ex);
		}
	}
	
	private String MD5(String md5) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(md5.getBytes());
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    }
		    return null;
		}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {	
			if (request.getParameter("id_tweet") == null) {
				long idTweetNew = Database.insertTweet(request.getParameter("author"), request.getParameter("tweet_text"));				
				SecureRandom random = new SecureRandom();
				String nomCookie = new BigInteger(130, random).toString(32);
				String contentCookie = MD5(String.valueOf(idTweetNew));					
				Cookie c = new Cookie (nomCookie, contentCookie);				
				response.addCookie(c);				
			}
			else {				
				if (request.getCookies() != null) {
				
					Cookie [] c = request.getCookies();
				
					long idTweetBorrat = Long.parseLong(request.getParameter("id_tweet"));
					String contentCookie = MD5(String.valueOf(idTweetBorrat));	
					
				
					for (Cookie cc: c) {										
						if (cc.getValue().equals(contentCookie)) Database.deleteTweet(idTweetBorrat);
					}					
				}			
			}			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!request.getHeader("Accept").equals("text/plain"))
			response.sendRedirect(request.getContextPath());		
	}
	
	private void printHTMLresult (Vector<Tweet> tweets, HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, currentLocale);
		DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, currentLocale);
		String currentDate = dateFormatter.format(new java.util.Date());
		res.setContentType ("text/html");
		res.setCharacterEncoding(ENCODING);
		PrintWriter  out = res.getWriter ( );
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head><title>Wall of Tweets</title>");
		out.println("<link href=\"wallstyle.css\" rel=\"stylesheet\" type=\"text/css\" />");
		out.println("</head>");
		out.println("<body class=\"wallbody\">");
		out.println("<h1>Wall of Tweets</h1>");
		out.println("<div class=\"walltweet\">"); 
		out.println("<form method=\"post\">");
		out.println("<table border=0 cellpadding=2>");
		out.println("<tr><td>Your name:</td><td><input name=\"author\" type=\"text\" size=70></td><td></td></tr>");
		out.println("<tr><td>Your tweet:</td><td><textarea name=\"tweet_text\" rows=\"2\" cols=\"70\" wrap></textarea></td>"); 
		out.println("<td><input type=\"submit\" name=\"action\" value=\"Tweet!\"></td></tr>"); 
		out.println("</table></form></div>"); 
		for (Tweet tweet: tweets) {
			String messDate = dateFormatter.format(tweet.getDate());
			if (!currentDate.equals(messDate)) {
				out.println("<br><h3>...... " + messDate + "</h3>");
				currentDate = messDate;
			}
			out.println("<div class=\"wallitem\">");
			out.println("<h4><em>" + tweet.getAuthor() + "</em> @ "+ timeFormatter.format(tweet.getDate()) +"</h4>");
			
			out.println("<form method=\"post\">");
			out.println("<input type=\"hidden\" name=\"id_tweet\" value=\""+ tweet.getTwid() +"\">");
			out.println("<input type=\"submit\" value=\" Esborrar Tweet\">");
			out.println("</form>");

			out.println("<p>" + tweet.getText() + "</p>");
			out.println("</div>");
		}
		out.println ( "</body></html>" );
	}
	
	private void printPLAINresult (Vector<Tweet> tweets, HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		
		String pattern = "EEE MMM d hh:mm:ss z yyyy";
		
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
				
		res.setContentType ("text/html");
		res.setCharacterEncoding(ENCODING);
		PrintWriter  out = res.getWriter ( );
		
		out.println(tweets.get(0).getTwid());
		
		for (Tweet tweet: tweets) {			
			out.println("tweet #" + tweet.getTwid() + ": " + tweet.getAuthor() + ": "+ tweet.getText() + " ["+ formatter.format(tweet.getDate()) +"]");
		}
	}
}
