function setCardHeaderTitle(iconType, title) {
    const icon = document.createElement('i');
    icon.setAttribute('class', iconType + ' custom-card-header-fas-icon-sm');
    
    const typeLabel = document.getElementById('boardType');
    typeLabel.prepend(title + ' ');
    typeLabel.prepend(icon);
}

async function deleteDocs(deleteUrl) {
    if(confirm('문서를 삭제하시겠습니까?')) {
        let docsId = document.getElementById('docsId').innerText;
        await fetchCustom('DELETE', 'default', '', deleteUrl + '/' + docsId, 'msg');
        location.href = '/' + deleteUrl + '/listpage';
    }
}

function copyDocs(url) {
    let docs = {
        'title': document.getElementById('docsTitle').innerHTML,
        'content': document.getElementById('docsContent').innerHTML
    };
    localStorage.setItem('docs', JSON.stringify(docs));
    location.href = '/' + url + '/writepage';
}