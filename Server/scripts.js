$(document).ready(function() {
	var updateMessages = function() {
		var $messages = $("#messages");
		
		// empty messages list
		$messages.html("Loading messages...");
		
		$.get("ajax.messages.get.php", {}, function(data) {
			var messages;
			
			try {
				messages = JSON.parse(data);
			} catch (e) {
				
			}
			
			$messages.empty();
			
			if (messages && messages.length > 0) {
				// append new messages
				messages.forEach(function(message) {
					var li = "";
					
					li += "<li class='message'>";
						// delete link
						li += "<span class='delete-msg right' data-id='" + message.id + "'><a href='#'>Delete</a></span>";
					
						// arrival
						li += "<div class='message-arrival'>" + formatTimestamp(message.arrival) + "</div>";
						
						// sender
						li += "<div class='message-sender'>" + message.sender + "</div>";
						
						// message body
						li += "<div class='message-body'><pre>" + message.message + "</pre></div>";
					li += "</li>";
					
					$messages.append(li);
				});
				
				addDeleteHandlers();
			} else {
				$("#messages").html("No messages saved yet.");
			}
		});
	};
	
	var deleteMessage = function(id) {
		$.get("ajax.messages.delete.php", { id: id }, function(response) {
			response = JSON.parse(response);
			
			if (response.status === "success") {
				updateMessages();
			} else {
				console.error("could not delete message", response.message);
			}
		});
	};
	
	var addDeleteHandlers = function() {
		$(".delete-msg").on("click", function() {
			var id = $(this).attr("data-id");
			
			if (confirm("Are you sure you want to delete this message?")) {
				deleteMessage(id);
			}
		});
	};
	
	var formatTimestamp = function(timestamp) {
		// formats a timestamp ("2017-01-05 00:00:00" -> "05.01.2017 00:00")
		
		var parts = timestamp.split(" ");
		
		var dateParts = parts[0].split("-");
		var timePart = parts[1];
		
		return dateParts[2] + "." + dateParts[1] + "." + dateParts[0] + " " + timePart.substr(0, 5);
	};
	
	updateMessages();
	
	$("#btnUpdate").on("click", function() {
		updateMessages();
	});
});