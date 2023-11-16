<script>
    export let name;
    export let nbTestStudent;
    export let nbTestTp;
    
    let percentage = Math.round((nbTestStudent / nbTestTp) * 100);
</script>

<div class="individuel">
    <h2 class="name">{name}</h2>
    <div class="progress-bar">
        <span class="title">Nombre de test : {nbTestStudent} / {nbTestTp}</span>
        <div class="bar" style="width: 100%;">
            <div class="percentage-bar" class:complete={percentage === 100} style="width: {percentage}%;"></div>
            <span class="percentage">{percentage}%</span>
        </div>
    </div>
</div>

<style lang="scss">
    .individuel {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        margin-bottom: 1.5rem;
        width: 20rem;
    }

    .name {
        margin: 0;
    }

    .progress-bar {
        margin-bottom: 0.5rem;

        .title {
            display: block;
            margin-bottom: 0.2rem;
        }

        .bar {
            height: 1.5rem; 
            position: relative;
            border: 2px solid white;
            border-radius: 20px;

            .percentage-bar {
                background-color: rgb(52,120,198);
                border-radius: 15px;
                height: 100%;
                position: absolute;  
            }
            
            .complete{
                    background-color: green;
                }

            .percentage {
                position: absolute;
                left: 50%;
                top: 50%;
                transform: translate(-50%, -50%);
                color: white;
            }
        }
    }
</style>

