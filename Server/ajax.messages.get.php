<?php
	include('resources.php');
	
	// get DB connection
	$db = $dbc->getConnection();
	
	// build SQL query
	$sql = "SELECT * FROM messages ORDER BY arrival DESC";
	$query = $db->prepare($sql);
	$query->execute();
	
	// function to sanitize the array elements (output encoding)
	function sanitize($s) {
		$sanitized = array();
		
		$keys = array_keys($s);
		
		foreach ($keys as $key) {
			$sanitized[$key] = htmlspecialchars($s[$key]);
		}
		
		return $sanitized;
	}
	
	$return_data = array();
	
	if ($query->rowCount() > 0) {
		$data = $query->fetchAll(PDO::FETCH_ASSOC);
		
		// sanitize array elements
		$return_data = array_map('sanitize', $data);
	}
	
	echo json_encode($return_data);