document.addEventListener('DOMContentLoaded', () => { setScreenSize(); });
window.addEventListener('resize', () => { setScreenSize(); });   // document가 아니라 window이다.

function setScreenSize() {
    let vh = window.innerHeight * 0.01;
    let vw = window.innerWidth * 0.01;
    document.documentElement.style.setProperty('--vh', `${vh}px`);
    document.documentElement.style.setProperty('--vw', `${vw}px`);
}

function page(url) {
    location.href=`/page/m/${url}`;
}

function newTab(url) {
    window.open(`/page/m/${url}`, '', '');
}
