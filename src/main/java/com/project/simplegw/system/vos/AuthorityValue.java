package com.project.simplegw.system.vos;

public enum AuthorityValue {
    // R: Read-Only
    // W: Writable(=insert, update)
    // D: Deletable


    // W, WD, D는 기본적으로 Read가 기본적으로 있어야 하는 권한이므로 불필요.

    NONE, R, RW, RD, RWD;
}
