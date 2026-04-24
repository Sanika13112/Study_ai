import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        // Set response type
        res.setContentType("text/plain");
        PrintWriter out = res.getWriter();

        // Get data from form
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            // Load driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:33006/study_ai",
                "root",
                "Sanika@123"
            );

            // Prepare SQL
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(username, password) VALUES(?, ?)"
            );

            ps.setString(1, username);
            ps.setString(2, password);

            // Execute
            int rows = ps.executeUpdate();

            if (rows > 0) {
                out.print("Registered Successfully");
            } else {
                out.print("Registration Failed");
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.print("Error: " + e.getMessage());
        }
    }
}