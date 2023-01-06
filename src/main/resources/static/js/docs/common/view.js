document.addEventListener('DOMContentLoaded', () => {
    Array.from(document.getElementsByTagName('img')).forEach(e => {
        e.setAttribute('data-bs-toggle', 'tooltip');
        e.setAttribute('data-bs-placement', 'top');
        e.setAttribute('data-bs-original-title', '원본 크기로 보려면 이미지를 클릭하세요.');
        
        e.setAttribute('style', `${e.getAttribute('style')}; max-width: 100%; max-height: 500px;'`);

        e.addEventListener('click', () => {
            window.open(e.getAttribute('src'), '', 'width=1600, height=900');
        });
    });
});

const docsId = document.getElementById('docsId').innerText;
const docsType = document.getElementById('docsType').innerText;
