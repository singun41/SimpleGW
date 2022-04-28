const formatString = 'yyyy-MM-DD HH:mm';
const dateStartInput = document.getElementById('dateStart');
const dateEndInput = document.getElementById('dateEnd');

function setTypeTitle() {
    document.getElementById('typeTitle').innerText = '[ ' + opener.getTypeValue() + ' ]';   // main.js의 getTypeValue()
}

function checkDateStart() {
    if(dateEndInput.value === '') {
        return;
    }
    if(dateStartInput.value > dateEndInput.value) {
        dateEndInput.value = dateStartInput.value;
    }
}

function checkDateEnd() {
    if(dateStartInput.value === '') {
        return;
    }
    if(dateStartInput.value > dateEndInput.value) {
        dateStartInput.value = dateEndInput.value;
    }
}

async function saveSchedule() {
    let id = document.getElementById('scheduleId');
    let scheduleId;
    if(id) {
        scheduleId = id.innerText;
    } else {
        scheduleId = null;
    }
    let param = {
        id: scheduleId,
        type: document.getElementById('type').innerText,
        code: document.getElementById('codeSelector').value,
        title: document.getElementById('title').value,
        content: document.getElementById('content').value,
        datetimeStart: (document.getElementById('dateStart').value.replace(' ', 'T')),
        datetimeEnd: (document.getElementById('dateEnd').value.replace(' ', 'T'))
    };

    let result = await fetchCustom('POST', 'default', param, 'schedule', 'msg');
    if(result.status === 'SUCCESS') {
        opener.getScheduleData();
        self.close();
    }
}