window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('결재 요청 문서', 'fas fa-file-import');
    searchReceivedApprovalDocs();
});

async function searchReceivedApprovalDocs() {
    let param = {
        type: 'APPROVER',
        kind: 'ALL'
    };
    let url = 'approval/received/list/current';
    await getReceivedDocsList(param, url);
}