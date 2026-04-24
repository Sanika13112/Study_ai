import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 1. ADD THESE MISSING IMPORTS
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/EditTaskServlet")
public class EditTaskServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        try {
            // 2. Parse parameters
            int id = Integer.parseInt(req.getParameter("id"));
            String s = req.getParameter("subject");
            String t = req.getParameter("topic");

            // 3. Load the Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 4. Use try-with-resources to ensure connection closes
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:33006/study_ai", "root", "Sanika@123");
                 PreparedStatement ps = con.prepareStatement("UPDATE tasks SET subject=?, topic=? WHERE id=?")) {

                ps.setString(1, s);
                ps.setString(2, t);
                ps.setInt(3, id);

                int result = ps.executeUpdate();
                
                if (result > 0) {
                    res.getWriter().print("Updated Successfully");
                } else {
                    res.getWriter().print("Task not found");
                }
            }
        } catch (ClassNotFoundException e) {
            res.getWriter().print("Driver Error");
        } catch (SQLException e) {
            e.printStackTrace();
            res.getWriter().print("Database Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            res.getWriter().print("Invalid ID format");
        }
    }
}