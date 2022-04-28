window.addEventListener('DOMContentLoaded', event => {
    setDatetimePicker('dueDate', 'date', true);
    dueDate.value = '';
});

const dueDate = document.getElementById('dueDate');

function toggleOptions(elem) {
    let isChecked = elem.checked;

    Array.from(document.getElementsByClassName('option-chk-list')).forEach(tag => {
        tag.checked = false;
        tag.disabled = !isChecked;
    });

    if(!document.getElementById('useOptions').checked) {
        Array.from(document.getElementsByClassName('option-val-list')).forEach(tag => {
            tag.value = '';
            if(!isChecked)
                tag.disabled = true;
        });   
    }
}

function toggleDueDate(elem) {
    let isChecked = elem.checked;
    dueDate.value = (isChecked ? moment().format('YYYY-MM-DD') : '');
    dueDate.disabled = !isChecked;
}

async function optionsSave(docsId) {
    let dueDate = document.getElementById('dueDate').value;
    if(dueDate < moment().format('YYYY-MM-DD')) {   // 설정 날짜가 오늘 이전이면 null 처리.
        dueDate = null;
    }
    let param = {
        use: document.getElementById('useOptions').checked,
        docsId: docsId,
        dueDate: document.getElementById('dueDate').value
    };
    await fetchCustom('POST', 'default', param, 'docs-options', '');
}

async function getOptions() {
    let docsId = document.getElementById('docsId').innerText;
    let result = await fetchCustom('GET', 'default', '', 'docs-options/' + docsId, 'json');
    if(result) {
        if(result.use) {
            const useOptions = document.getElementById('useOptions');
            useOptions.checked = true;
            toggleOptions(useOptions);

            if(result.dueDate) {
                const setDueDate = document.getElementById('setDueDate');
                setDueDate.checked = true;
                toggleDueDate(setDueDate);
                document.getElementById('dueDate').value = result.dueDate;
            }
        }
    }
}