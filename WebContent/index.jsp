<%@page import="gr.ntua.vision.monitoring.util.Pair"%>
<%@page import="java.util.List"%>
<%@page import="com.google.common.collect.Lists"%>
<%@page import="java.util.Date"%>
<%@page import="gr.ntua.vision.monitoring.cluster.Configuration"%>
<%@page import="gr.ntua.vision.monitoring.ext.local.LocalCatalogFactory"%>
<%@page import="gr.ntua.vision.monitoring.ext.local.Catalog"%>
<%@page
	import="gr.ntua.vision.monitoring.ext.local.InMemoryLocalCatalog"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	Logs so far:
	<br />
	<div style="border: 2px; border-color: black;">
		<pre>
		<%
			Catalog ctg = (Catalog) getServletContext().getAttribute( "lcl-store" );

			out.println( "--------------------------------" );
			out.println( "\tMEMORY" );
			out.println( "--------------------------------" );

			List<Pair<String, Object>> items = Lists.newArrayList();
			ctg.timeRange( "vismo.memory", 0, new Date().getTime(), items );

			out.println( items.size() );

			for( Pair<String, Object> pair : items )
				out.println( pair.a + " :: " + pair.b );

			out.println( "--------------------------------" );
			out.println( "\tLOAD" );
			out.println( "--------------------------------" );

			items.clear();
			ctg.timeRange( "vismo.load", 0, new Date().getTime(), items );

			out.println( items.size() );

			for( Pair<String, Object> pair : items )
				out.println( pair.a + " :: " + pair.b );
		%>
		</pre>
	</div>
</body>
</html>