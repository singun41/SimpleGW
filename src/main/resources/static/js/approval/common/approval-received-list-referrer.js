window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('결재 참조 문서', 'fas fa-file-import');
    searchReceivedApprovalDocs();
});

async function searchReceivedApprovalDocs() {
    let param = {
        type: 'REFERRER',
        kind: 'ALL'
    };
    let url = 'approval/received/list/current';
    await getReceivedDocsList(param, url);
}