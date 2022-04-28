window.addEventListener('DOMContentLoaded', event => {
    // setTimeout(() => {
    //     getContent('freeboard/content/');
    // }, 500);

    document.getElementById('documentKindTitle').innerText = '자유게시판';
    document.getElementById('btnUpdate').setAttribute('onclick', 'updateBoardDocs("freeboard", 1)');
    if(document.getElementById('btnTempSave')) {
        document.getElementById('btnTempSave').setAttribute('onclick', 'updateBoardDocs("freeboard", 0)');
    }
});
