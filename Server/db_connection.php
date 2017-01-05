<?php
	class MysqlDatabaseConnection {
		private $conn = null;
		
		public function __construct() {
			$this->conn = new PDO('mysql:host=localhost;dbname=d024a9e0;charset=utf8', 'd024a9e0', 'r5PdRTW8DH4VeXCC');
			$this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$this->conn->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
		}
		
		public function getConnection() {
			return $this->conn;
		}
	}