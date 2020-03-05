package com.brok1n.kotlin.fx.game2048

import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Stage
import java.util.*
import kotlin.random.Random

class GameScene(val stage: Stage) {
    //游戏运行状态
    var isRunning = false

    //最大得分
    var maxScore = 0
    //当前得分
    var score = 0
    //合成的最大数
    var maxNumber = 0
    var isWin = false

    //当前是否在忙碌(处理按键事件) 如果在忙碌就忽略按键事件
    var inWorking = false

    //宽度高度
    val WIDTH = 300.0
    val HEIGHT = 500.0
    //游戏画布Y坐标
    var baseY = 100.0
    //各个方格中间的间隙
    var space = 4.0
    //布局
    lateinit var rootPane:Pane
    lateinit var rootScene: Scene
    lateinit var gamePane: Pane
    lateinit var scoreTxt: Text
    lateinit var maxScoreTxt: Text
    lateinit var newGameTxt: Text

    //数据打印定时器
    lateinit var timer: Timer

    //随机生成器
    val random = Random(System.currentTimeMillis())

    //数据列表
    var dataList = ArrayList<ArrayList<Block>>()
    //UI列表
    var uiList = ArrayList<ArrayList<Button>>()

    fun start() {
        isRunning = true
        initData()
        initUi()
        initEvent()
        initUpdate()
        refresh()
    }

    fun initUpdate() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {

            }
        }, 1000, 1000)
    }

    fun initEvent() {
        rootScene.setOnKeyPressed {
//            log.info("key:${it.code} - ${it.code.ordinal} - ${it.code.name}")
            if (it.code.ordinal in DIRECTION_KEYS && !inWorking) {
//                log.info("处理按键: ${it.code} - ${it.code.ordinal} ...")
                val moved = processDirectionKeys(it)
                refresh()
                if (moved) {
//                    log.info("有数据变化")
                    generateNumber()
                } else {
//                    log.info("没有数据变化")
                }
                if ( maxNumber == 2048 && !isWin ) {
                    isWin = true
                    val alert = Alert(Alert.AlertType.INFORMATION)
                    alert.setTitle("TIP")
                    alert.setHeaderText(null)
                    alert.setContentText("YOU WIN!")
                    alert.showAndWait()
                }
                val gameOver = isGameOver()
                if (gameOver) {
                    val alert = Alert(Alert.AlertType.WARNING)
                    alert.setTitle("TIP")
                    alert.setHeaderText(null)
                    alert.setContentText("GAME OVER")
                    alert.showAndWait()
                }
//                log.info("处理按键: ${it.code} - ${it.code.ordinal}  完毕!")
            }
        }
    }

    private fun isGameOver(): Boolean {
        for (i in 0..3) {
            for (n in 0..3) {
                val block = dataList[i][n]
                if (block.number < 2) {
                    return false
                }
                if (n + 1 <= 3) {
                    val right = dataList[i][n + 1]
                    if (block.number == right.number) {
                        return false
                    }
                }
                if (i + 1 <= 3) {
                    val bottom = dataList[i+1][n]
                    if (block.number == bottom.number) {
                        return false
                    }
                }
            }
        }
        return true
    }

    /**
     * 处理方向键
     * */
    private fun processDirectionKeys(key: KeyEvent):Boolean {
        when(key.code.ordinal) {
            16 -> return left()
            17 -> return up()
            18 -> return right()
            19 -> return bottom()
        }
        return false
    }

    /**
     * 按下了方向键 下
     * */
    private fun bottom(): Boolean {
//        log.info("按下了方向键 下")
        var moved = false
        for (i in 0..3) {
            val cols = arrayOf(dataList[0][i],dataList[1][i],dataList[2][i],dataList[3][i])
            var numBlock = cols.lastOrNull { it.number>1 }
            var firstEmptyBlock = dataList[3][i]
            if (numBlock != null) {
                while (numBlock!!.yInt >= 0) {
                    val nextBlock = cols.lastOrNull { numBlock != null && it.yInt < numBlock!!.yInt && it.number > 1 }
                    if (nextBlock == null) {
                        if (numBlock.yInt < 3 && numBlock.yInt < firstEmptyBlock.yInt) {
                            val num = numBlock.number
                            numBlock.number = 0
                            firstEmptyBlock.number = num
                            moved = true
                        }
                        break
                    }

                    if (numBlock.number == nextBlock.number) {
                        val num = numBlock.number * 2
                        score += num
                        nextBlock.number = 0
                        numBlock.number = 0
                        firstEmptyBlock.number = num
                        moved = true
                    } else if (firstEmptyBlock.yInt != numBlock.yInt) {
                        val num = numBlock.number
                        numBlock.number = 0
                        firstEmptyBlock.number = num
                        moved = true
                    }

                    numBlock = cols.lastOrNull { numBlock != null && it.number > 1 && it.yInt < numBlock!!.yInt }
                    if (numBlock == null) {
                        break
                    }
                    if (firstEmptyBlock.yInt - 1 >=  0) {
                        firstEmptyBlock = cols[firstEmptyBlock.yInt-1]
                    } else {
                        break
                    }
                }
            }
        }
        return moved
    }

    /**
     * 按下了方向键 右
     * */
    private fun right(): Boolean {
//        log.info("按下了方向键 右")
        var moved = false
        for (i in 0..3) {
            var numBlock = dataList[i].lastOrNull { it.number > 1 }
            var firstEmptyBlock = dataList[i][3]
            if (numBlock != null) {
                while (numBlock!!.xInt >= 0) {
                    val nextBlock = dataList[i].lastOrNull { numBlock != null && it.xInt < numBlock!!.xInt && it.number > 1 }
                    if (nextBlock == null) {
                        if (numBlock.xInt < 3 && numBlock.xInt < firstEmptyBlock.xInt) {
                            val num = numBlock.number
                            numBlock.number = 0
                            firstEmptyBlock.number = num
                            moved = true
                        }
                        break
                    }
                    if (numBlock.number == nextBlock.number) {
                        val num = numBlock.number * 2
                        score += num
                        nextBlock.number = 0
                        numBlock.number = 0
                        firstEmptyBlock.number = num
                        moved = true
                    } else if (firstEmptyBlock.xInt != numBlock.xInt) {
                        val num = numBlock.number
                        numBlock.number = 0
                        firstEmptyBlock.number = num
                        moved = true
                    }
                    numBlock = dataList[i].lastOrNull { numBlock != null && it.number > 1 && it.xInt < numBlock!!.xInt }
                    if (numBlock == null) {
                        break
                    }
                    if (firstEmptyBlock.xInt - 1 >=  0) {
                        firstEmptyBlock = dataList[i][firstEmptyBlock.xInt-1]
                    } else {
                        break
                    }
                }
            }
        }
        return moved
    }

    /**
     * 按下了方向键 上
     * */
    private fun up(): Boolean {
//        log.info("按下了方向键 上")
        var moved = false
        for (i in 0..3) {
            val cols = arrayOf(dataList[0][i],dataList[1][i],dataList[2][i],dataList[3][i])
            var numBlock = cols.firstOrNull { it.number>1 }
            var firstEmptyBlock = dataList[0][i]
            if (numBlock != null) {
                while (numBlock!!.yInt <= 3) {
                    val nextBlock = cols.firstOrNull { numBlock != null && it.yInt > numBlock!!.yInt && it.number > 1 }
                    if (nextBlock == null) {
                        if (numBlock.yInt > 0 && numBlock.yInt > firstEmptyBlock.yInt) {
                            val num = numBlock.number
                            numBlock.number = 0
                            firstEmptyBlock.number = num
                            moved = true
                        }
                        break
                    }

                    if (numBlock.number == nextBlock.number) {
                        val num = numBlock.number * 2
                        score += num
                        nextBlock.number = 0
                        numBlock.number = 0
                        firstEmptyBlock.number = num
                        moved = true
                    } else if (firstEmptyBlock.yInt != numBlock.yInt) {
                        val num = numBlock.number
                        numBlock.number = 0
                        firstEmptyBlock.number = num
                        moved = true
                    }

                    numBlock = cols.firstOrNull { numBlock != null && it.number > 1 && it.yInt > numBlock!!.yInt }
                    if (numBlock == null) {
                        break
                    }
                    if (firstEmptyBlock.yInt + 1 <=  3) {
                        firstEmptyBlock = cols[firstEmptyBlock.yInt+1]
                    } else {
                        break
                    }
                }
            }
        }
        return moved
    }

    /**
     * 按下了方向键 左
     * */
    private fun left(): Boolean {
//        log.info("按下了方向键 左")
        var moved = false
        for (i in 0..3) {
            var numBlock = dataList[i].firstOrNull { it.number > 1 }
            var firstEmptyBlock = dataList[i][0]
            if (numBlock != null) {
                while (numBlock!!.xInt <= 3) {
                    val nextBlock = dataList[i].firstOrNull { numBlock != null && it.xInt > numBlock!!.xInt && it.number > 1 }
                    if (nextBlock == null) {
                        //如果下一个待合并的元素为空 并且上一个不为空的元素不是第一个
                        //就合并，如果上一个元素是第一个 则就不用和第一个元素合并了
                        if (numBlock.xInt > 0 && numBlock.xInt > firstEmptyBlock.xInt) {
                            val num = numBlock.number
                            numBlock.number = 0
                            firstEmptyBlock.number = num
                            moved = true
                        }
                        break
                    }
                    if (numBlock.number == nextBlock.number) {
                        val num = numBlock.number * 2
                        score += num
                        nextBlock.number = 0
                        numBlock.number = 0
                        firstEmptyBlock.number = num
                        moved = true
                    } else if (firstEmptyBlock.xInt != numBlock.xInt) {
                        val num = numBlock.number
                        numBlock.number = 0
                        firstEmptyBlock.number = num
                        moved = true
                    }
                    numBlock = dataList[i].firstOrNull { numBlock != null && it.number > 1 && it.xInt > numBlock!!.xInt }
                    if (numBlock == null) {
                        break
                    }
                    if (firstEmptyBlock.xInt + 1 <=  3) {
                        firstEmptyBlock = dataList[i][firstEmptyBlock.xInt+1]
                    } else {
                        break
                    }
                }
            }
        }
        return moved
    }

    /**
     * 刷新页面游戏元素
     * */
    fun refresh() {
        dataList.forEach {
            it.forEach {
                it.update()
                if (maxNumber < it.number) {
                    maxNumber = it.number
                }
            }
        }
        updateScore()
    }

    /**
     * 更新分数
     * */
    fun updateScore() {
        if (score > maxScore) {
            maxScore = score
        }
        scoreTxt.text = "SCORE:${score}"
        maxScoreTxt.text = "MAX SCORE:${maxScore}"
    }


    /**
     * 初始化游戏逻辑数据
     * */
    fun initData() {
        dataList.clear()
        score = 0
        for ( i in 0..3) {
//            log.info("line:$i")
            val row = ArrayList<Block>()
            for ( n in 0 .. 3) {
                val col = Block()
                col.xInt = n
                col.yInt = i
                col.x = n * 70.0 + (n+1) * space
                col.y = baseY + i * 70.0 + (i+1) * space
                row.add(col)
            }
            dataList.add(row)
        }
    }

    /**
     * 初始化界面
     * */
    fun initUi() {
        rootPane = Pane()
        rootScene = Scene(rootPane,WIDTH, HEIGHT)

        //分数文本
        scoreTxt = Text(5.0, 40.0, "SCORE:${score}")
        scoreTxt.font = Font.font(26.0)
        scoreTxt.fill = Paint.valueOf(COLOR_BLACK_TXT)
        rootPane.children.add(scoreTxt)

        //分数文本
        maxScoreTxt = Text(5.0, 80.0, "MAX SCORE:${maxScore}")
        maxScoreTxt.font = Font.font(14.0)
        maxScoreTxt.fill = Paint.valueOf(COLOR_BLACK_TXT)
        rootPane.children.add(maxScoreTxt)

        //新游戏按钮
        newGameTxt = Text(170.0, 80.0, "NEW GAME")
        newGameTxt.font = Font.font(20.0)
        newGameTxt.fill = Paint.valueOf(COLOR_BLACK_TXT)
        rootPane.children.add(newGameTxt)
        newGameTxt.setOnMouseClicked {
//            log.info("新游戏被点击.....................")
            newGame()
        }

        //游戏画布
        gamePane = Pane()
        gamePane.maxWidth = WIDTH
        gamePane.minWidth = WIDTH
        gamePane.prefWidth = WIDTH
        gamePane.maxHeight = WIDTH
        gamePane.minHeight = WIDTH
        gamePane.prefHeight = WIDTH
        gamePane.layoutX = 0.0
        gamePane.layoutY = baseY
        gamePane.background = Background(BackgroundFill(Paint.valueOf(COLOR_CENTER_BG), null, null))
        rootPane.children.add(gamePane)


        //绘制方格
        dataList.forEach {
            val row = ArrayList<Button>()
            it.forEach{
                val btn = Button("")
                btn.padding = Insets.EMPTY
                btn.maxHeight = it.w
                btn.maxWidth = it.w
                btn.minWidth = it.w
                btn.minHeight = it.w
                btn.layoutX = it.x
                btn.layoutY = it.y
                it.ui = btn
                it.update()
                rootPane.children.add(btn)
                row.add(btn)
            }
            uiList.add(row)
        }

        //生成默认的两个方块
        generateNumber()
        generateNumber()

        stage.maxWidth = WIDTH+6
        stage.scene = rootScene
        stage.show()

        stage.setOnCloseRequest {
            stop()
        }
//        log.info("界面显示...............")
    }

    /**
     * 生成随机数字块
     * 如果当前分数小于4 就只能生成2，否则就可能会出现2和4
     * 生成成功，返回true
     * 生成失败，返回false 表示没有空白位置了 游戏结束
     * */
    fun generateNumber():Boolean {
        if (score < 4) {
            return randomBlock(2)
        } else {
            return randomNumBlock()
        }
    }

    /**
     * 生成2或者4的随机块
     * */
    fun randomNumBlock():Boolean {
        if (random.nextInt(100) == 50) {
            return randomBlock(4)
        } else {
            return randomBlock(2)
        }
    }

    /**
     * 获取一个空白的随机方块填入数字并更新元素
     * 如果添加随机成功，就返回true 添加失败 代表每空格了就游戏结束
     * */
    fun randomBlock(num:Int):Boolean {
        val list = getEmptyBlockList()
        if (list.size > 0) {
            val block = list.random(random)
            block.number = num
            block.update()
            return true
        } else {
            return false
        }
    }

    /**
     * 获取所有空白方块
     * */
    fun getEmptyBlockList():List<Block> {
        val list = ArrayList<Block>()
        dataList.forEach {
            it.forEach {
                if (it.number < 2) {
                    list.add(it)
                }
            }
        }
        return list
    }


    /**
     * 新游戏
     * */
    fun newGame() {
        dataList.forEach {
            it.forEach {
                it.number = 0
            }
        }
        score = 0
        maxNumber = 0
        isWin = false
        generateNumber()
        generateNumber()
        refresh()
    }

    /**
     * 停止游戏
     * */
    fun stop() {
//        log.info("游戏结束>......................")
        timer.cancel()
        isRunning = false
    }
}