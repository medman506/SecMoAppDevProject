<?php
	include('resources.php');
	
	// get DB connection
	$db = $dbc->getConnection();
	
	// build SQL query
	$sql = "SELECT * FROM messages ORDER BY arrival DESC";
	$query = $db->prepare($sql);
	$query->execute();
	
	$data = null;
	
	if ($query->rowCount() > 0) {
		$data = $query->fetchAll(PDO::FETCH_ASSOC);
	} else {
		$data = array();
	}
	
	echo json_encode($data);