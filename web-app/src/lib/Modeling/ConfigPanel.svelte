<script>
	import { showedEntities, entitiesList } from '$lib/stores';

	function handleCheckboxChange(entity) {
		showedEntities.update((current) => {
			if (!Array.isArray(current)) {
				console.error('Expected an array, received:', current);
				return []; // Fallback to an empty array in case of error
			}

			const index = current.indexOf(entity);
			if (index >= 0) {
				// Remove the entity
				return current.filter((e) => e !== entity);
			} else {
				// Add the entity
				return [...current, entity];
			}
		});
	}
</script>

<div class="config-panel">
	<div id="title-container">
		<h1 id="title">Panneau de configuration</h1>
	</div>
	<div class="checkboxes">
		{#each $entitiesList as entity}
			<label class:checked={$showedEntities.includes(entity)}>
				<input
					type="checkbox"
					checked={$showedEntities.includes(entity)}
					on:change={() => handleCheckboxChange(entity)}
				/>
				{entity}
			</label>
		{/each}
	</div>
</div>

<style lang="scss">
	.config-panel {
		display: flex;
		flex-direction: column;
		background-color: rgb(37, 37, 38);
		min-width: 20rem;
		height: 100%;

		// Base styles for the scrollbar
		&::-webkit-scrollbar {
			width: 12px;
			background-color: rgba(255, 255, 255, 0); // Fully transparent track
		}

		// Styling the thumb (the draggable scrolling element)
		&::-webkit-scrollbar-thumb {
			background-color: rgba(255, 255, 255, 0); // Fully transparent by default
			border: 2px solid transparent;
			background-clip: content-box; // Makes the thumb smaller than the track
			transition: background-color 1s ease; // Transition effect for the hover state
		}

		// Hiding the corner where horizontal and vertical scrollbars meet
		&::-webkit-scrollbar-corner {
			background-color: transparent;
		}

		// Styles when the container is hovered
		&:hover {
			// Make the thumb semi-transparent white on hover
			&::-webkit-scrollbar-thumb {
				background-color: rgba(255, 255, 255, 0.1);
			}

			// Hide the horizontal scrollbar on hover
			&::-webkit-scrollbar:horizontal {
				display: none;
			}
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
			margin-bottom: 1rem;
		}

		.checkboxes {
			display: flex;
			flex-direction: column;
			padding: 1rem;
			padding-top: 0;
			padding-bottom: 0;
			flex-grow: 1;
			color: white;

			// Hide the default checkbox
			input[type='checkbox'] {
				display: none;
			}

			// Style the label to look like a button
			label {
				display: inline-block;
				background-color: rgb(52, 52, 52); // Button background color
				color: white; // Text color
				cursor: pointer;
				padding: 0.5rem 1rem;
				margin-bottom: 0.5rem; // Space between buttons
				border: none;
				text-align: center;
				text-decoration: none;
				font-size: 1rem;
				transition: background-color 0.3s ease;
				border-radius: 5px; // Rounded corners

				// Checkbox checked state styles
				&.checked {
					background-color: darken(#3498db, 10%); // Darker background for checked state

					&:hover {
						background-color: lighten(#3498db, 10%);
					}
				}

				// Hover effect
				&:hover {
					background-color: lighten(rgb(52, 52, 52), 10%);
				}
			}

			// Visually indicate the checked state when the checkbox is checked
			input[type='checkbox']:checked + label {
				background-color: darken(#3498db, 10%);
				&:after {
					content: '✓'; // Checkmark symbol
					padding-left: 0.5rem;
				}
			}
		}
	}
</style>
