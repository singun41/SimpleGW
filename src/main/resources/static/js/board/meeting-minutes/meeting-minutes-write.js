window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '회의록';

    document.getElementById('btnTempSave').setAttribute('onclick', 'insertBoardDocs("meeting", 0)');
    document.getElementById('btnInsert').setAttribute('onclick', 'insertBoardDocs("meeting", 1)');

    copyDocsCheck();
});
