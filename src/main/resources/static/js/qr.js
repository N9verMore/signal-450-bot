async function pollStatus() {
    try {
        const r = await fetch('/status', { cache: 'no-store', credentials: 'same-origin' });
        const j = await r.json();
        if (j.authorized) {
            window.location.href = '/scheduler';
            return;
        }
        const el = document.getElementById('status');
        if (el) el.textContent = 'Not linked yet. Still waiting...';
    } catch (e) {
        console.error('Ошибка проверки статуса:', e);
    }
    setTimeout(pollStatus, 3000);
}
pollStatus();
