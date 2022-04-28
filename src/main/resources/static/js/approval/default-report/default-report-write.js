window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '기안서';

    document.getElementById('btnTempSave').setAttribute('onclick', 'insertApprovalDocs("default-report", 0)');
    document.getElementById('btnInsert').setAttribute('onclick', 'insertApprovalDocs("default-report", 1)');

    copyApprovalDocsCheck();
});
