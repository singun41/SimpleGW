window.addEventListener('DOMContentLoaded', event => {
    setTitleAndIcon('진행중인 결재', 'fas fa-file-export');
    searchSubmittedApprovalDocs();
});

async function searchSubmittedApprovalDocs() {
    let url = 'approval/proceeding/list';
    await getSubmittedDocsList('', url);
}