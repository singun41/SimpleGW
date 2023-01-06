document.addEventListener('DOMContentLoaded', () => {
    colorModal = new bootstrap.Modal(document.getElementById('colorList'), { keyboard: true });
});
let colorModal;

const colorType = document.getElementById('colorType');
const tbodyColor = document.getElementById('tbodyColor');

async function getColorList() {
    let response = await fetchGet(`schedule/color-list/${colorType.value}`);
    let result = await response.json();

    if(response.ok) {
        while(tbodyColor.hasChildNodes()) 
            tbodyColor.removeChild(tbodyColor.firstChild);
        
        Array.from(result.obj).forEach(e => {
            let tr = document.createElement('tr');
            tr.classList.add('text-center');

            let code = document.createElement('td');
            let value = document.createElement('td');
            let color = document.createElement('td');

            code.innerText = e.code;
            value.innerText = e.value;

            let colorInput = document.createElement('input');
            colorInput.setAttribute('type', 'color');
            colorInput.classList.add('form-control', 'form-control-color', 'mx-auto');
            colorInput.value = `#${e.hexValue}`;
            color.append(colorInput);

            tr.append(code, value, color);
            tbodyColor.append(tr);
        });
    }
}

async function updateColor() {
    if(colorType.value) {
        let params = [];
        tbodyColor.childNodes.forEach(e => {
            let param = {
                code: e.childNodes[0].innerText,
                hexValue: e.childNodes[2].childNodes[0].value
            };
            params.push(param);
        });

        let response = await fetchPatchParams(`schedule/color-list/${colorType.value}`, params);
        let result = await response.json();
        alert(result.msg);
    }
    colorModal.hide();
}
