window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '자료실';

    document.getElementById('btnTempSave').setAttribute('onclick', 'insertBoardDocs("archive", 0)');
    document.getElementById('btnInsert').setAttribute('onclick', 'insertBoardDocs("archive", 1)');

    copyDocsCheck();
});
