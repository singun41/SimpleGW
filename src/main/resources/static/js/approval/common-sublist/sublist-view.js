function copyApprovalForSublist(url) {
    let docs = {
        'id': document.getElementById('docsId').innerText,
        'title': document.getElementById('docsTitle').innerHTML,
        'content': document.getElementById('docsContent').value
    };
    localStorage.setItem('approvalDocs', JSON.stringify(docs));
    location.href = '/approval/' + url + '/writepage';
}