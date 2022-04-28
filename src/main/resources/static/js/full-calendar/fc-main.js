window.addEventListener('message', receiveMsgFromFrame);
function receiveMsgFromFrame(e) {
    // e.data가 frame.js에서 받은 메시지
    if(e.data === 'search') {
        getScheduleData();
    }
    if(e.data === 'write') {
        clearData();
        openToPopupSm('write/' + typeCode);
    }
    if(e.data === 'reload') {
        getType();
        getScheduleData();
    }
}

window.addEventListener('DOMContentLoaded', event => {
    getType();
    initialization();
});

let calendar;
function initialization() {
    let calendarEl = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(calendarEl, {
        // height: 700,
        aspectRatio: 1.5,
        initialView: 'dayGridMonth',
        expandRows: true,
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
        },
        slotMinTime: '00:00',
        slotMaxTime: '24:00',

        navLinks: true, // can click day/week names to navigate views
        editable: true,
        selectable: true,
        nowIndicator: true,
        dayMaxEvents: true, // allow "more" link when too many events

        locale: 'ko',
        weekNumbers: true,

        // 날짜 클릭 이벤트
        select: arg => {
            setData(arg);
            calendar.unselect();

            openToPopupSm('write/' + typeCode);
        },

        // event에 mouse over시 description을 tooltip으로 보여준다.
        eventDidMount: info => {
            let dateStr = document.createElement('p');
            dateStr.append(info.event.extendedProps.customStartDate + info.event.extendedProps.customStartTime, ' ~ ', info.event.extendedProps.customEndDate + info.event.extendedProps.customEndTime);

            let userStr = document.createElement('p');
            userStr.textContent = info.event.extendedProps.team + ' ' + info.event.extendedProps.jobTitle + ' ' + info.event.extendedProps.user;
            
            let resourceStr = document.createElement('p');
            resourceStr.textContent = info.event.extendedProps.resource;

            let tippyElem = document.createElement('div');
            tippyElem.append(dateStr, userStr, resourceStr);

            tippy(info.el, {
                content: tippyElem
            });
        },

        eventClick: arg => {
            openToPopupSm('details/' + arg.event.id);
        }
    });
    calendar.render();
    getScheduleData();

    document.getElementsByClassName('fc-prev-button')[0].addEventListener('click', () => {
        getScheduleData();
    });
    document.getElementsByClassName('fc-next-button')[0].addEventListener('click', () => {
        getScheduleData();
    });
    document.getElementsByClassName('fc-today-button')[0].addEventListener('click', () => {
        getScheduleData();
    });
}

function addEvent(id, team, jobTitle, user, code, resource, title, start, end) {
    calendar.addEvent({
        // default property
        id: id,
        start: start,
        end: end,
        title: resource + ' - ' + user,   // calendar에 렌더링 될 때 보이는 title
        color: resourceColor.get(code),
        textColor: '#343a40',
        
        // extendedProps --> calendar option에서 info.event.extendedProps.명칭으로 사용.
        customStartDate: start.substr(5, 5).replace('-' ,'. ') + '. ',
        customStartTime: start.substr(11, 5),
        customEndDate: end.substr(5, 5).replace('-' ,'. ') + '. ',
        customEndTime: end.substr(11, 5),
        customTitle: title,

        team: team,
        jobTitle: jobTitle,
        user: user,
        code: code,
        resource: resource
    });
}

async function getScheduleData() {
    let evtSources = calendar.getEvents();
    for(let i=0; i<evtSources.length; i++) {
        evtSources[i].remove();
    }

    let param = {
        year: calendar.getDate().getFullYear(),
        month: calendar.getDate().getMonth() + 1
    };
    let result = await fetchCustom('GET', 'urlEncoded', param, 'schedule/' + typeCode, 'json');
    result.forEach(e => { addEvent(e.id, e.team, e.jobTitle, e.name, e.code, e.codeValue, e.title, e.datetimeStart, e.datetimeEnd); });
}

async function getType() {
    const parent = window.parent.document;
    typeCode = parent.getElementById('typeSelector').value;
    typeValue = parent.getElementById('typeSelector').options[parent.getElementById('typeSelector').selectedIndex].text;

    await getCodelistAndColorSetting(typeCode);
}

let typeCode = '';
let typeValue = '';
let calendarData = {
    typeCode: '',
    dateStart: '',
    dateEnd: ''
};

function clearData() {
    calendarData = {
        typeCode: '',
        dateStart: '',
        dateEnd: ''
    };
}
function setData(arg) {
    calendarData.dateStart = arg.startStr;
    calendarData.dateEnd = arg.endStr;
}
function getData() {
    return calendarData;
}

function getTypeValue() {   // write.js에서 호출.
    return typeValue;
}

// color 세팅을 편하게 하기 위해서 hashmap을 사용.
HashMap = function() {
    this.map = [];
};
HashMap.prototype = {
    put: function(key, value) {
        this.map[key] = value;
    },
    get: function(key) {
        return this.map[key];
    },
    clear : function() {
        for(var prop in this.map) {
            delete this.map[prop];
        }
    }
};

let arrCodelist = [];
let arrColor = [
    // 순서대로 다른 색상 적용을 위해서 b -> g -> y -> r -> b 이렇게 순환하도록 섞는다.
    // blue    green      yellow     red
    '#89c4f4', '#d5ecc2', '#ffffcc', '#f1a9a0',
    '#c5eff7', '#98ddca', '#f4f776', '#ff9478'
];
let resourceColor = new HashMap();

async function getCodelistAndColorSetting(type) {
    let result = await fetchCustom('GET', 'default', '', 'schedule/code-list/' + type, 'json');

    // 초기화
    arrCodelist = [];
    resourceColor.clear();

    result.forEach(e => { arrCodelist.push(e.code); });

    let colorIdx = 0;
    let colorLastIdx = arrColor.length - 1;
    for(let i=0; i<arrCodelist.length; i++) {
        if(colorIdx > colorLastIdx)
            colorIdx = 0;
        
        resourceColor.put(arrCodelist[i], arrColor[colorIdx++]);
    }
}