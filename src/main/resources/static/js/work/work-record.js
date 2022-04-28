window.addEventListener('DOMContentLoaded', event => {
    setDatetimePicker('searchDate', 'date', true);
    setWorkRecordDays(moment());
});

const searchDate = document.getElementById('searchDate');
const beforeWork = document.getElementById('beforeWork');
const beforePlan = document.getElementById('beforePlan');
const work = document.getElementById('work');
const plan = document.getElementById('plan');
const id = document.getElementById('id');

async function setDateAgo(type) {
    let searchDateValue = searchDate.value;

    switch(type) {
        case 'day':
            searchDate.value = moment(searchDateValue).add(-1, 'days').format('YYYY-MM-DD');
            break;
        case 'today':
            searchDate.value = moment().format('YYYY-MM-DD');
            break;
        default:
            break;
    }

    await setWorkRecordDays(searchDate.value);
}

async function setWorkRecordDays(date) {
    const dayBefore = document.getElementById('dayBefore');
    const today = document.getElementById('today');

    dayBefore.innerText = '';
    today.innerText = '';

    dayBefore.append(moment(date).add(-1, 'days').format('YYYY-MM-DD').toString());
    today.append(moment(date).format('YYYY-MM-DD').toString());

    await getWorkRecord();
}

async function changeWorkRecord() {
    await setWorkRecordDays(searchDate.value);
}

async function getWorkRecord() {
    id.innerText = '';

    let param = {
        searchDate: searchDate.value
    };
    let result = await fetchCustom('GET', 'urlEncoded', param, 'work-record', 'json');
    
    beforeWork.value = result[0].todayWork;
    beforePlan.value = result[0].nextWorkPlan;
    work.value = result[1].todayWork;
    plan.value = result[1].nextWorkPlan;
    id.append(result[1].id);
}

async function saveWorkRecord() {
    if(work.value === '') {
        alert('업무 처리 내용을 입력하세요.');
        return;
    }

    let param = {
        id: id.innerText,
        workDate: searchDate.value,
        todayWork: work.value,
        nextWorkPlan: plan.value
    };
    await fetchCustom('PUT', 'default', param, 'work-record', 'msg');
}