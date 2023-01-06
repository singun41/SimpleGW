document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');

    flatpickr(searchDtp, {
        enableTime: false,
        dateFormat: 'Y. m. d.',
        defaultDate: dayjs().format('YYYY. MM. DD.'),
        'locale': 'ko',
        disableMobile: 'true'
        // mode 기본값인 single이면 모바일 브라우저에서는 기본 date/time/datetime input으로 전환됨. 이 옵션을 전환되지 않게 설정.
    });

    getData();
});
const searchDtp = document.getElementsByClassName('input-date')[0];

async function getData() {
    let dateVal = searchDtp.value.replaceAll(' ', '').split('.');
    let response = await fetchGet(`work-record/personal/${dateVal[0]}/${dateVal[1]}/${dateVal[2]}`);
    let result = await response.json();
    
    if(response.ok) {
        let data = result.obj;        
        document.getElementById('work').value = data[1].todayWork;
        document.getElementById('plan').value = data[1].nextPlan;
    }
}

async function save() {
    let params = {
        workDate: searchDtp.value.replaceAll('. ', '-').replace('.', ''),
        todayWork: ( document.getElementById('work').value === '' ? null : document.getElementById('work').value ),
        nextPlan: ( document.getElementById('plan').value === '' ? null : document.getElementById('plan').value )
    };

    let response = await fetchPatchParams('work-record/personal', params);
    let result = await response.json();
    alert(result.msg);
}
