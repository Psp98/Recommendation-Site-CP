import java.net.*;

import org.json.*;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.*;
import java.math.*;
import java.text.*;

@WebServlet("/upcoming_events")
public class Upcoming_events extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Set the response MIME type
        response.setContentType("text/html;charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        String s1 = "<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Event Calendar</title>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"upcoming_events_java.css\"></head><body>";
        String s2 = "</body></html>";

        // Write the response message, in an HTML page
        try {
            out.println(s1);

            URL oracle = new URL("http://codeforces.com/api/contest.list?gym=false");
            URLConnection url = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(url.getInputStream()));

            String input_line_by_line;
            String input_concate = "";

            while ((input_line_by_line = in.readLine()) != null)
                input_concate += (input_line_by_line);

            try {
                JSONObject jObject = new JSONObject(input_concate);
                JSONArray result = jObject.getJSONArray("result");

                int len = result.length();

                for (int i = 0; i < len; i++) {
                    JSONObject contest = result.getJSONObject(i);

                    String whatphase = contest.getString("phase");
                    String nameofcontest = contest.getString("name");
                    int contestId = contest.getInt("id");
                    long contestDuration = contest.getInt("durationSeconds");
                    long staringTime = contest.getInt("startTimeSeconds");

                    if (whatphase.equals("BEFORE")) {

                        String final_name = nameofcontest;

                        //Unix seconds
                        long unix_seconds = staringTime;
                        //convert seconds to milliseconds
                        Date date = new Date(unix_seconds * 1000L); // *1000 is to convert seconds to milliseconds
                        // format of the date
                        SimpleDateFormat jdf = new SimpleDateFormat("MMMM d, yyyy 'on' EEEEEEEE 'at' hh:mm a 'IST' "); // the format of your date
                        jdf.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));// give a timezone reference for formating (see comment at the bottom
                        String putindiandate = jdf.format(date);

                        long MINUTES_IN_AN_HOUR = 60;
                        long SECONDS_IN_A_MINUTE = 60;
                        long seconds = contestDuration % SECONDS_IN_A_MINUTE;
                        long totalMinutes = contestDuration / SECONDS_IN_A_MINUTE;
                        long minutes = totalMinutes % MINUTES_IN_AN_HOUR;
                        long hours = totalMinutes / MINUTES_IN_AN_HOUR;

                        String durationTime = "Duration : " + hours + " hours ";
                        if (minutes != 0)
                            durationTime += minutes + " minutes ";
                        if (seconds != 0)
                            durationTime += seconds + " seconds";

                        out.println("<a href=\"#\"><span>" + final_name + "</span>");
                        out.print("<br>" + putindiandate);
                        out.print("<br>" + durationTime + "</a>");
                        out.print("<br>");

                    }
                }
            } catch (Exception e) {
                out.println("Error in Usolved.java " + e);
            }

            out.println(s2);
        } finally {
            out.close();  // Always close the output writer
        }
    }
}
