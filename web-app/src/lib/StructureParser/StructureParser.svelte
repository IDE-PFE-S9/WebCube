<script>
    import {openedCode} from "$lib/stores.js";
</script>

<div class="structure-parser">
    <div id="title-container">
		<h1 id="title">Structure</h1>
	</div>
    <div class="project-structure">
        {openedCode}
    </div>
</div>

<style lang="scss">
    .structure-parser{
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
    }
</style>