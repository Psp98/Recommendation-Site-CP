import java.net.*;

import org.json.*;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.*;
import java.math.*;

@WebServlet("/problem_tags_full")
public class Problems_as_per_tags_full extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Set the response MIME type
        response.setContentType("text/html;charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        //String tag_name = request.getParameter("option");
        String u_name = request.getParameter("username");

        String s1 = "<!doctype html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Problem_Tags</title>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"problems_as_per_tags_java_full.css\"></head><body>";
        String s2 = "</body></html>";

        // Write the response message, in an HTML page
        try {
            out.println(s1);
            out.print("<script src=\"problems_as_per_tags_full_javascript_java.js\"></script>");
            // added javascript link for clickable view

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

            String searchfortag[] = {"trees", "greedy", "graphs", "number theory", "implementation", "dfs and similar", "dp",
                    "two pointers", "math", "sortings", "combinatorics"};
            String tagforjavascript[] = {"trees", "greedy", "graphs", "numbertheory", "implementation", "dfsandsimilar", "dp",
                    "twopointers", "math", "sortings", "combinatorics"};
            String displaysearchfortag[] = {"Tree", "Greedy", "Graph", "Number Theory", "Implementation", "DFS and Similar",
                    "Dynamic Programming", "Two Pointers", "Math", "Sortings", "Combinatorics"};

            //new try for unsolved json
            try {
                //started unsolved json
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
                //end of unsolved json
                for (int I = 0; I < searchfortag.length; I++) {
                    String putassearchtag = searchfortag[I].replace(" ", "%20");

                    String puturl = "http://codeforces.com/api/problemset.problems?tags=" + putassearchtag;

                    URL oracle_tag = new URL(puturl);
                    URLConnection yc = oracle_tag.openConnection();
                    BufferedReader in_tag = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                    String inputLine;

                    String x_tag = "";
                    while ((inputLine = in_tag.readLine()) != null)
                        x_tag += (inputLine);

                    in_tag.close();

                    try {
                        // tags
                        JSONObject jObject_tags = new JSONObject(x_tag);
                        JSONObject result_tags = jObject_tags.getJSONObject("result");
                        JSONArray problems_tags = result_tags.getJSONArray("problems");
                        //System.out.println("\n\n\n\n-----------------"+problems.length());
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
                                if (gottag.equals(searchfortag[I])) {
                                    contestidarray[++contestidarrayindex] = onecontest.getInt("contestId");
                                    indexofquestion[++indexofquestionindex] = indexofonequestion;
                                    nameofquestion[++nameofquestionindex] = nameofonequestion;
                                }
                            }
                        }

                        int count = 0;

                        if (I < 6)
                            out.print("<div class=\"dropdown\" ><button class=\"dropbtn\">" + displaysearchfortag[I] + "</button><div id=\"" + tagforjavascript[I] + "\" class=\"dropdown-content\"  style=\"left:0;\">");
                        else
                            out.print("<div class=\"dropdown\" ><button class=\"dropbtn\">" + displaysearchfortag[I] + "</button><div id=\"" + tagforjavascript[I] + "\" class=\"dropdown-content\"  style=\"right:0;\">");

                        // Getting Problem statistics.....
                        JSONArray problemsStats = result_tags.getJSONArray("problemStatistics");

                        int l = problemsStats.length();
                        HashMap<Integer, HashMap<String, Integer>> problem_stats_info = new HashMap<>();

                        for (int i = 0; i < l; i++) {
                            JSONObject probleminfo = problemsStats.getJSONObject(i);
                            int id_no = probleminfo.getInt("contestId");
                            String index_no = probleminfo.getString("index");
                            int solve_count = probleminfo.getInt("solvedCount");
                            HashMap<String, Integer> hm = new HashMap<>();
                            hm.put(index_no, solve_count);

                            problem_stats_info.put(id_no, hm);
                        }
                        // Received Problem statistics....

                        // to store 'Index_In_Stored_Array, solvedCount'...
                        Integer final_ans[][] = new Integer[contestidarrayindex + 1][2];
                        int ind = 0;

                        for (int i = 0; i <= contestidarrayindex; i++) {
                            String check = contestidarray[i] + " " + indexofquestion[i];

                            boolean ok = true;
                            if (link_verdict.containsKey(check) && link_verdict.get(check).equals("OK")) {
                                ok = false;
                            }

                            if (ok) {
                                count++;

                                if (problem_stats_info.containsKey(contestidarray[i])) {
                                    HashMap<String, Integer> hm = problem_stats_info.get(contestidarray[i]);

                                    if (hm.containsKey(indexofquestion[i])) {
                                        final_ans[ind][0] = i;
                                        final_ans[ind++][1] = hm.get(indexofquestion[i]);
                                    }
                                }
                            }
                            if (count == 10) {
                                break;
                            }
                        }

                        for (int i = ind; i <= contestidarrayindex; i++)
                            final_ans[i][1] = Integer.MAX_VALUE;

                        Arrays.sort(final_ans, new Comparator<Integer[]>() {
                            public int compare(Integer[] int1, Integer[] int2) {
                                return int1[1].compareTo(int2[1]);
                            }
                        });

                        for (int I1 = 0; I1 < 10; I1++) {
                            int i = (int) final_ans[I1][0];
                            String link = "http://codeforces.com/contest/" + contestidarray[i] + "/problem/" + indexofquestion[i];
                            String printnameofquestion = nameofquestion[i];
                            out.println("<a href=" + link + ">" + printnameofquestion + "</a>");
                        }

                        //------------------------------------------------------------------------------------------------

                        String putastag = searchfortag[I].replace(" ", "%20");
                        out.println("<a href=http://localhost:9999/minip/problem_tags_more?option=" + putastag + "&username=" + u_name + ">more...</a>");
                        out.print("</div></div>");
                    }//catch for tag json
                    catch (Exception e) {
                        out.println("Error in problems_as_per_tags --> file  " + e);
                    }
                }//end of for loop

            }//catch for unsolved json
            catch (Exception e) {
                out.println("Error in getting unsolved " + e);
            }
            out.println(s2);

        } finally {
            out.close();  // Always close the output writer
        }//end of main try
    }
}