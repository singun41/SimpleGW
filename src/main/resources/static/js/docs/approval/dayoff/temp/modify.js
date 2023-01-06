window.addEventListener('DOMContentLoaded', () => {
    getDetails(`${docsId}/temp`);
});

async function updateTemp() {
    let params = getSaveParams();
    let docsId = await updateTempApprovalDocs(params);
    if(docsId) {
        saveComplete = true;
        location.href = `/page/approval/${docsType}/temp/${docsId}`;
    }
}

async function save() {
    let params = getSaveParams();
    let docsId = await saveApprovalDocs(params);
    if(docsId)
        location.href = `/page/approval/${docsType}/${docsId}`;
}
