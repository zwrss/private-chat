  var links;

  var websocket;

  function randomCode()
  {
    var d = function() { return Math.floor(Math.random() * 10); }
    return "" + d() + d() + d() + d() + d() + d();
  }

  function init()
  {
    links = document.getElementById("links");

    if (window.location.protocol != 'https:' && window.location.hostname != 'localhost') {
        location.href = 'https:' + window.location.href.substring(window.location.protocol.length);
    }


    if (window.location.protocol == 'https:') {
        websocket = new WebSocket('wss://' + window.location.host);
    } else {
        websocket = new WebSocket('ws://' + window.location.host);
    }

    websocket.onopen = function(evt) { onOpen(evt) };
    websocket.onclose = function(evt) { onClose(evt) };
    websocket.onmessage = function(evt) { onMessage(evt) };
    websocket.onerror = function(evt) { onError(evt) };
    document.getElementById("send").onclick = function () {
      var un = document.getElementById("username").value
      var code = document.getElementById("code").value
      var msg = {
        code: code,
        username: un
      }
      doSend(JSON.stringify(msg));
    };
    document.getElementById("code").value = randomCode();
  }

  function onOpen(evt)
  {
    writeToScreen("CONNECTED");
  }

  function onClose(evt)
  {
    writeToScreen("DISCONNECTED");
  }

  function onMessage(evt)
  {
    writeToScreen('<span style="color: blue;"><a href="' + evt.data + '">' + evt.data +'</a></span>');
  }

  function onError(evt)
  {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
  }

  function doSend(message)
  {
    websocket.send(message);
  }

  function writeToScreen(message)
  {
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    links.appendChild(pre);
  }

  window.addEventListener("load", init, false);