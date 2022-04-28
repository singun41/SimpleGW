window.addEventListener('DOMContentLoaded', event => {
    setDatetimePicker('searchDate', 'date', true);
    getWorkRecordList();
});

const searchDate = document.getElementById('searchDate');
const workRecordList = document.getElementById('workRecordList');

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

    await getWorkRecordList(searchDate.value);
}

async function changeWorkRecord() {
    await getWorkRecordList(searchDate.value);
}

function clearWorkRecordList() {
    while(workRecordList.hasChildNodes()) {
        workRecordList.removeChild(workRecordList.firstChild);
    }
}