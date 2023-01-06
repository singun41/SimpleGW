function openLineSearch() {
    let option = "width=1000, height=650";
    window.open('/page/approval/line-set', '', option);
}

// 결재문서 등록시 서버로 보내기 위한 멤버 id 변수
let approverIds = [];
let referrerIds = [];

function setLine() {
    let approvers = document.getElementById('approvers');
    let referrers = document.getElementById('referrers');

    while(approvers.hasChildNodes())
        approvers.removeChild(approvers.firstChild);

    while(referrers.hasChildNodes())
        referrers.removeChild(referrers.firstChild);


    approvers.innerHTML = '<p class="text-center text-secondary">[ 결재 ]</p>'
    referrers.innerHTML = '<p class="text-center text-secondary">[ 참조 ]</p>'

    let lineData = JSON.parse(localStorage.getItem('approverLineData'));
    localStorage.removeItem('approverLineData');

    approverIds = lineData.approverIds;
    referrerIds = lineData.referrerIds;

    let num = 1;
    Array.from(lineData.approverNames).forEach(e => {
        let p = document.createElement('p');
        p.classList.add('text-center');
        p.innerText = `${num}. ${e}`;
        approvers.append(p);
        num++;
    });

    Array.from(lineData.referrerNames).forEach(e => {
        let p = document.createElement('p');
        p.classList.add('text-center');
        p.innerText = e;
        referrers.append(p);
    });
}
