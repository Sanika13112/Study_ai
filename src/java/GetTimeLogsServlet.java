import com.tracker.util.DBConnection;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/GetTimeLogsServlet")
public class GetTimeLogsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("user");
        
        StringBuilder json = new StringBuilder("[");
        try {
            try (Connection con = DBConnection.getConnection()) {
                String sql = "SELECT t.id, t.subject, t.duration_minutes, t.log_date, t.notes " +
                        "FROM time_logs t INNER JOIN users u ON t.user_id = u.id " +
                        "WHERE u.username = ? AND t.log_date = CURDATE() ORDER BY t.log_date DESC";
                PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql);
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    first = false;
                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id")).append(",")
                            .append("\"subject\":\"").append(escapeJson(rs.getString("subject"))).append("\",")
                            .append("\"duration_minutes\":").append(rs.getInt("duration_minutes")).append(",")
                            .append("\"log_date\":\"").append(rs.getString("log_date")).append("\",")
                            .append("\"notes\":\"").append(escapeJson(rs.getString("notes"))).append("\"")
                            .append("}");
                }
            }
        } catch (SQLException e) {
            json.append("{\"error\":\"").append(escapeJson(e.getMessage())).append("\"}");
        }
        json.append("]");
        out.print(json.toString());
    }
    
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}