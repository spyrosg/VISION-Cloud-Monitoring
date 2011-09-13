<%@page import="org.json.JSONObject"%>
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
		<div style="float: left; height: 800px; width: 50%; overflow: auto;">
		<strong>Measurements</strong>
		<pre>
		<%
			Catalog ctg = (Catalog) getServletContext().getAttribute( "lcl-store" );

			List<Pair<String, Object>> items = Lists.newArrayList();
			ctg.timeRange( "vismo.measurements", new Date().getTime() - 10 * 60 * 1000, new Date().getTime(), items );

			out.println( items.size() );

			for( Pair<String, Object> pair : items )
				out.println( pair.a + " :: \n" + new JSONObject( pair.b.toString() ).toString( 10 ) );
		%>
		</pre>
		</div>
		<div style="float: left; height: 800px; width: 50%; overflow: auto;">
		<strong>Actions</strong>
		<pre>
		<%
			items.clear();
			ctg.timeRange( "vismo.actions", new Date().getTime() - 10 * 60 * 1000, new Date().getTime(), items );

			out.println( items.size() );

			for( Pair<String, Object> pair : items )
				out.println( pair.a + " :: \n" + new JSONObject( pair.b.toString() ).toString( 10 ) );
		%>
		</pre>
		</div>
	</div>
</body>
</html>