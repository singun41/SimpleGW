window.addEventListener('DOMContentLoaded', () => {
    document.getElementById('btnSave').setAttribute('onclick', 'saveNotice()');
});

async function saveNotice() {   // saveOptions() 때문에 공통함수 save()를 쓰지 않고 saveNotice()로 분리.
    let docsId = await saveBoard();
    if(docsId) {
        saveComplete = true;
        await saveOptions(docsId);
        deleteTemp();   // 정상 등록하면 임시저장 문서를 삭제할 지 묻는다.
        location.href = `/page/${docsType}/${docsId}`;
    }
}
