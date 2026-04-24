/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sanika
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet{
protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException{

req.getSession().invalidate();
res.sendRedirect("login.html");
}
}