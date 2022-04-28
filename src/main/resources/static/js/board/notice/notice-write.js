window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '공지사항';

    document.getElementById('btnTempSave').setAttribute('onclick', 'insertBoardDocs("notice", 0)');
    document.getElementById('btnInsert').setAttribute('onclick', 'insertBoardDocs("notice", 1)');

    copyDocsCheck();
});
