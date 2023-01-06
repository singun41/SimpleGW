let oldPwResult = false;
let newPwResult = false;
let chkPwResult = false;

function checkPattern(e) {
    if(!e.value)
        return false;

    let reg = new RegExp('((?=.*[a-z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$)');
    if(reg.test(e.value))
        return true;
    else
        return false;
}

async function checkOld(e) {
    let tooltip = bootstrap.Tooltip.getInstance(e);
    let chk = e.parentNode.childNodes[5];
    if(!e.value) {
        chk.innerHTML = '';
        tooltip.hide();
        oldPwResult = false;
        return;
    }
    let params = { oldPw: e.value };
    let response = await fetchGetParams('old-pw-matched', params);
    let result = await response.json();

    if(result.obj) {
        chk.classList.remove('text-danger');
        chk.classList.add('text-success');
        chk.innerHTML = '<i class="fa-solid fa-check"></i>';
        tooltip.hide();
        e.readOnly = true;
        oldPwResult = true;

    } else {
        chk.classList.remove('text-success');
        chk.classList.add('text-danger');
        chk.innerHTML = '<i class="fa-solid fa-ban"></i>';

        e.setAttribute('data-bs-original-title', '기존 패스워드가 일치하지 않습니다.');
        tooltip.show();
        oldPwResult = false;
    }
}

function checkNew(e) {
    let tooltip = bootstrap.Tooltip.getInstance(e);
    let chk = e.parentNode.childNodes[5];
    if(!e.value) {
        chk.innerHTML = '';
        tooltip.hide();
        newPwResult = false;
        return;
    }

    let result = checkPattern(e);
    if(!result) {
        chk.classList.remove('text-success');
        chk.classList.add('text-danger');
        chk.innerHTML = '<i class="fa-solid fa-ban"></i>';

        e.setAttribute('data-bs-original-title', '8자 이상 영문, 숫자, 특수문자를 포함하세요.');
        tooltip.show();
        newPwResult = false;
        return;
    }

    let oldPw = document.getElementById('oldPw').value;
    let newPw = e.value;

    if(oldPw !== newPw) {
        chk.classList.remove('text-danger');
        chk.classList.add('text-success');
        chk.innerHTML = '<i class="fa-solid fa-check"></i>';

        tooltip.hide();
        newPwResult = true;

    } else {
        chk.classList.remove('text-success');
        chk.classList.add('text-danger');
        chk.innerHTML = '<i class="fa-solid fa-ban"></i>';

        e.setAttribute('data-bs-original-title', '기존 패스워드와 다르게 작성하세요.');
        tooltip.show();
        newPwResult = false;
    }
}

function checkPw(e) {
    let tooltip = bootstrap.Tooltip.getInstance(e);
    let chk = e.parentNode.childNodes[5];
    if(!e.value) {
        chk.innerHTML = '';
        tooltip.hide();
        chkPwResult = false;
        return;
    }

    let newPw = document.getElementById('newPw').value;

    if(e.value === newPw) {
        chk.classList.remove('text-danger');
        chk.classList.add('text-success');
        chk.innerHTML = '<i class="fa-solid fa-check"></i>';

        tooltip.hide();
        chkPwResult = true;

    } else {
        chk.classList.remove('text-success');
        chk.classList.add('text-danger');
        chk.innerHTML = '<i class="fa-solid fa-ban"></i>';

        e.setAttribute('data-bs-original-title', '신규 패스워드와 일치하지 않습니다.');
        tooltip.show();
        chkPwResult = false;
    }
}

function clearPwForm() {
    oldPwResult = newPwResult = chkPwResult = false;

    Array.from(document.getElementsByClassName('pw-input')).forEach(e => {
        e.readOnly = false;
        e.value = '';
    });

    Array.from(document.getElementsByClassName('pw-chk-i')).forEach(e => {
        e.innerHTML = '';
    });
}


async function updatePw() {
    if(!confirm('패스워드를 변경하시겠습니까?'))
        return;

    if(oldPwResult && newPwResult && chkPwResult) {
        let params = {
            oldPw: document.getElementById('oldPw').value,
            newPw: document.getElementById('newPw').value
        };
        let response = await fetchPatchParams('password', params);
        let result = await response.json();
        alert(result.msg);

        if(response.ok)
            document.getElementById('btnClosePw').click();
    } else {
        alert('입력란을 정확히 작성하세요.');
    }
}
