window.addEventListener('DOMContentLoaded', event => {
    setCardHeaderTitle('far fa-copy', '기안서');
});

async function deleteApproval() {
    await deleteApprovalDocs('default-report');
}

function copyApproval() {
    copyApprovalDocs('default-report');
}