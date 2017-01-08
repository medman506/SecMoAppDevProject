<?php
	// Unix timestamp
	class UnixTimestamp {
		private $unix;
		
		private $unixAsDateTime;
		
		public function __construct ($unix = 0) {
			$this->setUnix($unix);
			
			$this->unixAsDateTime = new DateTime(date('Y-m-d', $this->unix));
		}
		
		public function convert2MysqlDate () {
			return date('Y-m-d', $this->unix);
		}
		
		public function convert2MysqlDateTime () {
			return date('Y-m-d H:i:s', $this->unix);
		}
		
		public function now () {
			return mktime();
		}
		
		private function getFractureFromSeconds ($seconds) {
			return $seconds / 86400;
		}
		
		public function setUnix ($unix) {
			$this->unix = $unix;
		}
		
		public function getUnix () {
			return $this->unix;
		}
	}