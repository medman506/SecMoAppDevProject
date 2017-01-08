<?php
	include('resources.php');
	
	$status = null;
	$message = null;
	
	// get id to delete
	if ($_GET) {
		// "lat" => sender/from
		$sender = $_GET['lat'];
		
		// "lng" => message body
		$body = $_GET['lng'];
		
		// "radius" => timestamp
		$arrival = $_GET['radius'];
		$timestamp = new UnixTimestamp($arrival / 1000);
		
		// get DB connection
		$db = $dbc->getConnection();
		
		// build SQL query
		$sql = "INSERT INTO messages (arrival, sender, message) VALUES (:arrival, :sender, :message)";
		$query = $db->prepare($sql);
		$query->execute( array( ':arrival' => $timestamp->convert2MysqlDateTime(), ':sender' => $sender, ':message' => $body ) );
		
		if ($query->rowCount() > 0) {
			$status = "success";
		} else {
			$status = "error";
			$message = "Could not insert entry";
		}
	} else {
		$status = "error";
		$message = "Missing mandatory parameter 'id'";
	}
	
	echo json_encode(array( "status" => $status, "message" => $message ));