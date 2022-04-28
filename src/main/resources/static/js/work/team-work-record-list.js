async function getWorkRecordList() {
    clearWorkRecordList();

    const team = document.getElementById('team');

    let param = {
        searchDate: searchDate.value,
        team: team.value
    };

    let result = await fetchCustom('GET', 'urlEncoded', param, 'team-work-record-list', 'json');

    result.forEach(elem => {
        let tr = document.createElement('tr');
        let tdMember = document.createElement('td');
        let tdWork = document.createElement('td');
        let tdPlan = document.createElement('td');
        let workTextarea = document.createElement('textarea');
        let planTextarea = document.createElement('textarea');

        workTextarea.setAttribute('class', 'form-control');
        planTextarea.setAttribute('class', 'form-control');
        workTextarea.setAttribute('rows', '12');
        planTextarea.setAttribute('rows', '12');
        workTextarea.readOnly = true;
        planTextarea.readOnly = true;

        workTextarea.value = elem.todayWork;
        planTextarea.value = elem.nextWorkPlan;

        tdMember.setAttribute('class', 'align-middle text-center');
        tdMember.append(elem.team, document.createElement('br'), elem.jobTitle + ' ' + elem.name);

        tdWork.append(workTextarea);
        tdPlan.append(planTextarea);
        tr.append(tdMember, tdWork, tdPlan);

        workRecordList.append(tr);
    });
}