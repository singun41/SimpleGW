window.addEventListener('DOMContentLoaded', event => {
    setCardHeaderTitle('fas fa-user-clock', '연장 근무 신청서');
});

async function deleteApproval() {
    await deleteApprovalDocs('overtime');
}

function copyApproval() {
    copyApprovalForSublist('overtime');
}