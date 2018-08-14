import java.net.*;

import org.json.*;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.*;
import java.math.*;

@WebServlet("/problem_tags_more")
public class Problems_as_per_tags_more extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Set the response MIME type
        response.setContentType("text/html;charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        String tag_name = request.getParameter("option");
        String u_name = request.getParameter("username");

        String s1 = "<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Problem_Tags</title>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"problems_as_per_tags_more_java.css\"></head><body>";
        String s2 = "</body></html>";

        // Write the response message, in an HTML page
        try {
            out.println(s1);

            // tags problem
            String searchfortag = tag_name;
            String putassearchtag = searchfortag.replace(" ", "%20");

            String puturl = "http://codeforces.com/api/problemset.problems?tags=" + putassearchtag;

            URL oracle_tag = new URL(puturl);
            URLConnection yc = oracle_tag.openConnection();
            BufferedReader in_tag = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;

            String x_tag = "";
            while ((inputLine = in_tag.readLine()) != null)
                x_tag += (inputLine);

            in_tag.close();

            // Unsolved
            String handle_name = u_name;
            String put_as_handle = handle_name.replace(" ", "%20");

            URL oracle_unsolved = new URL("http://codeforces.com/api/user.status?handle=" + put_as_handle + "&from=1");
            URLConnection url = oracle_unsolved.openConnection();
            BufferedReader in_unsolved = new BufferedReader(new InputStreamReader(url.getInputStream()));

            String input_line_by_line;
            String input_concate = "";

            while ((input_line_by_line = in_unsolved.readLine()) != null)
                input_concate += (input_line_by_line);

            in_unsolved.close();

            try {
                // tags
                JSONObject jObject_tags = new JSONObject(x_tag);
                JSONObject result_tags = jObject_tags.getJSONObject("result");
                JSONArray problems_tags = result_tags.getJSONArray("problems");

                int totalproblems = problems_tags.length();
                int contestidarray[] = new int[totalproblems];
                String indexofquestion[] = new String[totalproblems];
                String nameofquestion[] = new String[totalproblems];


                int contestidarrayindex = -1;
                int indexofquestionindex = -1;
                int nameofquestionindex = -1;


                String gottag = "";
                for (int i = 0; i < totalproblems; i++) {
                    JSONObject onecontest = problems_tags.getJSONObject(i);
                    JSONArray tags = onecontest.getJSONArray("tags");
                    String indexofonequestion = onecontest.getString("index");
                    String nameofonequestion = onecontest.getString("name");

                    int totaltags = tags.length();
                    for (int j = 0; j < totaltags; j++) {
                        gottag = tags.getString(j);
                        if (gottag.equals(searchfortag)) {
                            contestidarray[++contestidarrayindex] = onecontest.getInt("contestId");
                            indexofquestion[++indexofquestionindex] = indexofonequestion;
                            nameofquestion[++nameofquestionindex] = nameofonequestion;
                        }
                    }
                }

                // unsolved
                JSONObject jObject_unsolved = new JSONObject(input_concate);
                JSONArray result_unsolved = jObject_unsolved.getJSONArray("result");

                int len = result_unsolved.length();

                HashMap<String, String> link_verdict = new HashMap<>();
                HashMap<String, ArrayList<String>> link_tags = new HashMap<>();

                for (int i = 0; i < len; i++) {
                    JSONObject contest = result_unsolved.getJSONObject(i);

                    String verdict = contest.getString("verdict");

                    int contestId = contest.getInt("contestId");

                    JSONObject problems = contest.getJSONObject("problem");
                    String index = problems.getString("index");
                    JSONArray tags = problems.getJSONArray("tags");

                    int l = tags.length();
                    ArrayList<String> Tags = new ArrayList<>();
                    for (int j = 0; j < l; j++)
                        Tags.add(tags.getString(j));

                    String LINK = contestId + " " + index;

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

                for (int i = 0; i <= contestidarrayindex; i++) {
                    String check = contestidarray[i] + " " + indexofquestion[i];

                    boolean ok = true;
                    if (link_verdict.containsKey(check) && link_verdict.get(check).equals("OK"))
                        ok = false;

                    if (ok) {
                        if (i % 3 == 0 && i != 0)
                            out.println("<br>");
                        String link = "http://codeforces.com/contest/" + contestidarray[i] + "/problem/" + indexofquestion[i];
                        String printnameofquestion = nameofquestion[i];
                        out.println("<a href=" + link + "><span>" + printnameofquestion + "</span></a>");

                    }
                }

            } catch (Exception e) {
                out.println("Error in problems_as_per_tags --> file  " + e);
            }

            out.println(s2);
        } finally {
            out.close();  // Always close the output writer
        }
    }
}