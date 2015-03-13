<%@taglib 
 	prefix="c"
	uri="http://java.sun.com/jsp/jstl/core"  
%>

<!DOCTYPE html>

<html lang="en">
	<head>
		<meta charset=UTF-8">
		<title>Assignment 01 - Anagram System</title>
	</head>
	
	<body>
		<!-- if the user is logged in then we need to render one version of the page
			consequently if the user is logged out we need to render a different version of the page -->
			
		<c:choose>
			<c:when test="${user != null}">
				<p> Welcome ${user.email}<br/>
				You can signout <a href="${logout_url} }">here</a><br/>
				
				<p><b>${feedback}</b></p>

				<p>
				<c:forEach var="word" items="${list}">
				    <c:out value="${word}"/><br/> 
				</c:forEach>
				</p>
				
				<br/>
				<form action="/AnagramServlet" method="get">
					Search Anagram: <input type="text" name="search_input">
					<input type="submit" >
				</form>	
				 
				<form action="/AnagramServlet" method="post">
					Add Anagram....: <input type="text" name="add_input">
					<input type="submit" >
				</form>	
				
			</c:when>
			<c:otherwise>
				<p>Welcome <a href="${login_url}">Sign in or register</a></p>
			</c:otherwise>
		</c:choose>
			
		
	</body>
</html>