<%@page
	import="gr.ntua.vision.monitoring.ext.catalog.GlobalCatalogFactory"%>
<%@page import="gr.ntua.vision.monitoring.cluster.ClusterMonitoring"%>
<%@page import="gr.ntua.vision.monitoring.cloud.CloudMonitoring"%>
<%@page import="gr.ntua.vision.monitoring.VismoCtxListener"%>
<%@page import="org.json.JSONObject"%>
<%@page import="gr.ntua.vision.monitoring.util.Pair"%>
<%@page import="java.util.List"%>
<%@page import="com.google.common.collect.Lists"%>
<%@page import="java.util.Date"%>
<%@page import="gr.ntua.vision.monitoring.cluster.Configuration"%>
<%@page
	import="gr.ntua.vision.monitoring.ext.catalog.LocalCatalogFactory"%>
<%@page import="gr.ntua.vision.monitoring.ext.catalog.Catalog"%>
<%@page
	import="gr.ntua.vision.monitoring.ext.catalog.InMemoryLocalCatalog"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="jquery.min.js"></script>
<title>vismo control panel</title>
<script type="text/javascript">
	$(document).ready(function() {
		$("#cloudAliveButton").click(function(){
			$.get('/vismo/Monitoring/cloud/setCloudMonitoringParameter', { name:'Alive', value:$('#cloudAlive').val() }, function(data){
				alert(JSON.stringify(data));
			} );
		});
		$("#cloudCatalogButton").click(function(){
			$.get('/vismo/Monitoring/cloud/setCloudMonitoringParameter', { name:'GlobalCatalog', value:$('#cloudCatalog').val() }, function(data){
				alert(JSON.stringify(data));
			} );
		});
		$("#cloudClustersButton").click(function(){
			$.get('/vismo/Monitoring/cloud/setCloudMonitoringParameter', { name:'LocalCatalogs', value:$('#cloudClusters').val() }, function(data){
				alert(JSON.stringify(data));
			} );
		});
		$("#clusterAliveButton").click(function(){
			$.get('/vismo/Monitoring/cluster/setClusterMonitoringParameter', { name:'Alive', value:$('#clusterAlive').val() }, function(data){
				alert(JSON.stringify(data));
			} );
		});
		$("#clusterCatalogButton").click(function(){
			$.get('/vismo/Monitoring/cluster/setClusterMonitoringParameter', { name:'LocalCatalog', value:$('#clusterCatalog').val() }, function(data){
				alert(JSON.stringify(data));
			} );
		});
	});
</script>
</head>
<body>
	<div style="width: 100%; text-align: right;">
		<a href="logs.jsp">Logs</a>
	</div>
	<div style="float: left;">
		<strong>Cloud</strong>
		<table>
			<tr>
				<td>Alive:</td>
				<td><input size=35 id="cloudAlive"
					value="<%=VismoCtxListener.instance().isAlive( CloudMonitoring.class )%>">
				</td>
				<td><button id="cloudAliveButton">set</button>
			</tr>
			<tr>
				<td>Catalog:</td>
				<td><input size=35 id="cloudCatalog"
					value="<%=GlobalCatalogFactory.getGlobalURL()%>"></td>
				<td><button id="cloudCatalogButton">set</button>
			</tr>
			<tr>
				<td>Clusters:</td>
				<td><input size=35 id="cloudClusters"></td>
				<td><button id="cloudClustersButton">set</button>
			</tr>
		</table>
	</div>
	<div style="float: left;">
		<strong>Cluster</strong>
		<table>
			<tr>
				<td>Alive:</td>
				<td><input size=35 id="clusterAlive"
					value="<%=VismoCtxListener.instance().isAlive( ClusterMonitoring.class )%>">
				</td>
				<td><button id="clusterAliveButton">set</button>
			</tr>
			<tr>
				<td>Catalog:</td>
				<td><input size=35 id="clusterCatalog"
					value="<%=LocalCatalogFactory.getLocalURL()%>"></td>
				<td><button id="clusterCatalogButton">set</button>
			</tr>
		</table>
	</div>
</body>
</html>