function isUrlFormat(urlText) {
    let urlRegex = /(http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
    if(urlRegex.test(urlText)) {
        return true;
    }
    return false;
}
