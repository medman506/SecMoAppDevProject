<?php
	class MysqlDatabaseConnection {
		private $conn = null;
		
		public function __construct() {
			$this->conn = new PDO('mysql:host=localhost;dbname=emal;charset=utf8', 'root', '');
			$this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			$this->conn->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
		}
		
		public function getConnection() {
			return $this->conn;
		}
	}