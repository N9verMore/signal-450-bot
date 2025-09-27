async function checkStatusOnce() {
    try {
        // Encode the mobile number so that '+' and other special characters are transmitted correctly
        const r = await fetch('/status?mobileNumber=' + encodeURIComponent(mobileNumber), { cache: 'no-store', credentials: 'same-origin' });
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
const mobileNumber = new URL(document.currentScript.src).searchParams.get('mobileNumber');
checkStatusOnce();