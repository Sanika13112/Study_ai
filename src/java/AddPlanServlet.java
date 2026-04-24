import java.io.IOException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 *
 * @author Sanika
 */
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/AddPlanServlet")
public class AddPlanServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        try {
            // ✅ Get session user
            HttpSession session = req.getSession();
            int uid = (int) session.getAttribute("user_id");

            String title = req.getParameter("title");
            String date = req.getParameter("date");

            // ✅ Load Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // ✅ Create Connection
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:33006/study_ai",
                "root",
                "Sanika@123"
            );

            // ✅ Prepare Statement
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO study_plans(user_id,title,date) VALUES(?,?,?)"
            );

            ps.setInt(1, uid);
            ps.setString(2, title);
            ps.setString(3, date);

            ps.executeUpdate();

            res.getWriter().print("Plan Added Successfully");

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().print("Error: " + e.getMessage());
        }
    }
}