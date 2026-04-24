import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GetTopicsServlet") // Matches your JS fetch("GetTopicsServlet")
public class GetTaskServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:33006/study_ai";
            
            try (Connection con = DriverManager.getConnection(url, "root", "Sanika@123");
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM tasks")) {
                
                ResultSet rs = ps.executeQuery();
                StringBuilder json = new StringBuilder("[");
                boolean first = true;

                while (rs.next()) {
                    if (!first) json.append(",");
                    
                    json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"subject\":\"").append(rs.getString("subject")).append("\",")
                        .append("\"topic\":\"").append(rs.getString("topic")).append("\",")
                        .append("\"status\":\"").append(rs.getString("status")).append("\",")
                        .append("\"due_date\":\"").append(rs.getString("due_date")).append("\",") // CRITICAL FOR CALENDAR
                        .append("\"time\":\"").append(rs.getString("time")).append("\"")         // CRITICAL FOR ALARMS
                        .append("}");
                    
                    first = false;
                }
                json.append("]");
                out.print(json.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(500);
            out.print("{\"error\":\"Database connection failed: " + e.getMessage() + "\"}");
        }
    }
}