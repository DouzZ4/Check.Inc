// Placeholder fallback for Chart.js
// If you haven't downloaded Chart.js to /resources/js/chart.min.js,
// this stub prevents runtime errors and logs instructions.
(function(){
    if (typeof Chart !== 'undefined') return; // Chart already loaded

    console.warn('Chart.js no está presente en /resources/js/chart.min.js. Usar Chart.js local o ajustar CSP.');

    // Minimal stub to avoid exceptions in code that calls `new Chart(...)`.
    window.Chart = function(ctx, config) {
        console.warn('Chart stub invoked — no chart will be rendered. Install Chart.js v3.9.1 at /resources/js/chart.min.js to enable charts.');
        return {
            destroy: function(){},
            update: function(){}
        };
    };
})();
