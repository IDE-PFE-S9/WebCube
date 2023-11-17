<script>
    export let name;
    export let nbTestStudent;
    export let nbTestTp;
    
    let percentage = Math.round((nbTestStudent / nbTestTp) * 100);
    const calculateCircumference = (radius) => 2 * Math.PI * radius;
    const radius = 25;
    const circumference = calculateCircumference(radius);
</script>

<div class="individuel">
    <h5 class="name">{name}</h5>
    <div class="progress-container">
        <svg width="120" height="120">
          <circle
            class="progress-ring__circle"
            stroke="lightgrey"
            stroke-width="4"
            fill="transparent"
            r="{radius}"
            cx="60"
            cy="60"
          />
          <circle
            class="progress-ring__circle"
            stroke="rgb(52,120,198)"
            stroke-width="4"
            fill="transparent"
            stroke-dasharray="{circumference} {circumference}"
            stroke-dashoffset="{circumference - percentage / 100 * circumference}"
            r="{radius}"
            cx="60"
            cy="60"
          />
        </svg>
        <div class="percentage-text">{percentage}%</div>
      </div>
</div>

<style lang="scss">
    .individuel {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        margin-bottom: 1rem;
        width: 10rem;
    }

    .name {
        margin: 0.5rem;
    }

    
  .progress-ring__circle {
    transition: stroke-dashoffset 0.35s;
    transform: rotate(-90deg);
    transform-origin: 50% 50%;
  }

  .progress-container {
    position: relative;
    width: 120px;
    height: 120px;
  }

  .percentage-text {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 1rem;
  }
</style>