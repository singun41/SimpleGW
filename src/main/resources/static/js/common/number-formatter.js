function numcheckAndSetComma(event, elem) {
    // html의 input 태그에서 onkeyup="numcheckAndSetComma(event, this)"를 작성해주면 된다.
    let str;
    if(event.keyCode != 8) {
        if(!(event.keyCode >= 37 && event.keyCode <= 40)) {
            var inputVal = elem.value;
            str = inputVal.replace(/[^-0-9]/gi,'');
            if(str.lastIndexOf("-") > 0) {   // 중간에 -가 있다면 replace
                if(str.indexOf("-") == 0) {  // 음수라면 replace 후 - 붙여준다.
                    str = "-" + str.replace(/[-]/gi,'');
                } else {
                    str = str.replace(/[-]/gi,'');
                }
            }
            elem.value = str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,'); // 숫자 콤마처리를 한다.
        }
    }
}
