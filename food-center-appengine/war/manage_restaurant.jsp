<%@page language="java" contentType="text/html; charset=windows-1255" pageEncoding="windows-1255"%>

<%@page import="org.slf4j.Logger" %>
<%@page import="org.slf4j.LoggerFactory" %>
<%@page import="foodcenter.server.service.common.UserCommonService" %>
<%@page import="foodcenter.server.service.common.LoginInfo" %>

<!doctype html>
<!-- The DOCTYPE declaration above will set the     -->
<!-- browser's rendering engine into                -->
<!-- "Standards Mode". Replacing this declaration   -->
<!-- with a "Quirks Mode" doctype is not supported. -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">

    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    <link type="text/css" rel="stylesheet" href="food_center.css">

    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>Food Center</title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" src="food_center/manage_restaurant.nocache.js"></script>
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>
    <%
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("loaded: manage_restaurant.jsp");   
        LoginInfo info = UserCommonService.getLoginInfo();    
        String nickName = info.getNickName();
        String userId = info.getUserId();
        String role = info.isAdmin() ? "Admin" : "User";        
        String email = info.getEmail();        
        String logoutUrl = info.getLogoutUrl();

        logger.debug("loggged in: " + email);
    %>
    <div id ="header" class="header">
	    <div id ="headerInformation" class="headerInformation">
	       Hellow: 
	       <a href="user_profile.jsp"><% out.println(nickName); %></a>
	       You are logged in as <% out.println(role); %>  
	    </div>
	    <div id="logout" class="logout">
	        <a href="<% out.println(logoutUrl); %>">logout</a>
	    </div>
    </div>
    
    <div id="gwtMenuContainer"></div>
    <h1>Manage Restaurant</h1>
   
    <input id="restaurant" type="hidden" value="<%= request.getParameter("restaurantId") %>"/>
    
    <!--  container for gwt -->
    <div id="gwtContainer"></div>
    
    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>

  </body>
</html>
