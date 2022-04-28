let main = document.getElementById('main');
function sendToMain(type) {
    // frame.js에서 iFrame으로 불러온 main.html의 main.js로 데이터를 보낸다.
    main.contentWindow.postMessage(type, '*');
}