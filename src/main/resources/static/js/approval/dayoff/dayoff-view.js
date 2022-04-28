window.addEventListener('DOMContentLoaded', event => {
    setCardHeaderTitle('fas fa-user-check', '휴가 신청서');
});

async function deleteApproval() {
    await deleteApprovalDocs('dayoff');
}

function copyApproval() {
    copyApprovalForSublist('dayoff');
}