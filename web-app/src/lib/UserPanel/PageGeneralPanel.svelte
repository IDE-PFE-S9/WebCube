<script>
	import ConnectedUsers from './ConnectedUsers.svelte';
    import ExamModeToggle from './ExamModeToggle.svelte';
    import UserCount from './UserCount.svelte';
</script>

<div class="general-container">
    <h1>Dashboard administateur</h1>

    <ExamModeToggle />
    <ConnectedUsers />
</div>

<style lang="scss">
    .general-container{
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
    }
</style>