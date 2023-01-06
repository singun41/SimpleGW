document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');

    flatpickr(searchDtp, {
        enableTime: false,
        dateFormat: 'Y. m. d.',
        defaultDate: dayjs().format('YYYY. MM. DD.'),
        'locale': 'ko'
    });

    getData();
});
const searchDtp = document.getElementById('searchDtp');

async function getData() {
    let dateVal = searchDtp.value.replaceAll(' ', '').split('.');
    let response = await fetchGet(`work-record/personal/${dateVal[0]}/${dateVal[1]}/${dateVal[2]}`);
    let result = await response.json();
    
    if(response.ok) {
        let data = result.obj;
        document.getElementById('prevDate').innerText = data[0].workDate;
        document.getElementById('prevWork').value = data[0].todayWork;
        document.getElementById('prevPlan').value = data[0].nextPlan;
        
        document.getElementById('date').innerText = data[1].workDate;
        document.getElementById('work').value = data[1].todayWork;
        document.getElementById('plan').value = data[1].nextPlan;
    }
}

async function prev() {
    searchDtp.value = dayjs(searchDtp.value).add(-1, 'day').format('YYYY. MM. DD.');
    getData();
}

async function today() {
    searchDtp.value = dayjs().format('YYYY. MM. DD.');
    getData();
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
