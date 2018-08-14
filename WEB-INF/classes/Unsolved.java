import java.net.*;

import org.json.*;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.*;
import java.math.*;

@WebServlet("/unsolve")
public class Unsolved extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Set the response MIME type
        response.setContentType("text/html;charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        String xx = request.getParameter("username");

        String s1 = "<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Unsolved Problems</title>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"unsolved_java.css\"></head><body>";
        String s2 = "</body></html>";

        // Write the response message, in an HTML page
        try {
            out.println(s1);

            String handle_name = xx;
            String put_as_handle = handle_name.replace(" ", "%20");

            URL oracle = new URL("http://codeforces.com/api/user.status?handle=" + put_as_handle + "&from=1");
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

                HashMap<String, String> link_verdict = new HashMap<>();
                HashMap<String, ArrayList<String>> link_tags = new HashMap<>();

                for (int i = 0; i < len; i++) {
                    JSONObject contest = result.getJSONObject(i);

                    String verdict = contest.getString("verdict");

                    int contestId = contest.getInt("contestId");

                    JSONObject problems = contest.getJSONObject("problem");
                    String index = problems.getString("index");
                    String questionname = problems.getString("name");
                    String put_as_questioname = questionname.replace(" ", "|");

                    JSONArray tags = problems.getJSONArray("tags");

                    int l = tags.length();
                    ArrayList<String> Tags = new ArrayList<>();
                    for (int j = 0; j < l; j++)
                        Tags.add(tags.getString(j));

                    String LINK = contestId + " " + index + " " + put_as_questioname;

                    if (link_verdict.containsKey(LINK)) {
                        if (!link_verdict.get(LINK).equals("OK") && verdict.equals("OK")) {
                            link_verdict.put(LINK, "OK");

                            if (link_tags.containsKey(LINK))
                                link_tags.remove(LINK);
                        }
                    } else {
                        link_verdict.put(LINK, verdict);
                        if (!verdict.equals("OK"))
                            link_tags.put(LINK, Tags);
                    }
                }

                for (Map.Entry m : link_tags.entrySet()) {
                    String s = (String) m.getKey();
                    ArrayList<String> TAGS = (ArrayList) m.getValue();

                    String S[] = s.split(" ");
                    String cId = S[0];
                    String Index = S[1];
                    String qname = S[2];

                    String final_questioname = qname.replace("|", " ");

                    String final_link = "http://codeforces.com/contest/" + cId + "/problem/" + Index;
                    out.println("<a href=" + final_link + "><span>" + final_questioname + "</span>");
                    out.print("<br>");
                    out.print(TAGS);
                    out.print("<br></a>");
                    out.print("<br>");
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