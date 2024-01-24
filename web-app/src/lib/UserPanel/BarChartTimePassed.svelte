<script>
    import '@carbon/charts-svelte/styles.css'
	import { BarChartSimple } from '@carbon/charts-svelte'

    export let names;
    export let times;

    let data = [];
    let chartHeight = 0;

    let options = {};

    $: {
        data = names.map((name, index) => {
            return {
                "name": name,
                "time_passed": times[index]
            };
        });
        data.sort((a, b) => a.name.localeCompare(b.name));
        data.reverse();
        chartHeight = data.length*50 + "px"

        options = {
            "axes": {
                "left": {
                    "title":"Nom",
                    "mapsTo": "name",
                    "scaleType": "labels"
                },
                "bottom": {
                    "title":"Temps passé (minutes)",
                    "mapsTo": "time_passed",
                    "scaleType": "linear" 
                }
            },
            "height": chartHeight,
            "width": "600px",
            "theme": "g100",
            "toolbar":{"enabled":false},
            "legend": {"enabled": false},
            "color": {
                "scale": {
                    "terminé": "rgb(218,153,36)",
                    "en cours": "rgb(52,120,198)"
                }
            }
        }
    }
</script>

<div class="individuel">
    <BarChartSimple {data} {options} />
</div>

<style lang="scss">
    .individuel {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        margin-bottom: 1rem;
    }
</style>