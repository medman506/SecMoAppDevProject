<?php
	// require source files
	require_once('db_connection.php');

	// Default time zone
	date_default_timezone_set("Europe/Vienna");
	
	// Execution time limit
	set_time_limit(0);
	
	// Init database connection_aborted
	$dbc = new MysqlDatabaseConnection();