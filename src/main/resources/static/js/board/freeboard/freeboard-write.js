window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '자유게시판';

    document.getElementById('btnTempSave').setAttribute('onclick', 'insertBoardDocs("freeboard", 0)');
    document.getElementById('btnInsert').setAttribute('onclick', 'insertBoardDocs("freeboard", 1)');

    copyDocsCheck();
});
