package com.brok1n.kotlin.fx.game2048

import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
//import org.apache.logging.log4j.LogManager




fun main(args: Array<String>) = Application.launch(Launcher::class.java, *args)

class Launcher : Application() {
    lateinit var gameScene: GameScene
    override fun start(primaryStage: Stage) {
//        log.info("application launcher start .....")
        primaryStage.getIcons().add(Image("/2048_96.jpg"));
//        primaryStage.getIcons().add(Image(Launcher::class.java.getResourceAsStream("/2048_96.jpg")));
        primaryStage.isResizable = false
        primaryStage.title = "2048 by brok1n"

        gameScene = GameScene(primaryStage)
        gameScene.start()
    }
}

//方向键 左 上 右 下
val DIRECTION_KEYS = arrayOf(16,17,18,19)

//val log = LogManager.getLogger("game2048")

val COLOR_CENTER_BG = "BBADA0"
val COLOR_BLACK_TXT = "776E65"
val COLOR_WHITE_TXT = "F9F6F2"

val COLOR_MAP = HashMap<Int, String>().apply {
    put(0, "#CDC1B4")
    put(2, "#EEE4DA")
    put(4, "#EDE0C8")
    put(8, "#F2B179")
    put(16, "#F59563")
    put(32, "#F67C5F")
    put(64, "#F65E3B")
    put(128, "#EDCF72")
    put(256, "#EDCC61")
    put(512, "#EDC850")
    put(1024, "#EDC53F")
    put(2048, "#EDC22E")
    put(4096, "#EDC22E")
    put(8192, "#EDC22E")
}

fun getColorByName(num:Int):String {
    if (COLOR_MAP.containsKey(num)) {
        return COLOR_MAP[num]!!
    }
    return COLOR_MAP[0]!!
}

fun sleep(time:Long) {
    Thread.sleep(time)
}