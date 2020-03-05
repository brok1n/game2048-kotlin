package com.brok1n.kotlin.fx.game2048

import javafx.scene.control.Button
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Paint
import javafx.scene.text.Font

class Block {

    var xInt = 0
    var yInt = 0

    var x = 0.0
    var y = 0.0
    var w = 70.0

    var number = 0

    var ui: Button? = null

    fun update() {
        when {
            number < 10 -> ui?.font = Font.font(50.0)
            number < 100 -> ui?.font = Font.font(45.0)
            number < 1000 -> ui?.font = Font.font(35.0)
            number < 10000 -> ui?.font = Font.font(25.0)
        }
        if (number > 0) {
            ui?.text = "${number}"
        } else {
            ui?.text = ""
        }
        if (number > 8) {
            ui?.textFill = Paint.valueOf(COLOR_WHITE_TXT)
        } else {
            ui?.textFill = Paint.valueOf(COLOR_BLACK_TXT)
        }
        ui?.background = Background(BackgroundFill(Paint.valueOf(getColorByName(number)), null, null))
    }
}