window.addEventListener('DOMContentLoaded', event => {
    setDatetimePicker('dateStart', '', false);
    setDatetimePicker('dateEnd', '', false);

    setTypeTitle();
});

async function deleteSchedule() {
    if(confirm('삭제하시겠습니까?')) {
        let id = document.getElementById('scheduleId').innerText;
        let result = await fetchCustom('DELETE', 'default', '', 'schedule/' + id, 'msg');
        if(result.status === 'SUCCESS') {
            window.opener.getScheduleData();
            self.close();
        }
    }
}