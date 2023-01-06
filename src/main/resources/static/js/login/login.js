let inputPw = document.getElementsByName('userPw')[0];
function checkCapsLock(event) {
    let tooltip = bootstrap.Tooltip.getInstance(inputPw);
    if(event.getModifierState('CapsLock')) {
        inputPw.setAttribute('data-bs-original-title', 'CapsLock이 켜져 있습니다.');
        tooltip.show();
    } else {
        tooltip.hide();
    }
}
