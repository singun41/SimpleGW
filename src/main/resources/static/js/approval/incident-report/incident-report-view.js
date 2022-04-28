window.addEventListener('DOMContentLoaded', event => {
    setCardHeaderTitle('far fa-copy', '사고 보고서');
});

async function deleteApproval() {
    await deleteApprovalDocs('incident-report');
}

function copyApproval() {
    copyApprovalDocs('incident-report');
}