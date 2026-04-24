import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Import specific SQL classes to avoid conflicts
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/DeleteTaskServlet")
public class DeleteTaskServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        
        try {
            // 1. Get ID from request
            String idStr = req.getParameter("id");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr);

            // 2. Load Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 3. Database operation (Note the semicolon between con and ps)
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:33006/study_ai", "root", "Sanika@123");
                 PreparedStatement ps = con.prepareStatement("DELETE FROM tasks WHERE id=?")) {

                ps.setInt(1, id);
                ps.executeUpdate();
                
                res.getWriter().print("success");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().print("error");
        }
    }
}