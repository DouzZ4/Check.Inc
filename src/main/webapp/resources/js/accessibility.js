
let accessibilityMode = false;
let currentUtterance = null;

function toggleAccessibilityMode() {
    accessibilityMode = !accessibilityMode;
    const btn = document.getElementById('accessibility-toggle-btn');

    if (accessibilityMode) {
        btn.classList.add('active');
        btn.setAttribute('aria-pressed', 'true');
        speakText("Modo de lectura activado");
        document.body.style.cursor = 'help'; // Visual cue
    } else {
        btn.classList.remove('active');
        btn.setAttribute('aria-pressed', 'false');
        window.speechSynthesis.cancel();
        speakText("Modo de lectura desactivado");
        document.body.style.cursor = 'default';
    }
}

function speakText(text) {
    if (!text || text.trim() === '') return;

    // Stop any current speech
    window.speechSynthesis.cancel();

    currentUtterance = new SpeechSynthesisUtterance(text);
    currentUtterance.lang = 'es-ES'; // Spanish
    currentUtterance.rate = 1.0;

    window.speechSynthesis.speak(currentUtterance);
}

document.addEventListener('mouseover', function (e) {
    if (!accessibilityMode) return;

    // Find closest readable element
    const target = e.target;

    // Skip if it's the body or html directly
    if (target === document.body || target === document.documentElement) return;

    // We want to read text content of elements like p, h1-h6, span, a, button, li, th, td, label
    // or elements with aria-label

    let textToRead = '';

    if (target.hasAttribute('aria-label')) {
        textToRead = target.getAttribute('aria-label');
    } else if (target.hasAttribute('alt')) { // For images
        textToRead = target.getAttribute('alt');
    } else if (target.innerText && target.innerText.trim().length > 0) {
        // For container elements, we might want to be careful not to read massive blocks
        // Just reading innerText of the target might read all children.
        // Let's try to be specific: direct text nodes or specific tags.

        // Simple approach: read innerText but cap length or checking implementation
        // For a hover feature, reading the immediate text content is usually what's expected.

        // Prevent reading large containers causing noise
        if (target.tagName === 'DIV' || target.tagName === 'SECTION' || target.tagName === 'MAIN') {
            // check if it has direct text
            // This can be complex. Let's stick to reading the standard elements or leaf nodes.
            const hasTextNode = Array.from(target.childNodes).some(node => node.nodeType === Node.TEXT_NODE && node.textContent.trim().length > 0);
            if (!hasTextNode) return;
        }

        textToRead = target.innerText;
    }

    if (textToRead) {
        // Highlight logic
        const originalOutline = target.style.outline;
        target.style.outline = '2px solid #f39c12';

        target.addEventListener('mouseout', function () {
            target.style.outline = originalOutline;
            window.speechSynthesis.cancel();
        }, { once: true });

        speakText(textToRead);
    }
});
