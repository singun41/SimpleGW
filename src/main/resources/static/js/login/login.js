window.addEventListener('DOMContentLoaded', event => {
    let agent = navigator.userAgent.toLowerCase();
    // if ((navigator.appName == 'Netscape' && agent.indexOf('trident') != -1) || (agent.indexOf("msie") != -1)) {   // appName 속성 --> deprecated
    if ((agent.indexOf('trident') != -1) || (agent.indexOf("msie") != -1)) {
        // ie일 경우
        alert('IE로는 접속할 수 없습니다.' + '\n' + '크롬이나 엣지 브라우저를 사용하세요.');
        document.getElementById('userPw').setAttribute('disabled', true);
    }
});
function checkCapsLock(event) {
    if(event.getModifierState('CapsLock')) {
        document.getElementById('capslockMsg').innerText = 'CapsLock이 켜져 있습니다.';
    } else {
        document.getElementById('capslockMsg').innerText = '';
    }
}