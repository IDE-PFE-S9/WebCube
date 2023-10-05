<script>
	import { onMount } from 'svelte';
	import { openedCode } from '$lib/stores.js';
	let editor;
	let editorContainer;
	let webSocket;

	const initializeWebSocket = () => {
		const url = 'ws://localhost:3000';
		webSocket = new WebSocket(url);

		webSocket.onopen = () => {
			sendInitializeMessage();
		};

		webSocket.onmessage = handleWebSocketMessage;
	};

	const sendInitializeMessage = () => {
		const initializeMessage = JSON.stringify({
			jsonrpc: '2.0',
			id: 1,
			method: 'initialize',
			params: {
				processId: null, // Assume this is available, otherwise set to null
				clientInfo: {
					name: 'WebCube',
					version: '1.0.0' // Replace with your editor's version
				},
				rootUri: 'file:///path/to/your/project', // The root URI of the workspace
				initializationOptions: {}, // Language specific initialization options
				capabilities: {
					workspace: {
						applyEdit: true,
						workspaceEdit: {
							documentChanges: true
						},
						didChangeConfiguration: {
							dynamicRegistration: true
						},
						didChangeWatchedFiles: {
							dynamicRegistration: true
						},
						symbol: {
							dynamicRegistration: true,
							symbolKind: {
								valueSet: [
									1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
									24, 25
								]
							}
						},
						executeCommand: {
							dynamicRegistration: true
						},
						configuration: true,
						workspaceFolders: true
					},
					textDocument: {
						synchronization: {
							dynamicRegistration: true,
							willSave: true,
							willSaveWaitUntil: true,
							didSave: true
						},
						completion: {
							dynamicRegistration: true,
							completionItem: {
								snippetSupport: true
							}
						},
						hover: {
							dynamicRegistration: true,
							contentFormat: ['markdown', 'plaintext']
						},
						signatureHelp: {
							dynamicRegistration: true,
							signatureInformation: {
								documentationFormat: ['markdown', 'plaintext']
							}
						},
						definition: {
							dynamicRegistration: true
						},
						typeDefinition: {
							dynamicRegistration: true
						},
						implementation: {
							dynamicRegistration: true
						},
						references: {
							dynamicRegistration: true
						},
						documentHighlight: {
							dynamicRegistration: true
						},
						documentSymbol: {
							dynamicRegistration: true
						},
						codeAction: {
							dynamicRegistration: true
						},
						codeLens: {
							dynamicRegistration: true
						},
						documentLink: {
							dynamicRegistration: true
						},
						colorProvider: {
							dynamicRegistration: true
						},
						formatting: {
							dynamicRegistration: true
						},
						rangeFormatting: {
							dynamicRegistration: true
						},
						onTypeFormatting: {
							dynamicRegistration: true
						},
						rename: {
							dynamicRegistration: true
						},
						foldingRange: {
							dynamicRegistration: true
						}
					}
				},
				trace: 'off',
				workspaceFolders: [
					{
						uri: 'file:///path/to/your/project',
						name: 'WebCube'
					}
				] // Initial configured workspace folders
			}
		});
		webSocket.send(JSON.stringify(initializeMessage));
	};

	const handleWebSocketMessage = (event) => {
		// Split the message by double newline to separate headers from body
		console.log('Received message from LSP server:', event.data);
		const [headers, body] = event.data.split('\r\n\r\n');
		// Now `body` should contain just the JSON-RPC message

		if (body && body.length > 0) {
			// Check if body exists and is not empty
			try {
				const message = JSON.parse(body);

				if (message.method === 'window/logMessage') {
					// Handle log messages
					console.log('Log message from LSP server:', message.params.message);

				} else if (message.method === 'textDocument/publishDiagnostics') {
					// Handle diagnostics messages
					const diagnostics = message.params.diagnostics;
					// TODO: Display diagnostics in the editor
				}
				// ... handle other message types
			} catch (e) {
				console.error('Failed to parse JSON message:', e);
				console.error('Raw message body:', body); // Log raw body data
			}
		} else {
			console.error('Empty or undefined message body received:', body);
		}
	};

	const updateOpenedCode = () => {
		const newValue = editor.getValue();
		openedCode.set(newValue);
	};

	const handleEditorContentChange = () => {
		updateOpenedCode();
		sendDidOpenMessage();
	};

	const sendDidOpenMessage = () => {
		if (webSocket.readyState !== WebSocket.OPEN) {
			console.error('WebSocket is not open: readyState is ', webSocket.readyState);
			return;
		}
		const didOpenMessage = {
			jsonrpc: '2.0',
			method: 'textDocument/didOpen',
			params: {
				textDocument: {
					uri: 'file:///path/to/your/document.java',
					languageId: 'java',
					version: 1,
					text: editor.getValue()
				}
			}
		};
		webSocket.send(JSON.stringify(didOpenMessage));
	};

	const handleStoreSubscription = (value) => {
		if (editor && value !== editor.getValue()) {
			editor.setValue(value ? value.toString() : '');
		}
	};

	onMount(async () => {
		const monaco = await import('monaco-editor');

		editor = monaco.editor.create(editorContainer, {
			value: '',
			language: 'java',
			theme: 'vs-dark',
			automaticLayout: true
		});

		initializeWebSocket();

		editor.onDidChangeModelContent(handleEditorContentChange);

		const unsubscribe = openedCode.subscribe(handleStoreSubscription);

		return () => {
			unsubscribe();
			webSocket.close();
		};
	});
</script>

<div id="editor-container" bind:this={editorContainer} />

<style lang="scss">
	#editor-container {
		height: 70%;
		width: 100%;
	}
</style>
