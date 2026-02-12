package com.complaint.servlet;

import com.complaint.util.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class SubmitComplaintServlet extends HttpServlet {

    private String generateComplaintId(Connection con) throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM complaints");
        rs.next();
        int count = rs.getInt(1) + 1;
        return "CMP" + (1000 + count);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection con = DBConnection.getConnection()) {

            // Prevent duplicate complaint
            PreparedStatement check = con.prepareStatement(
                "SELECT * FROM complaints WHERE user_id=? AND description=?");

            check.setInt(1, userId);
            check.setString(2, description);

            ResultSet checkRs = check.executeQuery();
            if (checkRs.next()) {
                out.println("<h3>Duplicate Complaint Detected!</h3>");
                return;
            }

            String complaintId = generateComplaintId(con);

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO complaints VALUES(?,?,?,?,?,NULL,DEFAULT)");

            ps.setString(1, complaintId);
            ps.setInt(2, userId);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setString(5, "Pending");

            ps.executeUpdate();

            out.println("<h2>Complaint Submitted Successfully!</h2>");
            out.println("<h3>Your Complaint ID: " + complaintId + "</h3>");
            out.println("<a href='dashboard.html'>Back</a>");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
