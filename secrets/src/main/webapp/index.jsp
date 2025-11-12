<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="utils.HttpUtil,org.json.JSONObject,org.json.JSONArray" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Secrets App</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>
<div class="container mt-5">
    <h1>Projects</h1>
    <div id="project-list">
        <%
            String apiUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + "/v1/projects";
            String responseJson = null;
            int status = 200;
            try {
                responseJson = script_jsp.httpRequest("GET", apiUrl, null);
                status = 200;
            } catch (Exception e) {
                responseJson = null;
            }
            if (status == 401) {
                response.sendRedirect("/login");
                return;
            }
            org.json.JSONObject json = null;
            org.json.JSONArray projects = null;
            if (responseJson != null && !responseJson.isEmpty()) {
                json = new org.json.JSONObject(responseJson);
                projects = json.optJSONArray("projects");
            }
        %>
        <% if (projects != null && projects.length() > 0) { %>
        <ul class="list-group">
            <% for (int i = 0; i < projects.length(); i++) {
                org.json.JSONObject project = projects.getJSONObject(i); %>
            <li class="list-group-item">
                <strong><%= project.getString("name") %></strong> (<%= project.getString("slug") %>)<br/>
                <span><%= project.getString("description") %></span>
            </li>
            <% } %>
        </ul>
        <% } else { %>
        <em>No projects found or error fetching projects.</em>
        <% } %>
    </div>
</div>
</body>
</html>
