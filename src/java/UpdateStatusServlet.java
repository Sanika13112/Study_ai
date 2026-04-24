import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// REQUIRED: Database imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/UpdateStatusServlet")
public class UpdateStatusServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        // 1. Get and parse data
        String idStr = req.getParameter("id");
        String status = req.getParameter("status");

        if (idStr == null || status == null) {
            res.getWriter().print("failure");
            return;
        }

        int id = Integer.parseInt(idStr);

        // 2. Wrap everything in a try-catch block
        try {
            // Explicitly load the driver (good practice in older environments)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Use try-with-resources to automatically close the connection
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:33006/study_ai", 
                    "root", 
                    "Sanika@123");
                 PreparedStatement ps = con.prepareStatement("UPDATE tasks SET status=? WHERE id=?")) {

                ps.setString(1, status);
                ps.setInt(2, id);

                int rowsUpdated = ps.executeUpdate();
                
                if (rowsUpdated > 0) {
                    res.getWriter().print("success");
                } else {
                    res.getWriter().print("not_found");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            res.getWriter().print("driver_error");
        } catch (SQLException e) {
            e.printStackTrace();
            res.getWriter().print("db_error");
        }
    }
}