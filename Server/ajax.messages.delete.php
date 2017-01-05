<?php
	include('resources.php');
	
	$status = null;
	$message = null;
	
	// get id to delete
	if ($_GET AND $_GET['id']) {
		$id = $_GET['id'];
		
		// get DB connection
		$db = $dbc->getConnection();
		
		// build SQL query
		$sql = "DELETE FROM messages WHERE id = :id";
		$query = $db->prepare($sql);
		$success = $query->execute( array( ':id' => $id ) );
		
		if ($success !== false) {
			$status = "success";
		} else {
			$status = "error";
			$message = "Could not delete message";
		}
	} else {
		$status = "error";
		$message = "Missing mandatory parameter 'id'";
	}
	
	echo json_encode(array( "status" => $status, "message" => $message ));