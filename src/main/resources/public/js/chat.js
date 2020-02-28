
  var messages;

  var myName = 'zwrss';

  var websocket;

  var key = '';

  function findGetParameter(parameterName) {
    var result = null,
        tmp = [];
    location.search
        .substr(1)
        .split("&")
        .forEach(function (item) {
          tmp = item.split("=");
          if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
        });
    return result;
  }

  function init() {
    messages = document.getElementById("messages");

    if (window.location.protocol != 'https:' && window.location.hostname != 'localhost') {
        location.href = 'https:' + window.location.href.substring(window.location.protocol.length);
    }



    if (window.location.protocol == 'https:') {
        websocket = new WebSocket('wss://' + window.location.host + '/chat?token=' + findGetParameter('token'));
    } else {
        websocket = new WebSocket('ws://' + window.location.host + '/chat?token=' + findGetParameter('token'));
    }

    websocket.onopen = function(evt) { onOpen(evt) };
    websocket.onclose = function(evt) { onClose(evt) };
    websocket.onmessage = function(evt) { onMessage(evt) };
    websocket.onerror = function(evt) { onError(evt) };
    document.getElementById("send-message-btn").onclick = function () {
      var msg = document.getElementById("send-message-input").value;
      document.getElementById("send-message-input").value = "";
      doSend(msg);
    };
    document.getElementById("send-message-input").addEventListener("keyup", function (event) {
      event.preventDefault();
      if (event.keyCode === 13) {
        document.getElementById("send-message-btn").click();
      }
    });
  }

  function onOpen(evt) {
    writeMessage('System', 'system', 'black', 'WebSocket connected to chat service!');
  }

  function onClose(evt) {
    writeMessage('System', 'system', 'black', 'WebSocket disconnected from chat service!');
  }

  function onMessage(evt) {

    console.log('Got message: ');
    console.log(evt);
    if (key == '') {
        key = evt.data;
    } else {
        var message = decrypt(evt.data);
    //    var message = JSON.parse(evt.data);

        var direction = 'incoming';
        var color = 'red';

        if (!message.hasOwnProperty('user')) { message.user = 'System'; direction = 'system'; color = 'black'; }
        if (message.user == myName) { direction = 'outgoing'; }

        writeMessage(message.user, direction, color, message.message);
    }
  }

  function onError(evt) {
    console.log('Got message: ');
    console.log(evt);
    writeMessage("System", "system", "black", evt.data);
  }

  function doSend(message) {
    console.log('Sending message: ');
    console.log(message);
    websocket.send(message);
  }

  function currentTime() {

    var today = new Date();

    var dd = today.getDate();
    var mm = today.getMonth() + 1;
    var yyyy = today.getFullYear();
    var hh = today.getHours();
    var MM = today.getMinutes();

    if (dd < 10) { dd = '0' + dd; }
    if (mm < 10) { mm = '0' + mm; }
    if (hh < 10) { hh = '0' + hh; }
    if (MM < 10) { MM = '0' + MM; }

    return yyyy + '-' + mm + '-' + dd + ' ' + hh + ':' + MM;
  }

  function writeMessage(user, direction, color, message) {

    var eIcon = document.createElement("i");
    eIcon.className = "fas fa-user-secret fa-3x color-" + color;

    var eUserName = document.createElement("h4");
    eUserName.className = "message-name";
    eUserName.innerHTML = user;

    var eUserContainer = document.createElement("div");
    eUserContainer.className = "col-xs-8 col-md-6";
    eUserContainer.appendChild(eIcon);
    eUserContainer.appendChild(eUserName);

    var eDate = document.createElement("div");
    eDate.className = "col-xs-4 col-md-6 text-right message-date";
    eDate.innerHTML = currentTime();

    var eHeaderRow = document.createElement("div");
    eHeaderRow.className = "row";
    eHeaderRow.appendChild(eUserContainer);
    eHeaderRow.appendChild(eDate);

    var eMessageRow = document.createElement("div");
    eMessageRow.className = "row message-text";
    eMessageRow.innerHTML = message;

    var eMessage = document.createElement("div");
    eMessage.className = "message message-" + direction + " mx-auto";
    eMessage.appendChild(eHeaderRow);
    eMessage.appendChild(eMessageRow);

    document.getElementById("messages").appendChild(eMessage);

    document.getElementById("send-message-input").scrollIntoView();

  }

  window.addEventListener("load", init, false);