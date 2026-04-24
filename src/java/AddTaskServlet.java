import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/AddTopicServlet") // Matches your JS fetch("AddTopicServlet")
public class AddTaskServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        // 1. Get parameters matching your index.jsp fetch call
        String subject = req.getParameter("subject");
        String topic = req.getParameter("topic");
        String dueDate = req.getParameter("due_date"); // Matches 'due_date' in JS
        String time = req.getParameter("time");       // Matches 'time' in JS

        // 2. Simple Validation
        if (subject == null || topic == null || dueDate == null) {
            res.getWriter().print("Error: Missing required fields");
            return;
        }

        // 3. Database operations
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Note: Check if your port is 3306 or 33006. Standard is 3306.
            String url = "jdbc:mysql://localhost:33006/study_ai";
            
            try (Connection con = DriverManager.getConnection(url, "root", "Sanika@123");
                 PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO tasks(subject, topic, due_date, time, status) VALUES(?,?,?,?, 'Pending')")) {
                
                ps.setString(1, subject);
                ps.setString(2, topic);
                ps.setString(3, dueDate);
                ps.setString(4, time); // This is the Alarm System link

                int result = ps.executeUpdate();
                
                if (result > 0) {
                    res.getWriter().print("Success");
                } else {
                    res.getWriter().print("Failed to save");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().print("Driver Error");
        } catch (SQLException e) {
            e.printStackTrace();
            res.setStatus(500);
            res.getWriter().print("Database Error: " + e.getMessage());
        }
    }
}