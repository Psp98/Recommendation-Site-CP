import java.net.*;

import org.json.*;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.*;
import java.math.*;

@WebServlet("/uprofile")
public class UserProfile extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Set the response MIME type
        response.setContentType("text/html;charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        String xx = request.getParameter("username");

        String s1 = "<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Profile</title>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"user_profile_java.css\"></head><body>";
        String s2 = "</body></html>";

        // Write the response message, in an HTML page
        try {
            out.println(s1);

            String handlename = xx;
            String putashandle = handlename.replace(" ", "%20");

            URL oracle = new URL("http://codeforces.com/api/user.info?handles=" + putashandle);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            String x = "";
            while ((inputLine = in.readLine()) != null)
                x += (inputLine);
            in.close();

            try {
                JSONObject jObject = new JSONObject(x);
                String strsuccessful = jObject.getString("status");
                if (!(strsuccessful.equals("OK"))) {
                    out.print("You are not authorized for this details");
                    out.print("<br>");
                    return;
                }

                JSONArray result = jObject.getJSONArray("result");
                JSONObject jsd = result.getJSONObject(0);
                HashMap<String, String> mapd = new HashMap<String, String>();
                Iterator<?> keys = jsd.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String ans = "";
                    try {
                        ans = jsd.getString(key);
                    } catch (Exception e) {
                        ans = Integer.toString(jsd.getInt(key));
                    }
                    mapd.put(key, ans);
                }
                for (Map.Entry m : mapd.entrySet()) {
                    out.println("<p><b>" + m.getKey() + " : </b>  " + m.getValue() + "</p>");
                    out.print("<br>");
                }
            } catch (Exception e) {
                out.println("Exception in user_profile " + e);
            }

            out.println(s2);
        } finally {
            out.close();  // Always close the output writer
        }
    }
}