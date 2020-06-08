package com.defianttech.convertme

/*
 * Copyright (c) 2014-2018 Dmitry Brant
 */
class SingleUnit(val id: Int, val name: String, val multiplier: Double, val offset: Double) {
    var isEnabled = true

    override fun toString(): String {
        return name
    }
}
