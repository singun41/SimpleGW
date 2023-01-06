document.addEventListener('DOMContentLoaded', () => {
    renderingModal = new bootstrap.Modal(document.getElementById('renderingModal'), { keyboard: false });
});
let renderingModal;

const scheduleType = document.getElementById('type');
const scheduleSearchOption = document.getElementById('option');

function openNew() {
    window.open(`/page/schedule/${document.getElementById('type').value}/new`, '', 'width=750, height=550');
}

function openNewWithData(data) {   // 캘린더에서 받은 파라미터를 새 창에 바인딩하기 위해 로컬스토리지 사용.
    localStorage.setItem('schedule', JSON.stringify(data));
    openNew();
}

function openView(id) {
    window.open(`/page/schedule/${document.getElementById('type').value}/${id}`, '', 'width=750, height=550');
}

window.addEventListener('message', receiveMsgFromChild);
function receiveMsgFromChild(e) {   // calendar.js로부터 메시지 수신
    if(e.data === 'getParams')
        sendParams();

    else if(e.data === 'show')
        renderingModal.show();

    else if(e.data === 'hide')
        renderingModal.hide();
    
    else if(e.data.msg === 'openNew')
        openNewWithData(e.data);
    
    else if(e.data.msg === 'openView')
        openView(e.data.id);
}

const iframePage = document.getElementById('innerCalendar');
function sendMsgToChild(msg) {   // iFrame으로 띄워져 있는 페이지의 js로 메시지 보내기. 여기서는 calendar.js
    iframePage.contentWindow.postMessage(msg, '*');
}

function sendParams() {
    let params = {
        type: scheduleType.value,
        option: scheduleSearchOption.value
    };
    sendMsgToChild(params);
}
