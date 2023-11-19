<script>
	import mermaid from 'mermaid';
	import antlr4 from 'antlr4';
	import { CharStreams, CommonTokenStream } from 'antlr4';
	import JavaLexer from '$lib/StructureParser/JavaLexer';
	import JavaParser from '$lib/StructureParser/JavaParser';
	import JavaParserListener from '$lib/StructureParser/JavaParserListener';
	import { openedArchive } from '$lib/stores';

	let parsedOutput = { classes: [], enums: [], interfaces: [] };

	class JavaListener extends JavaParserListener {
		constructor() {
			super();
			this.output = { classes: [], enums: [], interfaces: [] };
			this.currentContextStack = [];
			this.modifierStack = [];
		}

		enterClassDeclaration(ctx) {
			const className = this.extractName(ctx);
			const classObj = {
				name: className,
				superClass: this.getSuperClass(ctx),
				implementedInterfaces: this.getImplementedInterfaces(ctx),
				methods: [],
				fields: []
			};
			this.output.classes.push(classObj);
			this.currentContextStack.push(classObj);
		}

		exitClassDeclaration(ctx) {
			this.currentContextStack.pop();
		}

		enterInterfaceDeclaration(ctx) {
			const interfaceName = this.extractName(ctx);
			const interfaceObj = {
				name: interfaceName,
				extendedInterfaces: this.getExtendedInterfaces(ctx),
				methods: []
			};
			this.output.interfaces.push(interfaceObj);
			this.currentContextStack.push(interfaceObj);
		}

		exitInterfaceDeclaration(ctx) {
			this.currentContextStack.pop();
		}

		enterFieldDeclaration(ctx) {
			const fieldType = ctx.typeType().getText();
			const fieldName = this.extractName(
				ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId()
			);
			const fieldObj = {
				type: fieldType,
				name: fieldName,
				modifiers: [...this.modifierStack]
			};
			this.modifierStack = [];
			this.getCurrentContext().fields.push(fieldObj);
		}

		existFieldDeclaration(ctx) {
			// This method can be left empty if no specific action is needed on exit
		}

		enterConstructorDeclaration(ctx) {
			let parametersList = ctx.formalParameters().formalParameterList()?.formalParameter();
			if (parametersList) {
				parametersList = parametersList.map((parameter) => {
					return {
						type: parameter.typeType().getText(),
						name: parameter.variableDeclaratorId().getText()
					};
				});
			} else {
				parametersList = [];
			}
			const methodObj = {
				name: ctx.identifier().getText(),
				returnType: ctx.identifier().getText(),
				args: parametersList,
				modifiers: [...this.modifierStack]
			};
			this.modifierStack = [];
			this.getCurrentContext().methods.push(methodObj);
		}

		enterMethodDeclaration(ctx) {
			let parametersList = ctx.formalParameters().formalParameterList()?.formalParameter();
			if (parametersList) {
				parametersList = parametersList.map((parameter) => {
					return {
						type: parameter.typeType().getText(),
						name: parameter.variableDeclaratorId().getText()
					};
				});
			} else {
				parametersList = [];
			}
			const methodObj = {
				name: ctx.identifier().getText(),
				returnType: ctx.typeTypeOrVoid().getText(),
				args: parametersList,
				modifiers: [...this.modifierStack]
			};
			this.modifierStack = [];
			this.getCurrentContext().methods.push(methodObj);
		}

		exitMethodDeclaration(ctx) {
			// This method can be left empty if no specific action is needed on exit
		}

		enterInterfaceMethodDeclaration(ctx) {
			let parametersList = ctx
				.getChild(0)
				.formalParameters()
				.formalParameterList()
				?.formalParameter();
			if (parametersList) {
				parametersList = parametersList.map((parameter) => {
					return {
						type: parameter.typeType().getText(),
						name: parameter.variableDeclaratorId().getText()
					};
				});
			} else {
				parametersList = [];
			}
			const methodObj = {
				name: ctx.getChild(0).identifier().getText(),
				returnType: ctx.getChild(0).typeTypeOrVoid().getText(),
				args: parametersList,
				modifiers: [...this.modifierStack]
			};
			this.modifierStack = [];
			this.getCurrentContext().methods.push(methodObj);
		}

		enterModifier(ctx) {
			this.modifierStack.push(ctx.getText());
		}

		enterEnumDeclaration(ctx) {
			const enumObj = {
				name: ctx.identifier().getText(),
				fields: []
			};
			this.output.enums.push(enumObj);
			this.currentContextStack.push(enumObj);
		}

		exitEnumDeclaration(ctx) {
			this.currentContextStack.pop();
		}

		enterEnumConstant(ctx) {
			const fieldObj = {
				name: ctx.identifier().getText()
			};
			this.getCurrentContext().fields.push(fieldObj);
		}

		// Utility methods
		extractName(ctx) {
			const identifierContext = ctx.children.find(
				(child) => child.constructor.name === 'IdentifierContext'
			);
			return identifierContext ? identifierContext.getText() : '';
		}

		getSuperClass(ctx) {
			if (ctx.EXTENDS()) {
				const typeTypeCtx = ctx.typeType();
				if (typeTypeCtx) {
					return typeTypeCtx.getText();
				}
			}
			return null;
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

		getCurrentContext() {
			return this.currentContextStack.length > 0
				? this.currentContextStack[this.currentContextStack.length - 1]
				: null;
		}

		getOutput() {
			return this.output;
		}
	}

	function traverseAndParse(node, parsedOutput) {
		if (node.type === 'file' && node.name.endsWith('.java')) {
			const chars = new CharStreams.fromString(node.data);
			const lexer = new JavaLexer(chars);
			const tokens = new CommonTokenStream(lexer);
			const parser = new JavaParser(tokens);
			const tree = parser.compilationUnit();
			const listener = new JavaListener();
			antlr4.tree.ParseTreeWalker.DEFAULT.walk(listener, tree);
			parsedOutput.classes.push(...listener.getOutput().classes);
			parsedOutput.interfaces.push(...listener.getOutput().interfaces);
			parsedOutput.enums.push(...listener.getOutput().enums);
		} else if (node.type === 'directory' && node.children) {
			node.children.forEach((child) => traverseAndParse(child, parsedOutput));
		}
	}

	const archive = $openedArchive; // Accessing the store

	if (archive && archive.children) {
		archive.children.forEach((child) => traverseAndParse(child, parsedOutput));
	}

	console.log(parsedOutput);
	let diagram = toMermaidSyntax(parsedOutput);
	console.log(diagram);

	let container;

	function toMermaidSyntax(data) {
		let mermaidSyntax = 'classDiagram\n';

		// Function to process modifiers for UML notation
		const processModifiers = (modifiers) => {
			return modifiers
				.filter((mod) => !mod.startsWith('@')) // Remove @ annotations
				.map((mod) => {
					switch (mod) {
						case 'public':
							return '+';
						case 'private':
							return '-';
						case 'protected':
							return '#';
						default:
							return mod; // Keep other modifiers as is
					}
				})
				.join(' ');
		};

		// Loop through classes
		data.classes.forEach((cls) => {
			mermaidSyntax += `class ${cls.name} {\n`;

			// Add fields with modifiers
			cls.fields.forEach((field) => {
				const fieldString = processModifiers(field.modifiers) + field.type + ' ' + field.name;
				mermaidSyntax += `  ${fieldString}\n`;
			});

			// Add methods with modifiers, return type, and arguments
			cls.methods.forEach((method) => {
				const argsString = method.args.map((arg) => `${arg.type}`).join(', ');
				const methodString =
					processModifiers(method.modifiers) +
					' ' +
					method.name +
					'(' +
					argsString +
					') ' +
					method.returnType;
				mermaidSyntax += `  ${methodString}\n`;
			});

			mermaidSyntax += '}\n';

			// Add superclass relationship
			if (cls.superClass) {
				mermaidSyntax += `${cls.name} --|> ${cls.superClass}\n`;
			}

			// Add implemented interfaces
			cls.implementedInterfaces.forEach((interfaceName) => {
				mermaidSyntax += `${cls.name} ..|> ${interfaceName}\n`;
			});

			// Process fields for association
			cls.fields.forEach((field) => {
				if (isCustomType(field.type, data)) {
					mermaidSyntax += `${cls.name} --> ${field.type}\n`;
				}
			});

			// Optionally, process method parameters for association
			cls.methods.forEach((method) => {
				method.args.forEach((arg) => {
					if (isCustomType(arg.type, data)) {
						mermaidSyntax += `${cls.name} --> ${arg.type}\n`;
					}
				});
			});

			mermaidSyntax += '\n'; // Newline for separation
		});

		// Loop through interfaces
		data.interfaces.forEach((interfaceObj) => {
			mermaidSyntax += `class ${interfaceObj.name} {\n <<interface>> \n`;

			// Add interface methods
			interfaceObj.methods.forEach((method) => {
				const argsString = method.args.map((arg) => `${arg.type}`).join(', ');
				const methodString =
					processModifiers(method.modifiers) +
					' ' +
					method.name +
					'(' +
					argsString +
					') ' +
					method.returnType;
				mermaidSyntax += `  ${methodString}\n`;
			});

			mermaidSyntax += '}\n\n'; // Extra newline for separation
		});

		// Loop through enums
		data.enums.forEach((enumObj) => {
			mermaidSyntax += `class ${enumObj.name} {\n <<enumeration>> \n`;
			enumObj.fields.forEach((field) => {
				mermaidSyntax += `  ${field.name}\n`;
			});
			mermaidSyntax += '}\n\n'; // Extra newline for separation
		});

		return mermaidSyntax;
	}

	function isCustomType(typeName, data) {
		const allTypes = [
			...data.classes.map((cls) => cls.name),
			...data.interfaces.map((intf) => intf.name),
			...data.enums.map((enumObj) => enumObj.name)
		];
		return allTypes.includes(typeName);
	}

	async function renderDiagram() {
		mermaid.initialize({
			theme: 'neutral',
			securityLevel: 'loose'
		});
		const { svg, bindFunctions } = await mermaid.render('mermaid', diagram);
		container.innerHTML = svg;
	}

	$: diagram && renderDiagram();
</script>

<span class="diagram" bind:this={container} />

<style>
	.diagram {
		display: flex;
		justify-content: center;
		align-items: center;
		width: 100%;
	}
</style>
