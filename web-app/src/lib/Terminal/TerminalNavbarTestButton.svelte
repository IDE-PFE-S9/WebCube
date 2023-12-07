<script>
	import { terminalOutput, openedArchive, tpId } from '$lib/stores.js';
	import TestIcon from '../assets/TerminalNavbarIcons/TestIcon.svelte';
	import Cookies from 'js-cookie';
	import { workTestPopup, workTestErrorPopup } from '/src/lib/PopUps/popup.js';
	import { getUserInformations } from '$lib/auth.js';

	let apiUrl = process.env.API_URL;
	let projectPath = process.env.PROJECT_PATH; 

	async function testCode() {
		try {
			$terminalOutput = [...$terminalOutput, 'Compiling...'];

			let headersList = {
				Accept: '*/*',
				'Content-Type': 'application/json',
				'Authorization-Azure': 'Bearer ' + Cookies.get('azureJWT'),
				'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
			};

			const user = await getUserInformations();
			let username = user.uniqueName.split('@')[0].replace('.', '-');

			// API call to compile the code and get the API response
			let compilationResponse = await fetch(
				`${apiUrl}/api/compileAndTest?projectPath=${projectPath}/${username}/${$openedArchive.name}`,
				{
					method: 'GET',
					headers: headersList
				}
			);
			let compilationResult = await compilationResponse.text();

			// parse results
			const resultList = parseInput(compilationResult);

			let successCount = 0;
			resultList.forEach((message) => {
				$terminalOutput = [...$terminalOutput, objectToString(message)];
				if (message.Status === "SUCCESS") {
					successCount++;
				}
			});

			let completion = Math.round(successCount / resultList.length * 100);
			console.log(completion)
			await fetch(`${apiUrl}/api/tp/myCompletion/${$tpId}`, {
				method: 'PUT',
				headers: headersList,
				body: JSON.stringify(completion)
			});

			workTestPopup();

		} catch (error) {
			console.error('Une erreur est survenue lors du test du fichier :', error);
			workTestErrorPopup();
		}
	}

	function objectToString(obj) {
		let resultString = `Test: ${obj.Test}\nStatus: ${obj.Status}`;

		if (obj.Data) {
			resultString += `\nData: ${obj.Data}`;
		}
		return resultString;
	}

	// Main parsing function
	function parseInput(input) {
		input = input.slice(0, -1).slice(1);
		let entries = input.split(', Test:').map((entry) => entry.trim());

		const parsedEntries = entries.map((item, index) => {
			if (index === 0) {
				// Return the first item as is
				return item;
			} else {
				// Add "Test:" to the beginning of the item
				return 'Test: ' + item;
			}
		});

		console.log(parsedEntries);

		const result = parsedEntries.map((entry) => extractInfo(entry));

		function extractInfo(str) {
			// Extract 'Test' and 'Status' values
			const testMatch = str.match(/Test: (.*?),/);
			const statusMatch = str.match(/Status: (.*?),/);
			let testData = str.match(/Data: (.*)/s); // 's' flag to include newline characters

			let test = testMatch && testMatch[1] ? testMatch[1].trim() : null;
			let status = statusMatch && statusMatch[1] ? statusMatch[1].trim() : null;
			let data = testData && testData[1] ? testData[1].trim() : null;

			// Check if 'Data' is 'null' as a string
			if (data === 'null') {
				data = null;
			}

			return {
				Test: test,
				Status: status,
				Data: data
			};
		}

		return result;
	}
</script>

<button class="run-button" on:click={testCode}>
	<TestIcon />
</button>

<style lang="scss">
	.run-button {
		all: unset;
		cursor: pointer;
	}
</style>
