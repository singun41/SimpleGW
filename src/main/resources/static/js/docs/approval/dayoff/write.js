window.addEventListener('DOMContentLoaded', () => {
    copyCheck();
});

async function copyCheck() {
    let docs = JSON.parse(localStorage.getItem('docs'));
    if(docs) {
        document.getElementById('title').value = docs.title;
        document.getElementById('content').value = docs.content;
        await getDetails(docs.id);
        localStorage.removeItem('docs');
    }
}

async function save() {
    let params = getSaveParams();
    let docsId = await saveApprovalDocs(params);
    if(docsId)
        location.href = `/page/approval/${docsType}/${docsId}`;
}

async function saveTemp() {
    let params = getSaveParams();
    let docsId = await saveTempApprovalDocs(params);
    if(docsId)
        location.href = `/page/approval/${docsType}/temp/${docsId}`;
}
