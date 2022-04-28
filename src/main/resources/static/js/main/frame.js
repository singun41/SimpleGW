/*!
    * Start Bootstrap - SB Admin v6.0.3 (https://startbootstrap.com/template/sb-admin)
    * Copyright 2013-2021 Start Bootstrap
    * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-sb-admin/blob/master/LICENSE)
*/


window.addEventListener('DOMContentLoaded', event => {
    "use strict";

    // Add active state to sidbar nav links
    // var path = window.location.href; // because the 'href' property of the DOM element is the absolute path
    // $("#layoutSidenav_nav .sb-sidenav a.nav-link").each(() => {
    //     if (this.href === path) {
    //         $(this).addClass("active");
    //     }
    // });

    // Toggle the side navigation
    $("#sidebarToggle").on("click", (e) => {
        e.preventDefault();
        $("body").toggleClass("sb-sidenav-toggled");
    });

    const currentDate = document.getElementById('frameNavDatetime');
    setInterval(() => {
        currentDate.innerText = moment().format('YYYY. MM. DD. ddd HH:mm:ss');
    }, 1000);

    getPicture();
});

function getPage(page) {
    // 네비게이션 앵커들을 비활성화해서 색상 변경
    const anchors = document.querySelectorAll('.nav > a.nav-link');
    anchors.forEach(anchor => {
        let hrefValue = anchor.getAttribute('href');
        anchor.className = anchor.className.replace(' active', '');
        
        if(hrefValue.replace('#', '') === page) {
            anchor.className += ' active';
        }
    });
    showIframePage(page);
}

function showIframePage(page) {
    document.getElementById('mainViewFrame').setAttribute('src', page);
}

function checkCapsLock(event) {
    if(event.getModifierState('CapsLock')) {
        document.getElementById('capslockMsg').innerText = 'CapsLock이 켜져 있습니다.';
    } else {
        document.getElementById('capslockMsg').innerText = '';
    }
}

function clearPwForm() {
    document.getElementById('originalPw').value = '';
    document.getElementById('newPw').value = '';
    document.getElementById('newPwCheck').value = '';
}

function getPicture() {
    document.getElementById('myPicture').setAttribute('src', '/picture');
    document.getElementById('myPicture').setAttribute('onerror', 'this.src="/images/user-default.png"');
}

async function setPicture() {
    const pictureInput = document.getElementById('pictureInput');
    if(pictureInput.files[0] === undefined) {
        alert('사진을 선택하세요.');
        return;
    }

    let formData = new FormData;
    formData.append('img', pictureInput.files[0]);

    let result = await fetchCustomFormData('picture', formData);
    if(result === 'ok') {
        getPicture();
    } else {
        alert(result);
    }
}

async function updateMyInfo() {
    const myName = document.getElementById('myName');
    const myNameEng = document.getElementById('myNameEng');
    const myMobile = document.getElementById('myMobile');
    const mybirthYear = document.getElementById('year');
    const mybirthMonth = document.getElementById('month');
    const mybirthDay = document.getElementById('day');

    let param = {
        name: myName.value,
        nameEng: myNameEng.value,
        mobileNo: myMobile.value,
        year: mybirthYear.value,
        month: mybirthMonth.value,
        day: mybirthDay.value,
    };

    let result = await fetchCustom('PUT', 'default', param, 'my-info', 'msg');
    if(result.status !== 'SUCCESS') {
        return;
    } else {
        $('#myinfoForm').modal('hide');
    }
}

async function updateMyPw() {
    const originalPw = document.getElementById('originalPw');
    const newPw = document.getElementById('newPw');
    const newPwCheck = document.getElementById('newPwCheck');

    let param = {
        originalPw: originalPw.value,
        newPw: newPw.value,
        newPwCheck: newPwCheck.value
    };

    let result = await fetchCustom('PUT', 'default', param, 'my-pw', 'msg');
    if(result.status !== 'SUCCESS') {
        return;
    } else {
        $('#mypwForm').modal('hide');
    }
}

window.addEventListener('message', receivedFromMain);
function receivedFromMain(e) {
    if(e.data === 'show-myinfo') {
        $('#myinfoForm').modal('show');
    }
}
