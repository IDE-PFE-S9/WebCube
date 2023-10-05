const WebSocket = require('ws');
const net = require('net');

const wsServer = new WebSocket.Server({ port: 3000 });  // WebSocket server listening on port 3000

wsServer.on('connection', (ws) => {
    console.log('WebSocket Client Connected');

    const lspSocket = net.connect({ port: 5036, host: '127.0.0.1' }, () => {
        console.log('Connected to LSP server');
    });

    // Relay messages from WebSocket to LSP server
    ws.on('message', (message) => {
        console.log('Received from Client:', message);
        lspSocket.write(message);
    });

    // Relay messages from LSP server to WebSocket
    lspSocket.on('data', (data) => {
        console.log('Received from LSP server:', data.toString());
        ws.send(data.toString());
    });

    // Handle close events
    ws.on('close', () => {
        console.log('WebSocket Client Disconnected');
        lspSocket.end();
    });

    lspSocket.on('close', () => {
        console.log('LSP server Disconnected');
        ws.close();
    });

    // Handle error events
    ws.on('error', (error) => {
        console.error('WebSocket Error:', error);
    });

    lspSocket.on('error', (error) => {
        console.error('LSP Socket Error:', error);
    });
});

console.log('WebSocket proxy server running on ws://localhost:3000');
