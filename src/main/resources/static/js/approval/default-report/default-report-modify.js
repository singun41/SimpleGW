window.addEventListener('DOMContentLoaded', event => {
    // setTimeout(() => {
    //     getContent('default-report/content/');
    // }, 500);   // 0.5초 정도 지연시킴. ckeditor 초기화 시간이 필요함. 비동기로 동작하므로 아래 코드가 바로 실행됨.
    
    document.getElementById('documentKindTitle').innerText = '기안서';
    document.getElementById('btnUpdate').setAttribute('onclick', 'updateApprovalDocs("default-report", 1)');
    if(document.getElementById('btnTempSave')) {
        document.getElementById('btnTempSave').setAttribute('onclick', 'updateApprovalDocs("default-report", 0)');
    }
});
