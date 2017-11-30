const express = require('express');
const http = require('http');
const url = require('url');
const WebSocket = require('ws');

const path = require('path');

const PORT = process.env.PORT || 3000;
const INDEX = path.join(__dirname, 'index.html');

const server = express()
  .use((req, res) => res.send({ msg: "hello" }) )
  .listen(PORT, () => console.log(`Listening on ${ PORT }`));

const wss = new WebSocket.Server({ server });

wss.broadcast = function broadcast(message) {
    wss.clients.forEach(function each(client) {
    if (client.readyState === WebSocket.OPEN) {
        client.send(message);
    }
    });
};

wss.on('connection', function connection(ws, req) {
  let isFirstMessage = true;
  let userName = "";
  const location = url.parse(req.url, true);
  // You might use location.query.access_token to authenticate or share sessions
  // or req.headers.cookie (see http://stackoverflow.com/a/16395220/151312)

  ws.on('message', function incoming(message) {
    console.log('received: %s', message);    
    if (isFirstMessage) {
        isFirstMessage = false;
        userName = message
    } else {
        // Broadcast to all.
        wss.broadcast(userName+ ": " +message)        
    }
  });

  ws.send('Welcome to chat');
});



