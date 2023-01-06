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
    let team = document.getElementById('team').value;
    let response = await fetchGet(`work-record/${team}/${dateVal[0]}/${dateVal[1]}/${dateVal[2]}`);
    let result = await response.json();
    
    if(response.ok) {
        let list = document.getElementById('workRecordList');
        while(list.hasChildNodes())
            list.removeChild(list.firstChild);

        Array.from(result.obj).forEach(e => {
            let containerRow = document.createElement('div');
            containerRow.classList.add('row', 'mb-4', 'align-items-center');

            let colOne = document.createElement('div');
            colOne.classList.add('col-1');

            let colEleven = document.createElement('div');
            colEleven.classList.add('col-11');


            let memberRow = document.createElement('div');
            memberRow.classList.add('row', 'align-items-center');

            let nameDiv = document.createElement('div');
            nameDiv.classList.add('col-12', 'text-center');

            let name = document.createElement('span');
            name.classList.add('text-secondary', 'text-center', 'custom-fs-8');
            name.innerHTML = `${e.team}<br>${e.name} ${e.jobTitle}`;

            nameDiv.append(name);
            memberRow.append(nameDiv);
            colOne.append(memberRow);


            let contentRow = document.createElement('div');
            contentRow.classList.add('row');

            let workDiv = document.createElement('div');
            workDiv.classList.add('col-6');


            let work = document.createElement('textarea');
            work.classList.add('form-control');
            work.setAttribute('rows', '14');
            work.readOnly = true;
            work.value = e.todayWork;
            workDiv.append(work);


            let planDiv = document.createElement('div');
            planDiv.classList.add('col-6');

            let plan = document.createElement('textarea');
            plan.classList.add('form-control');
            plan.setAttribute('rows', '14');
            plan.readOnly = true;
            plan.value = e.nextPlan;
            planDiv.append(plan);


            contentRow.append(workDiv, planDiv);
            colEleven.append(contentRow);


            containerRow.append(colOne, colEleven);
            document.getElementById('workRecordList').append(containerRow);


            // let row = document.createElement('div');
            // row.classList.add('row', 'align-items-center', 'mb-3');

            // let nameDiv = document.createElement('div');
            // nameDiv.classList.add('col-2', 'text-center');

            // let name = document.createElement('span');
            // name.classList.add('text-secondary', 'text-center');
            // name.innerHTML = `${e.team}<br>${e.name} ${e.jobTitle}`;

            // nameDiv.append(name);

            // let workDiv = document.createElement('div');
            // workDiv.classList.add('col-5');

            // let work = document.createElement('textarea');
            // work.classList.add('form-control');
            // work.setAttribute('rows', '14');
            // work.readOnly = true;
            // work.value = e.todayWork;

            // workDiv.append(work);

            // let planDiv = document.createElement('div');
            // planDiv.classList.add('col-5');

            // let plan = document.createElement('textarea');
            // plan.classList.add('form-control');
            // plan.setAttribute('rows', '14');
            // plan.readOnly = true;
            // plan.value = e.nextPlan;

            // planDiv.append(plan);

            // row.append(nameDiv, workDiv, planDiv);

            // document.getElementById('workRecordList').append(row);
        });
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
