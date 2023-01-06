window.addEventListener('DOMContentLoaded', () => {
    getDetails(docsId);
});

async function update() {
    let params = getSaveParams();
    let docsId = await updateApprovalDocs(params);
    if(docsId) {
        saveComplete = true;
        location.href = `/page/approval/${docsType}/${docsId}`;   // common/modify.js와 url이 다르다.
    }
}
