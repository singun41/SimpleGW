window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('완결된 결재 문서', 'fas fa-clipboard-check');
    document.getElementById('search').setAttribute('onclick', 'searchSubmittedApprovalDocs()');
    searchSubmittedApprovalDocs();
});

async function searchSubmittedApprovalDocs() {
    let param = {
        dateStart: document.getElementById('searchDateStart').value,
        dateEnd: document.getElementById('searchDateEnd').value
    };
    let url = 'approval/finished/list';
    await getSubmittedDocsList(param, url);
}