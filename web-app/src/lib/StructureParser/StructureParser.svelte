<script>
	import { openedCodes, selectedArchiveFile, editorUpdateTrigger } from '$lib/stores.js';

	import antlr4 from 'antlr4';
	import { CharStreams, CommonTokenStream } from 'antlr4';
	import JavaLexer from './JavaLexer';
	import JavaParser from './JavaParser';
	import JavaParserListener from './JavaParserListener';
	import Structure from './Structure.svelte';

	let parsedOutput = {};

	class JavaListener extends JavaParserListener {
		constructor() {
			super();
			this.output = { classes: [], enums: [], interfaces: [] };
		}

		enterClassDeclaration(ctx) {
			const identifierContext = ctx.children.find(
				(child) => child.constructor.name === 'IdentifierContext'
			);
			const className = identifierContext ? identifierContext.getText() : '';
			if (className) {
				const classObj = {
					name: className,
					modifiers: this.getModifiers(ctx),
					superClass: this.getSuperClass(ctx),
					implementedInterfaces: this.getImplementedInterfaces(ctx),
					methods: [],
					fields: []
				};
				this.output.classes.push(classObj);
			}
		}

		enterInterfaceDeclaration(ctx) {
			const identifierContext = ctx.children.find(
				(child) => child.constructor.name === 'IdentifierContext'
			);
			const interfaceName = identifierContext ? identifierContext.getText() : '';
			if (interfaceName) {
				const interfaceObj = {
					name: interfaceName,
					modifiers: this.getModifiers(ctx),
					extendedInterfaces: this.getExtendedInterfaces(ctx),
					methods: []
				};
				this.output.interfaces.push(interfaceObj);
			}
		}

		enterMethodDeclaration(ctx) {
			const identifierContext = ctx.children.find(
				(child) => child.constructor.name === 'IdentifierContext'
			);
			const methodName = identifierContext ? identifierContext.getText() : '';
			const currentClassOrInterface = this.getCurrentClassOrInterface();
			if (currentClassOrInterface && methodName) {
				currentClassOrInterface.methods.push({ name: methodName });
			}
		}

		enterFieldDeclaration(ctx) {
			// Find the type of the field
			const typeContext = ctx.typeType();
			const fieldType = typeContext ? typeContext.getText() : '';

			// Extract modifiers (if any)
			const fieldModifiers = this.getModifiers(ctx);

			// Extract all variable declarators (fields can declare multiple variables of the same type)
			const variableDeclaratorsContext = ctx.variableDeclarators();
			if (variableDeclaratorsContext) {
				variableDeclaratorsContext.variableDeclarator().forEach((variableDeclaratorCtx) => {
					// Find the IdentifierContext within the variableDeclarator context
					const identifierContext = variableDeclaratorCtx
						.variableDeclaratorId()
						.children.find((child) => child.constructor.name === 'IdentifierContext');
					const variableName = identifierContext ? identifierContext.getText() : '';

					const currentClassOrInterface = this.getCurrentClassOrInterface();
					if (currentClassOrInterface && variableName) {
						currentClassOrInterface.fields.push({
							name: variableName,
							type: fieldType,
							modifiers: fieldModifiers
						});
					}
				});
			}
		}

		getModifiers(ctx) {
			let modifiers = [];
			console.log(ctx.children)
			// If not, you might need to iterate through children and find modifier nodes
			if (ctx.children) {
				ctx.children.forEach((child) => {
					if (child.constructor.name === 'ClassOrInterfaceModifierContext') {
						modifiers.push(child.getText());
					}
				});
			}

			return modifiers;
		}

		getSuperClass(ctx) {
			// Assuming ctx corresponds to a classDeclaration context
			if (ctx.EXTENDS()) {
				// Check if the EXTENDS keyword is present
				const typeTypeCtx = ctx.typeType();
				if (typeTypeCtx) {
					return typeTypeCtx.getText(); // Get the text of the typeType context, which should be the superclass
				}
			}
			return null; // Return null if no superclass is found
		}

		getImplementedInterfaces(ctx) {
			let implementedInterfaces = [];

			if (ctx.IMPLEMENTS()) {
				const typeListArray = ctx.typeList();
				if (typeListArray) {
					typeListArray.forEach((typeListCtx) => {
						if (typeListCtx.children) {
							typeListCtx.children.forEach((child) => {
								if (child.constructor.name === 'TypeTypeContext') {
									implementedInterfaces.push(child.getText());
								}
							});
						}
					});
				}
			}

			return implementedInterfaces;
		}

		getExtendedInterfaces(ctx) {
			let extendedInterfaces = [];

			if (ctx.EXTENDS()) {
				const typeListArray = ctx.typeList();
				if (typeListArray) {
					typeListArray.forEach((typeListCtx) => {
						if (typeListCtx.children) {
							typeListCtx.children.forEach((child) => {
								if (child.constructor.name === 'TypeTypeContext') {
									extendedInterfaces.push(child.getText());
								}
							});
						}
					});
				}
			}

			return extendedInterfaces;
		}

		getCurrentClassOrInterface() {
			return (
				this.output.classes[this.output.classes.length - 1] ||
				this.output.interfaces[this.output.interfaces.length - 1]
			);
		}

		getOutput() {
			return this.output;
		}
	}

	// Reactive statement to run parseJava whenever editorUpdateTrigger changes
	const getCode = editorUpdateTrigger.subscribe(() => {
		if (!$selectedArchiveFile) {
			parsedOutput = {};
			return;
		}

		const codes = $openedCodes;
		const selectedFileName = $selectedArchiveFile;
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
		height: calc(30% + 0.5px);
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
	}
</style>
