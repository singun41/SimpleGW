window.addEventListener('DOMContentLoaded', event => {
    document.getElementById('documentKindTitle').innerText = '공지사항';
    document.getElementById('btnUpdate').setAttribute('onclick', 'updateBoardDocs("notice", 1)');
    if(document.getElementById('btnTempSave')) {
        document.getElementById('btnTempSave').setAttribute('onclick', 'updateBoardDocs("notice", 0)');
    }

    getOptions();   // 공지사항만 적용한다.
});
