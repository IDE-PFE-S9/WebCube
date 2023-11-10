<script>
	import ExamModeToggle from './ExamModeToggle.svelte';
    import UserCount from './UserCount.svelte';
</script>

<div class="general-container">
    <h1>Dashboard administateur</h1>

    <ExamModeToggle />
    <UserCount />
    <h2>More Features Coming Soon...</h2>
</div>

<style lang="scss">
    .general-container{
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
    }
</style>