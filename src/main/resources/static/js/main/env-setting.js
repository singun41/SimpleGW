document.addEventListener('DOMContentLoaded', () => {
    getEnvironmentSetting();
});

let envSysNotiDelDay = 0;
let envAtOnceApprover = false;
let envAtOnceReferrer = false;
let envAtOnceApproval = false;
let envMainCalendarMine = false;
let envMainCalendarTeam = false;
let envCalendarHoliday = false;

async function getEnvironmentSetting() {   // main.js에서 한 번만 호출.
    let response = await fetchGet('environment-setting');
    let result = await response.json();
    if(response.ok) {
        let env = result.obj;

        // 로컬스토리지에 저장해서 전역으로 사용함.
        localStorage.removeItem('env');
        let envSetting = {
            'sysNotiDelDay': env.sysNotiDelDay,
            'atOnceApprover': env.atOnceApprover,
            'atOnceReferrer': env.atOnceReferrer,
            'atOnceApproval': env.atOnceApproval,
            'mainCalendarMine': env.mainCalendarMine,
            'mainCalendarTeam': env.mainCalendarTeam,
            'calendarHoliday': env.calendarHoliday
        };
        localStorage.setItem('env', JSON.stringify(envSetting));

        envSysNotiDelDay = env.sysNotiDelDay;
        envAtOnceApprover = env.atOnceApprover;
        envAtOnceReferrer = env.atOnceReferrer;
        envAtOnceApproval = env.atOnceApproval;
        envMainCalendarMine = env.mainCalendarMine;
        envMainCalendarTeam = env.mainCalendarTeam;
        envCalendarHoliday = env.calendarHoliday;

        document.getElementById('envSysNotiDelDay').value = envSysNotiDelDay;
        document.getElementById('envAtOnceApprover').checked = envAtOnceApprover;
        document.getElementById('envAtOnceReferrer').checked = envAtOnceReferrer;
        document.getElementById('envAtOnceApproval').checked = envAtOnceApproval;
        document.getElementById('envMainCalendarMine').checked = envMainCalendarMine;
        document.getElementById('envMainCalendarTeam').checked = envMainCalendarTeam;
        document.getElementById('envCalendarHoliday').checked = envCalendarHoliday;
    }
}

function calendarMineCheck() {
    if( ! document.getElementById('envMainCalendarMine').checked )
        document.getElementById('envMainCalendarTeam').checked = false;
}

function calendarTeamChecked() {
    if(document.getElementById('envMainCalendarTeam').checked)
        document.getElementById('envMainCalendarMine').checked = true;
}

async function saveEnvSetting() {
    envSysNotiDelDay = document.getElementById('envSysNotiDelDay').value;
    envAtOnceApprover = document.getElementById('envAtOnceApprover').checked;
    envAtOnceReferrer = document.getElementById('envAtOnceReferrer').checked;
    envAtOnceApproval = document.getElementById('envAtOnceApproval').checked;
    envMainCalendarMine = document.getElementById('envMainCalendarMine').checked;
    envMainCalendarTeam = document.getElementById('envMainCalendarTeam').checked;
    envCalendarHoliday = document.getElementById('envCalendarHoliday').checked;

    let params = {
        sysNotiDelDay: envSysNotiDelDay,
        atOnceApprover: envAtOnceApprover,
        atOnceReferrer: envAtOnceReferrer,
        atOnceApproval: envAtOnceApproval,
        mainCalendarMine: envMainCalendarMine,
        mainCalendarTeam: envMainCalendarTeam,
        calendarHoliday: envCalendarHoliday
    };
    let response = await fetchPatchParams('environment-setting', params);
    let result = await response.json();
    alert(result.msg);

    if(response.ok){
        localStorage.removeItem('env');
        let envSetting = {
            'sysNotiDelDay': envSysNotiDelDay,
            'atOnceApprover': envAtOnceApprover,
            'atOnceReferrer': envAtOnceReferrer,
            'atOnceApproval': envAtOnceApproval,
            'mainCalendarMine': envMainCalendarMine,
            'mainCalendarTeam': envMainCalendarTeam,
            'calendarHoliday': envCalendarHoliday
        };
        localStorage.setItem('env', JSON.stringify(envSetting));

        envSettingModal.hide();   // main.js에 선언됨.
    }
}
