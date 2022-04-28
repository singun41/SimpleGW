window.addEventListener('DOMContentLoaded', event => {
    setTimeout(() => {
        getContent('archive/content/');
    }, 500);   // 0.5초 정도 지연시킴. ckeditor 초기화 시간이 필요함. 비동기로 동작하므로 아래 코드가 바로 실행됨.

    document.getElementById('documentKindTitle').innerText = '자료실';
    document.getElementById('btnUpdate').setAttribute('onclick', 'updateBoardDocs("archive", 1)');
    if(document.getElementById('btnTempSave')) {
        document.getElementById('btnTempSave').setAttribute('onclick', 'updateBoardDocs("archive", 0)');
    }
});
