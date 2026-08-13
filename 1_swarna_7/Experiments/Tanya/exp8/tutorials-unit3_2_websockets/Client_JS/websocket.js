var ws;

function connect() {
    var username = document.getElementById("username").value.trim();
    var interest = document.getElementById("interest").value.trim();
    var wsserver = document.getElementById("wsserver").value.trim();

    // Combine into full URL ws://localhost:8080/chat/art/username
    var url = wsserver + interest + "/" + username;

    ws = new WebSocket(url);

    ws.onopen = function(event) {
        var log = document.getElementById("log");
        log.value += "Connected to " + event.currentTarget.url + "\n";
    };

    ws.onmessage = function(event) {
        console.log(event.data);
        var log = document.getElementById("log");
        log.value += "Message from server: " + event.data + "\n";
    };
}

function send() {
    var content = document.getElementById("msg").value;
    ws.send(content);
    document.getElementById("msg").value = "";
}

