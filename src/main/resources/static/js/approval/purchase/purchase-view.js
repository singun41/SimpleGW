window.addEventListener('DOMContentLoaded', event => {
    setCardHeaderTitle('fas fa-boxes', '물품 구매 신청서');
});

async function deleteApproval() {
    await deleteApprovalDocs('purchase');
}

function copyApproval() {
    copyApprovalForSublist('purchase');
}