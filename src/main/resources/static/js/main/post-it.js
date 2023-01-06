document.addEventListener('DOMContentLoaded', () => {
    initializePostIt();
    getPostIt();
});

function initializePostIt() {
    Array.from(document.getElementsByTagName('textarea')).forEach(e => {
        e.setAttribute('placeholder', '간단히 작성하고 활용하세요.');
    });
}

async function updateTitle() {
    let idx = 0;
    let titles = document.getElementsByClassName('btn-post-it-title');
    let inputTitles = Array.from(document.getElementsByClassName('input-post-it-title'));

    inputTitles.forEach(e => {
        if(e.value) {
            titles[idx].innerText = e.value;
        } else {
            titles[idx].innerText = e.getAttribute('placeholder');
        }
        idx++;
    });

    await savePostIt();
}

async function getPostIt() {
    let response = await fetchGet('post-it');
    if(response.ok) {
        let result = await response.json();

        if(result.obj.length === 0)
            return;

        let titles = Array.from(document.getElementsByClassName('btn-post-it-title'));
        let contents = Array.from(document.getElementsByClassName('post-it-content'));
        let inputTitles = Array.from(document.getElementsByClassName('input-post-it-title'));

        let data = result.obj;
        let idx = 0;
        titles.forEach(e => {
            e.innerText = data[idx].title;
            contents[idx].value = data[idx].content;
            inputTitles[idx].value = data[idx].title;
            idx++;
        });
    }
}

async function savePostIt() {
    let idx = 0;
    let titles = Array.from(document.getElementsByClassName('btn-post-it-title'));
    let contents = Array.from(document.getElementsByClassName('post-it-content'));

    let arrParams = [];
    titles.forEach(e => {
        let params = {
            seq: idx + 1,
            title: e.innerText,
            content: contents[idx].value
        };
        arrParams.push(params);
        idx++;
    });
    await fetchPostParams('post-it', arrParams);
}

async function clearPostIt() {
    if(!confirm('포스트잇 내용을 초기화하시겠습니까?'))
        return;

    let inputTitles = Array.from(document.getElementsByClassName('input-post-it-title'));
    inputTitles.forEach(e => { e.value = ''; });

    let contents = Array.from(document.getElementsByClassName('post-it-content'));
    contents.forEach(e => { e.value = ''; });

    updateTitle();
    await savePostIt();
}
