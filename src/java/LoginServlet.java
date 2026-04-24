import java.io.IOException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet; // Added this missing import

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        String u = req.getParameter("username");
        String p = req.getParameter("password");

        // Use try-with-resources for automatic closing of database objects
        String query = "SELECT * FROM users WHERE username=? AND password=?";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:33006/study_ai", // Double check port 33006 vs 3306
                    "root",
                    "Sanika@123");
                 PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, u);
                ps.setString(2, p);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        HttpSession s = req.getSession();
                        s.setAttribute("user", u);
                        s.setAttribute("user_id", rs.getInt("id"));

                        res.getWriter().print("success");
                    } else {
                        res.getWriter().print("invalid");
                    }
                }
            } 

        } catch (Exception e) {
            e.printStackTrace();
            // It's safer to send a 500 error code or a specific message
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().print("error: " + e.getMessage());
        }
    }
}