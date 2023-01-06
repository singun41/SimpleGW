package com.project.simplegw.document.vos;

import com.project.simplegw.system.vos.Menu;

public enum EditorDocs {
    // WYSWYG 에디터를 사용하는 문서 리스트를 작성한다.

    NOTICE(Menu.NOTICE), FREEBOARD(Menu.FREEBOARD), SUGGESTION(Menu.SUGGESTION), ARCHIVE(Menu.ARCHIVE),

    MINUTES(Menu.MINUTES),
    
    APPROVAL_DEFAULT(Menu.APPROVAL_DEFAULT), APPROVAL_COOPERATION(Menu.APPROVAL_COOPERATION)
    ;

    private Menu menu;
    private String title;

    private EditorDocs(Menu menu) {
        this.menu = menu;
        this.title = menu.getTitle();
    }

    public Menu getMenu() {
        return this.menu;
    }

    public String getTitle() {
        return this.title;
    }
}
