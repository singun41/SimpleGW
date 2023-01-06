function copyDocs() {
    let docs = {
        'title': document.getElementById('docsTitle').innerHTML,
        'content': document.getElementById('docsContent').innerHTML
    };
    localStorage.setItem('docs', JSON.stringify(docs));
    location.href = 'write';
}

function editDocs() {
    location.href = `${docsId}/modify`;
}

async function deleteDocs() {
    if(!confirm('문서를 삭제하시겠습니까?'))
        return;
    
    let response = await fetchDelete(`${docsType}/${docsId}`);
    let result = await response.json();
    alert(result.msg);
    
    if(response.ok)
        location.href = 'list';
}
