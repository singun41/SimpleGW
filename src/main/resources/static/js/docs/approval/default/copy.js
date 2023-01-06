document.addEventListener('DOMContentLoaded', () => {
    let btnDocsCopy = document.getElementById('btnDocsCopy');
    if(btnDocsCopy)
        btnDocsCopy.setAttribute('onclick', 'copyDocs()');
});

function copyDocs() {
    let docs = {
        'title': document.getElementById('docsTitle').innerHTML,
        'content': document.getElementById('docsContent').innerHTML
    };
    localStorage.setItem('docs', JSON.stringify(docs));
    location.href = 'write';
}
