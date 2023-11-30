<script>
	import mermaid from 'mermaid';
	import antlr4 from 'antlr4';
	import { CharStreams, CommonTokenStream } from 'antlr4';
	import JavaLexer from '$lib/StructureParser/JavaLexer';
	import JavaParser from '$lib/StructureParser/JavaParser';
	import JavaParserListener from '$lib/StructureParser/JavaParserListener';
	import { openedArchive } from '$lib/stores';
	import Swal from 'sweetalert2';
	import { onMount } from 'svelte';
	import { showedEntities, entitiesList } from '$lib/stores';

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
		} else if (node.type === 'directory' && node.children && !node.name.includes("test")) {
			node.children.forEach((child) => traverseAndParse(child, parsedOutput));
		}
	}

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

	function onClickClass(node) {
		console.log(`Class clicked: ${node.id.split('-')[1]}`);
		let className = node.id.split('-')[1];
		Swal.fire({
			title: `Edit Class: ${className}`,
			showCancelButton: true,
			html: `
            <input id="field-name" placeholder="Field Name">
            <input id="field-type" placeholder="Field Type">
			<select id="field-modifier">
                <option value="public">public</option>
                <option value="protected">protected</option>
                <option value="private">private</option>
            </select>`,
			focusConfirm: false,
			preConfirm: () => {
				const fieldName = document.getElementById('field-name').value;
				const fieldType = document.getElementById('field-type').value;
				const fieldModifier = document.getElementById('field-modifier').value;
				return { fieldName, fieldType, fieldModifier };
			}
		}).then((result) => {
			if (result.isConfirmed) {
				updateClassFields(className, result.value);
			}
		});
	}

	function updateClassFields(className, { fieldName, fieldType, fieldModifier }) {
		const classObj = parsedOutput.classes.find((cls) => cls.name === className);
		if (classObj) {
			classObj.fields.push({
				name: fieldName,
				type: fieldType,
				modifiers: [fieldModifier]
			});
			diagram = toMermaidSyntax(parsedOutput);
		}
	}

	const filterOutput = (showedEntities, parsedOutput) => {
		console.log(showedEntities)
		const filteredClasses = parsedOutput.classes
			.filter((cls) => showedEntities.includes(cls.name))
			.map((cls) => ({
				...cls,
				implementedInterfaces:
					cls.implementedInterfaces?.filter((name) => showedEntities.includes(name)) || [],
				superClass: showedEntities.includes(cls.superClass) ? cls.superClass : null
			}));

		const filteredInterfaces = parsedOutput.interfaces
			.filter((intf) => showedEntities.includes(intf.name))
			.map((intf) => ({
				...intf,
				extendedInterfaces:
					intf.extendedInterfaces?.filter((name) => showedEntities.includes(name)) || []
			}));

		const filteredEnums = parsedOutput.enums.filter((enm) => showedEntities.includes(enm.name));

		const filteredOutput = {
			classes: filteredClasses,
			enums: filteredEnums,
			interfaces: filteredInterfaces
		};
		return filteredOutput;
	};

	async function renderDiagram(diagram) {
		const { svg } = await mermaid.render('mermaid', diagram);
		container.innerHTML = svg;

		const svgElement = container.querySelector('svg');
		svgElement.querySelectorAll('.node').forEach((node) => {
			node.addEventListener('click', () => {
				onClickClass(node);
			});
		});
	}

	const updateDiagram = () => {
		const filteredOutput = filterOutput($showedEntities, parsedOutput);
		console.log($showedEntities)
		console.log(filteredOutput)
		if (
			filteredOutput.classes.length === 0 &&
			filteredOutput.enums.length === 0 &&
			filteredOutput.interfaces.length === 0
		) {
			// Handle the case where there are no entities to display
			diagram = ''; // Clear the diagram
			container.innerHTML = '<p>No entities to display. Please select some entities.</p>';
		} else {
			// Generate the diagram syntax from the filtered output
			diagram = toMermaidSyntax(filteredOutput);
			renderDiagram(diagram);
		}
	}

	const getEntityNames = (output) => {
		let classesList = output.classes.map((cls) => cls.name);
		let enumsList = output.enums.map((enm) => enm.name);
		let interfacesList = output.interfaces.map((intf) => intf.name);
		return [...classesList, ...enumsList, ...interfacesList];
	};

	onMount(async () => {
		const archive = $openedArchive; // Accessing the store

		if (archive && archive.children) {
			archive.children.forEach((child) => traverseAndParse(child, parsedOutput));
		}

		$entitiesList = [...getEntityNames(parsedOutput)];
		console.log($entitiesList)
		$showedEntities = [...$entitiesList]; // Show all entities by default

		let diagram = toMermaidSyntax(parsedOutput);

		mermaid.initialize({
			theme: 'neutral',
			securityLevel: 'loose'
		});

		await renderDiagram(diagram);

		mounted = true
	});

	let parsedOutput = { classes: [], enums: [], interfaces: [] };
	let diagram = '';
	let container; // Reference to the diagram container
	let mounted = false;

	$: if (mounted) {
        $showedEntities;
        updateDiagram();
    }
</script>

{#if openedArchive}
	<span class="diagram" bind:this={container} />
{:else}
	<h3 class="message">Veuillez d'abord ouvrir un tp</h3>
{/if}

<style>
	.diagram {
		display: flex;
		justify-content: center;
		align-items: center;
		width: 100%;
		height: 100%;
	}

	.message {
		display: flex;
		justify-content: center;
		align-items: center;
		width: 100%;
		height: 100%;
		color: white;
	}
</style>
