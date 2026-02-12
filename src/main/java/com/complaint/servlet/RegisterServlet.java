package com.complaint.servlet;

import com.complaint.util.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(name,email,password) VALUES(?,?,?)"
            );

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();

            out.println("<h2>Registration Successful!</h2>");
            out.println("<a href='login.html'>Login Here</a>");

        } catch (SQLIntegrityConstraintViolationException e) {
            out.println("<h3>Email already registered!</h3>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error occurred!</h3>");
        }
    }
}
