package com.complaint.servlet;

import com.complaint.util.DBConnection;
import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection con = DBConnection.getConnection()) {

            // 🔹 First check USERS table
            PreparedStatement userPs = con.prepareStatement(
                "SELECT * FROM users WHERE email=? AND password=?");

            userPs.setString(1, email);
            userPs.setString(2, password);

            ResultSet userRs = userPs.executeQuery();

            if (userRs.next()) {

                HttpSession session = request.getSession();
                session.setAttribute("userId", userRs.getInt("user_id"));
                session.setAttribute("userType", "USER");

                response.sendRedirect("dashboard.html");
                return;
            }

            // 🔹 Then check ADMINS table
            PreparedStatement adminPs = con.prepareStatement(
                "SELECT * FROM admins WHERE username=? AND password=?");

            adminPs.setString(1, email);   // using email field as username
            adminPs.setString(2, password);

            ResultSet adminRs = adminPs.executeQuery();

            if (adminRs.next()) {

                HttpSession session = request.getSession();
                session.setAttribute("adminId", adminRs.getInt("admin_id"));
                session.setAttribute("userType", "ADMIN");

                response.sendRedirect("admin_dashboard.html");
                return;
            }

            // If neither matched
            out.println("<h3>Invalid Email or Password</h3>");
            out.println("<a href='login.html'>Try Again</a>");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
