<script>
	import { openedCodes, selectedFile, editorUpdateTrigger } from '$lib/stores.js';

	import antlr4 from 'antlr4';
	import { CharStreams, CommonTokenStream } from 'antlr4';
	import JavaLexer from './JavaLexer';
	import JavaParser from './JavaParser';
	import JavaParserListener from './JavaParserListener';
	import Structure from './Structure.svelte';

	let parsedOutput = {};

	let showDetails = {}; // Object to keep track of which class details are shown
	function toggleDetails(index) {
		showDetails[index] = !showDetails[index];
	}

	class JavaListener extends JavaParserListener {
		constructor() {
			super();
			this.output = { classes: [], enums: [], interfaces: [] };
		}

		enterClassDeclaration(ctx) {
			// Search through the children array for an instance of IdentifierContext.
			const identifierContext = ctx.children.find(
				(child) => child.constructor.name === 'IdentifierContext'
			);
			// If IdentifierContext is found, retrieve the text of the identifier.
			const className = identifierContext ? identifierContext.getText() : '';
			// If className is retrieved, push a new object representing the class to the classes array.
			if (className) {
				const classObj = { name: className, methods: [], fields: [] };
				this.output.classes.push(classObj);
			}
		}

		enterEnumDeclaration(ctx) {
			const identifierContext = ctx.children.find(
				(child) => child.constructor.name === 'IdentifierContext'
			);
			const enumName = identifierContext ? identifierContext.getText() : '';
			if (enumName) {
				this.output.enums.push({ name: enumName, methods: [], fields: [] });
			}
		}

		enterEnumConstant(ctx) {
			const identifierContext = ctx.children.find(
				(child) => child.constructor.name === 'IdentifierContext'
			);
			const fieldName = identifierContext ? identifierContext.getText() : '';
			if (fieldName) {
				const currentEnum = this.output.enums[this.output.enums.length - 1];
				currentEnum.fields.push({ name: fieldName });
			}
		}

		enterInterfaceDeclaration(ctx) {
			const identifierContext = ctx.children.find(
				(child) => child.constructor.name === 'IdentifierContext'
			);
			const interfaceName = identifierContext ? identifierContext.getText() : '';
			if (interfaceName) {
				this.output.interfaces.push({ name: interfaceName, methods: [], fields: [] });
			}
		}

		enterInterfaceMethodDeclaration(ctx) {
			const identifierContext = ctx.children.find(
				(child) => child.constructor.name === 'IdentifierContext'
			);
			const methodName = identifierContext ? identifierContext.getText() : '';
			if (methodName) {
				const currentInterface = this.output.interfaces[this.output.interfaces.length - 1];
				currentInterface.methods.push({ name: methodName });
			}
		}

		enterMethodDeclaration(ctx) {
			// Search through the children array for an instance of IdentifierContext.
			const identifierContext = ctx.children.find(
				(child) => child.constructor.name === 'IdentifierContext'
			);
			// If IdentifierContext is found, retrieve the text of the identifier.
			const methodName = identifierContext ? identifierContext.getText() : '';

			// Ensure there is a current class, enum, or interface
			const currentClass = this.output.classes[this.output.classes.length - 1];
			const currentEnum = this.output.enums[this.output.enums.length - 1];
			const currentInterface = this.output.interfaces[this.output.interfaces.length - 1];

			// Determine the current container (class, enum, or interface)
			const currentContainer = currentClass || currentEnum || currentInterface;

			if (currentContainer) {
				// If methodName is retrieved, push a new object representing the method to the methods array.
				if (methodName) {
					currentContainer.methods = currentContainer.methods || [];
					currentContainer.methods.push({ name: methodName });
				}
			} else {
				console.error('No current class, enum, or interface found for method declaration');
			}
		}

		enterFieldDeclaration(ctx) {
			// Find VariableDeclaratorsContext among the children
			const variableDeclaratorsContext = ctx.children.find(
				(child) => child.constructor.name === 'VariableDeclaratorsContext'
			);

			if (variableDeclaratorsContext) {
				// Assume the first child of VariableDeclaratorsContext is the field identifier
				// This assumption may need to be adjusted based on the actual structure
				const fieldIdentifierContext = variableDeclaratorsContext.children[0];

				if (fieldIdentifierContext) {
					// Assume getText method will provide the field name
					// This assumption may need to be adjusted based on the actual structure
					const fieldName = fieldIdentifierContext.getText();

					// Ensure there is a current class, enum, or interface
					const currentClass = this.output.classes[this.output.classes.length - 1];
					const currentEnum = this.output.enums[this.output.enums.length - 1];
					const currentInterface = this.output.interfaces[this.output.interfaces.length - 1];

					// Determine the current container (class, enum, or interface)
					const currentContainer = currentClass || currentEnum || currentInterface;

					if (currentContainer) {
						// If fieldName is retrieved, push a new object representing the field to the fields array.
						if (fieldName) {
							currentContainer.fields = currentContainer.fields || [];
							currentContainer.fields.push({ name: fieldName });
						}
					} else {
						console.error('No current class, enum, or interface found for field declaration');
					}
				}
			}
		}

		getOutput() {
			return this.output;
		}
	}

	// Reactive statement to run parseJava whenever editorUpdateTrigger changes
	const getCode = editorUpdateTrigger.subscribe(() => {
		if (!$selectedFile) {
			parsedOutput = {};
			return;
		}
		const codes = $openedCodes;
		const selectedFileName = $selectedFile?.name;
		const fileExtension = selectedFileName?.split('.').pop();


		if (fileExtension === 'java') {
			const codeObj = codes.find((code) => code.name === selectedFileName);
			const chars = new CharStreams.fromString(codeObj.code);
			const lexer = new JavaLexer(chars);
			const tokens = new CommonTokenStream(lexer);
			const parser = new JavaParser(tokens);

			// Parse the input
			const tree = parser.compilationUnit();

			// Walk the tree with the listener
			const listener = new JavaListener();
			antlr4.tree.ParseTreeWalker.DEFAULT.walk(listener, tree);

			parsedOutput = listener.getOutput();
			console.log(parsedOutput);
		} else {
			parsedOutput = {};
		}
	});
</script>

<div class="structure-parser">
	<div id="title-container">
		<h2 id="title">Structure</h2>
	</div>
	<Structure structure={parsedOutput} />
</div>

<style lang="scss">
	.structure-parser {
		display: flex;
		flex-direction: column;
		background-color: rgb(37, 37, 38);
		height: 30%;
		min-width: 20rem;
		border-top: 1px solid rgb(65, 65, 65);

		overflow: scroll;

		&::-webkit-scrollbar {
			display: none;
		}

		#title-container {
			align-self: self-start;
			#title {
				padding-left: 1rem;
				font-size: 0.7rem;
				text-transform: uppercase;
				font-weight: 300;
				color: rgb(187, 187, 187);
			}
		}

		.class-name,
		.field,
		.method {
			font-family: 'Courier New', monospace;
			margin-left: 20px;
		}

		.collapsible {
			display: none;
		}
	}
</style>
