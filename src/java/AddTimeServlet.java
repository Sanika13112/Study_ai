import com.tracker.util.DBConnection;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/AddTimeServlet")
public class AddTimeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String subject = req.getParameter("subject");
        String durationStr = req.getParameter("duration");
        String notes = req.getParameter("notes");
        
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("user");
        
        if (subject == null || durationStr == null || username == null) {
            res.getWriter().print("Error: Missing parameters or not logged in");
            return;
        }
        
        int minutes;
        try {
            minutes = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            res.getWriter().print("Error: Duration must be a number");
            return;
        }
        
        try {
            // Insert into time_logs table
            try ( // Use your existing DBConnection class (adjust if you have a different class name)
                    Connection con = DBConnection.getConnection()) {
                // Insert into time_logs table
                String sql = "INSERT INTO time_logs (user_id, subject, duration_minutes, log_date, notes) " +
                        "VALUES ((SELECT id FROM users WHERE username=?), ?, ?, CURDATE(), ?)";
                PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, subject);
                ps.setInt(3, minutes);
                ps.setString(4, notes);
                ps.executeUpdate();
                
                ps.close();
            }
            res.getWriter().print("Time Logged Successfully!");
            
        } catch (IOException | SQLException e) {
            res.getWriter().print("Error: " + e.getMessage());
        }
    }
}