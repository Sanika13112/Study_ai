import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Correct Imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/GetPlansServlet")
public class GetPlansServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("user_id");

        if (userId == null) {
            res.getWriter().print("[]");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:33006/study_ai", "root", "Sanika@123");
                 PreparedStatement ps = con.prepareStatement("SELECT * FROM study_plans WHERE user_id=?")) {
                
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"title\":\"").append(rs.getString("title")).append("\"")
                        .append("}");
                    first = false;
                }
                json.append("]");
                res.getWriter().print(json.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().print("[]");
        }
    }
}