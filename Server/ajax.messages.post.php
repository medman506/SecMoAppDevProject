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
			$message = array( "results" => array(
				array("location" => array("lat" => 47.0897328, "lng" => 15.4100455), "name" => "Polizeiinspektion Graz - Wiener Straße" ),
				array("location" => array("lat" => 47.1033302, "lng" => 15.4229339), "name" => "Polizeiinspektion Graz-Andritz" ),
				array("location" => array("lat" => 47.0765347, "lng" => 15.4287876), "name" => "Polizeiinspektion Graz-Lendplatz" ),
				array("location" => array("lat" => 47.0727907, "lng" => 15.4170933), "name" => "Polizeiinspektion Graz - Hauptbahnhof" ),
				array("location" => array("lat" => 47.07530619999999, "lng" => 15.4405424), "name" => "Polizeiinspektion Paulustorgasse 8, Graz" ),
			) );
		} else {
			$status = "error";
			$message = "Could not insert entry";
		}
	} else {
		$status = "error";
		$message = "Missing mandatory parameter 'id'";
	}
	
	echo json_encode(array( "status" => $status, "message" => $message ));