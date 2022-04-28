window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('수신문서', 'fas fa-file-import');
    searchReceivedApprovalDocs();
});

async function searchReceivedApprovalDocs() {
    let param = {
        type: document.getElementById('receivedType').value,
        kind: document.getElementById('docsType').value,
        dateStart: document.getElementById('searchDateStart').value,
        dateEnd: document.getElementById('searchDateEnd').value
    };
    let url = 'approval/received/list';
    await getReceivedDocsList(param, url);
}