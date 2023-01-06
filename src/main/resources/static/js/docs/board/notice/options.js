document.addEventListener('DOMContentLoaded', () => {
    dayjs.locale('ko');

    flatpickr(inputDueDate, {
        enableTime: false,
        dateFormat: 'Y. m. d.',
        'locale': 'ko'
    });
});
const inputDueDate = document.getElementById('inputDueDate');

async function saveOptions(docsId) {
    useOpt = enabledDueDate;   // 다른 옵션이 추가되는경우 or 연산으로 옵션사용여부를 체크.

    let dueDateVal = inputDueDate.value.replaceAll('. ', '-').replaceAll('.', '');
    let params = {
        use: useOpt,
        dueDate: ((enabledDueDate && dueDateVal !== '') ? dueDateVal : null)   // 체크되어 있고, 빈 값이 아닌 경우만 등록.
    };

    await fetchPostParams(`notice/${docsId}/options`, params);
}

let useOpt = false;
let enabledDueDate = false;
function setDueDate(e) {
    enabledDueDate = e.checked;
    if(!e.checked)
        inputDueDate.value = '';
}

async function getOptions() {
    let response = await fetchGet(`notice/${docsId}/options`);
    let result = await response.json();
    
    let options = result.obj;
    if(options) {
        if(options.dueDate) {
            document.getElementById('chkDueDate').click();
            document.getElementById('inputDueDate').value = `${options.dueDate.replaceAll('-', '. ')}.`;
        }
    }
}
