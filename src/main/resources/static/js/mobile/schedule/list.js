window.addEventListener('message', receiveMsgFromChild);
function receiveMsgFromChild(e) {   // calendar.js로부터 메시지 수신
    if(e.data === 'getParams')
        sendParams();

    else if(e.data.msg === 'openView')
        openView(e.data.id);
}

function sendMsgToChild(msg) {   // iFrame으로 띄워져 있는 페이지의 js로 메시지 보내기. 여기서는 calendar.js
    document.getElementById('innerCalendar').contentWindow.postMessage(msg, '*');
}

function sendParams() {
    let params = {
        type: document.getElementById('type').value,
        option: document.getElementById('option').value
    };
    sendMsgToChild(params);
}

function openView(id) {
    newTab(`schedule/personal/${id}`, '', '');
}
