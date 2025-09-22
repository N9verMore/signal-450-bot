async function checkStatusOnce() {
    try {
        const r = await fetch('/status', { cache: 'no-store', credentials: 'same-origin' });
        const j = await r.json();
        console.log("Auth status:", j.authorized);

        if (j.authorized) {
            window.location.href = '/scheduler';
        } else {
            setTimeout(checkStatusOnce, 3000);
        }
    } catch (e) {
        console.error("Ошибка проверки статуса:", e);
        setTimeout(checkStatusOnce, 3000);
    }
}

checkStatusOnce();