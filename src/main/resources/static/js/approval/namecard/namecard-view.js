window.addEventListener('DOMContentLoaded', event => {
    setCardHeaderTitle('far fa-id-card', '명함 신청서');
    document.getElementById('btnCopyApprovalDocs').classList.add('d-none');
});

async function deleteApproval() {
    await deleteApprovalDocs('namecard');
}