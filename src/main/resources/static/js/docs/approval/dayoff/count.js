window.addEventListener('DOMContentLoaded', () => {
    getCount();
});

async function getCount() {
    let response = await fetchGet('dayoff-count');
    let result = await response.json();
    if(response.ok) {
        let dayoff = result.obj;
        document.getElementById('qty').innerText = dayoff.qty;
        document.getElementById('use').innerText = dayoff.use;
        document.getElementById('remainder').innerText = (dayoff.qty - dayoff.use);
    }
}
