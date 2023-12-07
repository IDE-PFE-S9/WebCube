<script>
	import { onMount } from 'svelte';
	import { terminalOutput, cheerpjState, cheerpjWidth, cheerpjHeight } from '$lib/stores.js';

	let previousContent = '';
	let iframe;

	const stopExecution = () => {
		cheerpjState.set({ showPopup: true, runJar: false, reloadJar: false });
		$terminalOutput = [...$terminalOutput, `Process terminated by the user`];
		popup.style.display = 'none';
	};

	onMount(async () => {
		await cheerpjInit();
		let popup = document.getElementById('popup');

		// run the code when the button is pressed in another component
		cheerpjState.subscribe(({ showPopup, runJar }) => {
			if (showPopup) {
				popup.style.display = 'flex';
				if (runJar) {
					iframe.src = 'about:blank';
					iframe.onload = async () => {
						const iframeWindow = iframe.contentWindow;
						cheerpjCreateDisplay(-1, -1, iframeWindow.document.body);
						const exitCode = await cheerpjRunJar('/str/application.jar');
						$terminalOutput = [...$terminalOutput, `Program exited with code ${exitCode}`];
					};
				}
			}
		});

		// redirect the console inside the Terminal component
		const consoleElement = document.getElementById('console');
		const observer = new MutationObserver((mutations) => {
			let currentContent = consoleElement.innerText;
			let newContent = currentContent.replace(previousContent, '');
			if (newContent) {
				terminalOutput.update((output) => [...output, newContent]);
			}
			previousContent = currentContent;
		});

		observer.observe(consoleElement, { childList: true, characterData: true, subtree: true });

		return () => {
			observer.disconnect();
		};
	});
</script>

<div id="popup">
	<button on:click={stopExecution} aria-label="Close">
		<svg viewBox="0 0 24 24" width="12" height="12" fill="black">
			<path
				d="M18.3,5.7l-1.4-1.4L12,9.2L7.1,4.3L5.7,5.7l4.9,4.9l-4.9,4.9l1.4,1.4l4.9-4.9l4.9,4.9l1.4-1.4l-4.9-4.9
                     L18.3,5.7z"
			/>
		</svg>
	</button>
	<iframe
		id="cheerpjIframe"
		title="Output"
		bind:this={iframe}
		width={$cheerpjWidth}
		height={$cheerpjHeight}
	/>
	<p id="console" />
</div>

<style lang="scss">
	#popup {
		display: none;
		position: fixed;
		flex-direction: column;
		top: 40%;
		left: 50%;
		padding: 10px;
		transform: translate(-40%, -50%);
		background-color: rgb(51, 51, 51);

		#cheerpjIframe {
			border: 0;
		}

		p {
			display: none;
		}

		button {
			// Style adjustments to make it look like a macOS close button
			align-self: flex-start;
			display: flex; // Use flexbox to center content
			justify-content: center; // Center horizontally
			align-items: center; // Center vertically
			width: 16px; // Adjusted for better usability
			height: 16px; // Adjusted for better usability
			border-radius: 50%; // Circular shape
			border: none; // No border
			background-color: #ff3b30; // Red background
			padding: 0; // Remove padding
			padding-top: 1px; // Remove padding
			cursor: pointer; // Cursor pointer to indicate it's clickable
			box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2); // Optional: adds a subtle shadow

			svg {
				width: 12px; // Size of the SVG icon
				height: 12px; // Size of the SVG icon
				fill: black; // Color of the SVG icon
			}

			&:hover {
				background-color: darken(#ff3b30, 10%); // Darker red when hovered
			}

			&:active {
				background-color: darken(#ff3b30, 20%); // Even darker red when clicked
			}
		}
	}
</style>
