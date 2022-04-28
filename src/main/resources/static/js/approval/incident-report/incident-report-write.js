window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '사고 보고서';

    document.getElementById('btnTempSave').setAttribute('onclick', 'insertApprovalDocs("incident-report", 0)');
    document.getElementById('btnInsert').setAttribute('onclick', 'insertApprovalDocs("incident-report", 1)');

    copyApprovalDocsCheck();
});
