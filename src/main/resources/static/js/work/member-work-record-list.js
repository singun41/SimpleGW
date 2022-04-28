async function getWorkRecordList() {
    clearWorkRecordList();

    let param = {
        searchDate: searchDate.value,
    };

    let result = await fetchCustom('GET', 'urlEncoded', param, 'member-work-record-list', 'json');

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
        tdMember.append(elem.jobTitle + ' ' + elem.name);
        tdWork.append(workTextarea);
        tdPlan.append(planTextarea);
        tr.append(tdMember, tdWork, tdPlan);

        workRecordList.append(tr);
    });
}