package com.complaint.servlet;

import com.complaint.util.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class AdminUpdateServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // Optional: session protection
        HttpSession session = request.getSession(false);

        if (session == null || !"ADMIN".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.html");
            return;
        }

        String id = request.getParameter("id");
        String status = request.getParameter("status");
        String remarks = request.getParameter("remarks");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (id == null || id.trim().isEmpty()) {
            out.println("<h3>Complaint ID is required</h3>");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "UPDATE complaints SET status=?, remarks=? WHERE complaint_id=?"
            );

            ps.setString(1, status);
            ps.setString(2, remarks);
            ps.setString(3, id.trim());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                out.println("<h2>Complaint Updated Successfully!</h2>");
            } else {
                out.println("<h3>No Complaint Found with ID: " + id + "</h3>");
            }

            out.println("<br><a href='admin_dashboard.html'>Back to Admin Panel</a>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error updating complaint.</h3>");
        }
    }
}
