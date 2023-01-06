document.addEventListener('DOMContentLoaded', () => {
    let btnDocsCopy = document.getElementById('btnDocsCopy');
    if(btnDocsCopy)
        btnDocsCopy.setAttribute('onclick', 'copyDocs()');
});

function copyDocs() {
    // 디테일 데이터는 write.js에서 ajax로.
    let docs = {
        'id': docsId,
        'title': document.getElementById('docsTitle').innerHTML,
        'content': document.getElementById('docsContent').value
    };
    localStorage.setItem('docs', JSON.stringify(docs));
    location.href = 'write';
}
