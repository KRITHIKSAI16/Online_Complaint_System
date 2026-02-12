package com.complaint.servlet;

import com.complaint.util.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class TrackComplaintServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String complaintId = request.getParameter("complaintId");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (complaintId == null || complaintId.trim().isEmpty()) {
            out.println("<h3>Please enter a valid Complaint ID.</h3>");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM complaints WHERE complaint_id = ?"
            );

            ps.setString(1, complaintId.trim());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                out.println("<h2>Complaint Details</h2>");
                out.println("<table border='1' cellpadding='8'>");

                out.println("<tr><td><b>Complaint ID</b></td><td>"
                        + rs.getString("complaint_id") + "</td></tr>");

                out.println("<tr><td><b>Category</b></td><td>"
                        + rs.getString("category") + "</td></tr>");

                out.println("<tr><td><b>Description</b></td><td>"
                        + rs.getString("description") + "</td></tr>");

                out.println("<tr><td><b>Status</b></td><td>"
                        + rs.getString("status") + "</td></tr>");

                out.println("<tr><td><b>Remarks</b></td><td>"
                        + (rs.getString("remarks") == null
                        ? "Not Updated Yet"
                        : rs.getString("remarks"))
                        + "</td></tr>");

                out.println("<tr><td><b>Created At</b></td><td>"
                        + rs.getTimestamp("created_at") + "</td></tr>");

                out.println("</table>");

            } else {
                out.println("<h3>No Complaint Found with ID: "
                        + complaintId + "</h3>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Database Error Occurred</h3>");
        }
    }
}
